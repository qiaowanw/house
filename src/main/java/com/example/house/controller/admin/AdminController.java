package com.example.house.controller.admin;

import com.example.house.base.ApiResponse;
import com.example.house.base.ServiceMultiResult;
import com.example.house.base.ServiceResult;
import com.example.house.domain.SupportAddress;
import com.example.house.dto.*;
import com.example.house.form.HouseForm;
import com.example.house.service.house.IAddressService;
import com.example.house.service.house.IHouseService;
import com.example.house.service.house.IQiNiuService;
import com.google.gson.Gson;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

@Controller
public class AdminController {
    @Autowired
    private IQiNiuService qiNiuService;
    @Autowired
    private Gson gson;
    @Autowired
    private IAddressService addressService;
    @Autowired
    private IHouseService houseService;

//    查全部city
    @GetMapping("address/support/cities")
    @ResponseBody
    public ApiResponse getSupportCities(){
        ServiceMultiResult<SupportAddressDTO> result = addressService.findAllCities();
        if(result.getResultSize()==0){
            return ApiResponse.ofStatus(ApiResponse.Status.NOT_FOUND);
        }
        return ApiResponse.ofSuccess(result.getResult());
    }
///address/support/regions
    @GetMapping("address/support/regions")
    @ResponseBody
    public ApiResponse getSupportRegions(@RequestParam(name = "city_name") String cityEnName){
        ServiceMultiResult result = addressService.findAllRegionsByCityName(cityEnName);
        if(result.getResult()==null || result.getTotal() < 1){
            return ApiResponse.ofStatus(ApiResponse.Status.NOT_FOUND);
        }
        return ApiResponse.ofSuccess(result.getResult());
    }

    @GetMapping("address/support/subway/line")
    @ResponseBody
    public ApiResponse getSupportSubways(@RequestParam(name="city_name") String cityEnName){
        List<SubwayDTO> result = addressService.findAllSubwayByCity(cityEnName);
        if(result.isEmpty())
            return ApiResponse.ofStatus(ApiResponse.Status.NOT_FOUND);
        return ApiResponse.ofSuccess(result);
    }

    @GetMapping("address/support/subway/station")
    @ResponseBody
    public ApiResponse getSupportStatioins(@RequestParam("subway_id") Long subwayId){
        List<SubwayStationDTO> result = addressService.findAllStationBySubway(subwayId);
        if(result.isEmpty())
            return ApiResponse.ofStatus(ApiResponse.Status.NOT_FOUND);
        return ApiResponse.ofSuccess(result);
    }

    @RequestMapping("/admin/login")
    public String adminLoginPage() {
        return "admin/login";
    }

    @GetMapping("admin/add/house")
    public String addHousePage() {
        return "admin/house-add";
    }

    @PostMapping(value = "admin/upload/photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseBody
    public ApiResponse uploadPhoto(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ApiResponse.ofStatus(ApiResponse.Status.NOT_VALID_PARAM);
        }

        String fileName = file.getOriginalFilename();

        try {
            InputStream inputStream = file.getInputStream();
            Response response = qiNiuService.uploadFile(inputStream);
            if (response.isOK()) {
                QiNiuPutRet ret = gson.fromJson(response.bodyString(), QiNiuPutRet.class);
                return ApiResponse.ofSuccess(ret);
            } else {
                return ApiResponse.ofMessage(response.statusCode, response.getInfo());
            }

        } catch (QiniuException e) {
            Response response = e.response;
            try {
                return ApiResponse.ofMessage(response.statusCode, response.bodyString());
            } catch (QiniuException e1) {
                e1.printStackTrace();
                return ApiResponse.ofStatus(ApiResponse.Status.INTERNAL_SERVER_ERROR);
            }
        } catch (IOException e) {
            return ApiResponse.ofStatus(ApiResponse.Status.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("admin/add/house")
    @ResponseBody
    public ApiResponse addHouse(@Valid @ModelAttribute("form-house-add") HouseForm houseForm, BindingResult bindingResult){
        System.out.println(houseForm);
        if(bindingResult.hasErrors()){
            return new ApiResponse(HttpStatus.BAD_REQUEST.value(),bindingResult.getAllErrors().get(0).getDefaultMessage(),null);
        }
        if(houseForm.getPhotos()==null || houseForm.getCover()==null){
            return ApiResponse.ofMessage(HttpStatus.BAD_REQUEST.value(), "必须上传图片");
        }
        Map<String, SupportAddressDTO> addressMap = addressService.findCityAndRegion(houseForm.getCityEnName(), houseForm.getRegionEnName());
        System.out.println(addressMap.keySet().size());
        if(addressMap.keySet().size()!=2){
            return ApiResponse.ofStatus(ApiResponse.Status.NOT_VALID_PARAM);
        }

        ServiceResult<HouseDTO> result = houseService.save(houseForm);
        if(result.isSuccess()){
            return ApiResponse.ofSuccess(result.getResult());
        }
        return ApiResponse.ofSuccess(ApiResponse.Status.NOT_VALID_PARAM);
    }
}

