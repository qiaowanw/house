package com.example.house.service.house.impl;

import com.example.house.base.ServiceResult;
import com.example.house.domain.*;
import com.example.house.dto.HouseDTO;
import com.example.house.dto.HouseDetailDTO;
import com.example.house.dto.HousePictureDTO;
import com.example.house.dto.QiNiuPutRet;
import com.example.house.form.HouseForm;
import com.example.house.form.PhotoForm;
import com.example.house.mapper.*;
import com.example.house.service.house.IHouseService;
import com.example.house.service.house.IQiNiuService;
import com.example.house.util.LoginUserUtil;
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
        //house.setAdminId(3L);
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
