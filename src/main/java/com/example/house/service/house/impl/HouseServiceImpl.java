package com.example.house.service.house.impl;

import com.example.house.base.HouseStatus;
import com.example.house.base.ServiceMultiResult;
import com.example.house.base.ServiceResult;
import com.example.house.domain.*;
import com.example.house.dto.HouseDTO;
import com.example.house.dto.HouseDetailDTO;
import com.example.house.dto.HousePictureDTO;
import com.example.house.dto.QiNiuPutRet;
import com.example.house.form.DatatableSearch;
import com.example.house.form.HouseForm;
import com.example.house.form.PhotoForm;
import com.example.house.form.RentSearch;
import com.example.house.mapper.*;
import com.example.house.service.house.IHouseService;
import com.example.house.service.house.IQiNiuService;
import com.example.house.util.LoginUserUtil;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.BucketManager;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class HouseServiceImpl implements IHouseService {
    @Autowired
    private IQiNiuService qiNiuService;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private HouseMapper houseMapper;
    @Autowired
    private HouseDetailMapper houseDetailMapper;
    @Autowired
    private HousePictureMapper housePictureMapper;
    @Autowired
    private HouseTagMapper houseTagMapper;
    @Autowired
    private SubwayMapper subwayMapper;
    @Autowired
    private SubwayStationMapper subwayStationMapper;
    @Value("r5mj0bzjl.hb-bkt.clouddn.com")
    private String cdnPrefix;


    @Override
    public ServiceResult<HouseDTO> save(HouseForm houseForm) {
        System.out.println("house form:" + houseForm);
        HouseDetail detail = new HouseDetail();
        ServiceResult<HouseDTO> subwayValidationResult = wrapperDetailInfo(detail,houseForm);
        if(subwayValidationResult != null){
            return subwayValidationResult;
        }

        House house = new House();
        modelMapper.map(houseForm,house);

        LocalDateTime now = LocalDateTime.now();
        house.setCreateTime(now);
        house.setLastUpdateTime(now);
        house.setStatus(0);
        house.setAdminId(LoginUserUtil.getLoginUserId());
        //house.setAdminId(3L); //测试用
        houseMapper.save(house);

        detail.setHouseId(house.getId());
        houseDetailMapper.save(detail);

        List<HousePicture> pictures = generatePictures(houseForm,house.getId());
        housePictureMapper.save(pictures);

        HouseDTO houseDTO = modelMapper.map(house,HouseDTO.class);
        HouseDetailDTO houseDetailDTO = modelMapper.map(detail, HouseDetailDTO.class);

        houseDTO.setHouseDetail((houseDetailDTO));

        List<HousePictureDTO> pictureDTOS = new ArrayList<>();
        pictures.forEach(housePicture -> pictureDTOS.add(modelMapper.map(housePicture,HousePictureDTO.class)));
        houseDTO.setPictures(pictureDTOS);
        houseDTO.setCover(this.cdnPrefix+houseDTO.getCover());

        List<String> tags = houseForm.getTags();
        if(tags!=null && !tags.isEmpty()){
            List<HouseTag> houseTags = new ArrayList<>();
            for(String tag:tags){
                houseTags.add(new HouseTag(house.getId(),tag));
            }
            houseTagMapper.save(houseTags);
            houseDTO.setTags(tags);
        }
        return ServiceResult.success(null,houseDTO);
    }

    @Override
    public ServiceResult update(HouseForm houseForm) {
        House house = this.houseMapper.findOne(houseForm.getId());
        if(house == null) {
            return ServiceResult.notFound();
        }

        HouseDetail detail = houseDetailMapper.findByHouseId(house.getId());
        if(detail==null){return ServiceResult.notFound();}

        ServiceResult<HouseDTO> wrapperResult = wrapperDetailInfo(detail, houseForm);
        if(wrapperResult!=null){
            return wrapperResult;
        }
        houseDetailMapper.save(detail);

        List<HousePicture> pictures = generatePictures(houseForm, houseForm.getId());
        housePictureMapper.save(pictures);

        if(houseForm.getCover()==null){
            houseForm.setCover(house.getCover());
        }

        modelMapper.map(houseForm, house);
        house.setLastUpdateTime(LocalDateTime.now());
        houseMapper.save(house);
        return ServiceResult.success();
    }

    @Override
    public ServiceResult<HouseDTO> findCompleteOne(Long id) {
        House house = houseMapper.findOne(id);
        if(house==null){
            return ServiceResult.notFound();
        }
        HouseDetail detail = houseDetailMapper.findByHouseId(id);
        List<HousePicture> pictures = housePictureMapper.findAllByHouseId(id);

        HouseDetailDTO detailDTO = modelMapper.map(detail, HouseDetailDTO.class);
        List<HousePictureDTO> pictureDTOS=new ArrayList<>();
        for(HousePicture picture : pictures){
            HousePictureDTO pictureDTO = modelMapper.map(picture,HousePictureDTO.class);
            pictureDTOS.add(pictureDTO);
        }

        List<HouseTag> tags = houseTagMapper.findAllByHouseId(id);
        List<String> tagList = new ArrayList<>();
        for(HouseTag tag : tags){
            tagList.add(tag.getName());
        }

        HouseDTO result = modelMapper.map(house, HouseDTO.class);
        result.setHouseDetail(detailDTO);
        result.setPictures(pictureDTOS);
        result.setTags(tagList);
        return ServiceResult.of(result);
    }

    @Override
    public ServiceResult addTag(Long houseId, String tag) {
        House house = houseMapper.findOne(houseId);
        if(house == null){
            return ServiceResult.notFound();
        }

        HouseTag houseTag = houseTagMapper.findByNameAndHouseId(tag, houseId);
        if(houseTag!=null){
            return new ServiceResult(false,"标签已存在");
        }

        houseTagMapper.save(List.of(new HouseTag(houseId,tag)));
        return ServiceResult.success();
    }

    @Override
    public ServiceResult removeTag(Long houseId, String tag) {
        House house = houseMapper.findOne(houseId);
        if(house == null){
            return ServiceResult.notFound();
        }

        HouseTag houseTag = houseTagMapper.findByNameAndHouseId(tag, houseId);
        if(houseTag==null){
            return new ServiceResult(false,"标签不存在");
        }
        houseTagMapper.delete(houseTag.getId());
        return ServiceResult.success();
    }

    @Override
    public ServiceResult updateStatus(Long id, int status) {
        House house = houseMapper.findOne(id);
        if(house == null){
            return ServiceResult.notFound();
        }

        if(house.getStatus()== status){
            return new ServiceResult(false,"状态没有发生变化");
        }

        if(house.getStatus()== HouseStatus.RENTED.getValue()){
            return new ServiceResult(false,"已出租的房源不允许修改状态");
        }

        if(house.getStatus() == HouseStatus.DELETED.getValue()){
            return new ServiceResult(false, "已删除的资源不允许操作");
        }

        houseMapper.updateStatus(id,status);
        return ServiceResult.success();
    }

    @Override
    public ServiceResult removePhoto(Long id) {
        HousePicture picture = housePictureMapper.findOne(id);
        if(picture == null){
            return ServiceResult.notFound();
        }

        try{
            Response response = this.qiNiuService.delete(picture.getPath());
            if(response.isOK()){
                housePictureMapper.delete(id);
                return ServiceResult.success();
            } else{
                return new ServiceResult(false, response.error);
            }
        } catch (QiniuException e) {
            e.printStackTrace();
            return new ServiceResult(false, e.getMessage());
        }
    }

    @Override
    public ServiceResult updateCover(Long coverId, Long targetId) {
        HousePicture cover = housePictureMapper.findOne(coverId);
        if(cover == null){
            return ServiceResult.notFound();
        }

        houseMapper.updateCover(targetId, cover.getPath());
        return ServiceResult.success();
    }

    @Override
    public ServiceMultiResult<HouseDTO> adminQuery(DatatableSearch searchBody) {
        List<HouseDTO> houseDTOS = new ArrayList<>();
        List<House> houses = houseMapper.findByDatatableSearch(searchBody);
        houses.forEach(house -> {
            HouseDTO houseDTO = modelMapper.map(house, HouseDTO.class);
            houseDTO.setCover(this.cdnPrefix + "/" + house.getCover());
            houseDTOS.add(houseDTO);
        });
        return new ServiceMultiResult<>(houses.size(),houseDTOS);
    }

    @Override
    public ServiceMultiResult<HouseDTO> query(RentSearch rentSearch) {
        List<HouseDTO> houseDTOS = new ArrayList<>();
//        Sort sort = Sort.by(Sort.Direction.fromString(searchBody.getDirection()), searchBody.getOrderBy());
//        int page = searchBody.getStart() / searchBody.getLength();
//        Pageable pageable = PageRequest.of(page, searchBody.getLength(), sort);
        List<House> houses = houseMapper.findAll();

        houses.forEach(house -> {
            HouseDTO houseDTO = modelMapper.map(house, HouseDTO.class);
            houseDTO.setCover(this.cdnPrefix + "/"+house.getCover());
            HouseDetailDTO houseDetailDTO = modelMapper.map(houseDetailMapper.findByHouseId(house.getId()), HouseDetailDTO.class);
            houseDTO.setHouseDetail(houseDetailDTO);
            houseDTOS.add(houseDTO);
        });

        return new ServiceMultiResult<>(houses.size(), houseDTOS);
    }

    private List<HousePicture> generatePictures(HouseForm houseForm, Long houseId){
        List<PhotoForm> photos = houseForm.getPhotos();
        List<HousePicture> housePictures = new ArrayList<>();
        for(PhotoForm photo : photos){
            HousePicture housePicture = new HousePicture();
            housePicture.setHouseId(houseId);
            housePicture.setCdnPrefix(cdnPrefix);
            housePicture.setLocation("celinelyle");
            housePicture.setWidth(photo.getWidth());
            housePicture.setHeight(photo.getHeight());
            housePicture.setPath(photo.getPath());
            housePictures.add(housePicture);
        }

        return housePictures;
    }

    private ServiceResult<HouseDTO> wrapperDetailInfo(HouseDetail houseDetail, HouseForm houseForm){
        Subway subway = subwayMapper.findOne(houseForm.getSubwayLineId());
        if(subway==null){
            return ServiceResult.of(false,"Not valid subway line!");
        }

        SubwayStation subwayStation = subwayStationMapper.findOne(houseForm.getSubwayStationId());
        if(subwayStation==null || subway.getId()!= subwayStation.getSubwayId()){
            return ServiceResult.of(false,"Not valid subway station!");
        }

        houseDetail.setSubwayLineId(subway.getId());
        houseDetail.setSubwayLineName(subway.getName());
        houseDetail.setSubwayStationId(subwayStation.getSubwayId());
        houseDetail.setSubwayStationName(subwayStation.getName());
        houseDetail.setDescription(houseForm.getDescription());
        houseDetail.setAddress(houseForm.getDetailAddress());
        houseDetail.setRentWay(houseForm.getRentWay());
        houseDetail.setRoundService(houseForm.getRoundService());
        houseDetail.setTraffic(houseForm.getTraffic());
        return null;
    }
}
