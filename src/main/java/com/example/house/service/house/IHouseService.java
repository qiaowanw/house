package com.example.house.service.house;

import com.example.house.base.ServiceMultiResult;
import com.example.house.base.ServiceResult;
import com.example.house.dto.HouseDTO;
import com.example.house.form.DatatableSearch;
import com.example.house.form.HouseForm;
import com.example.house.form.RentSearch;

public interface IHouseService {
    ServiceResult<HouseDTO> save(HouseForm houseForm);

    ServiceResult update(HouseForm houseForm);

    ServiceResult<HouseDTO> findCompleteOne(Long id);

    ServiceResult addTag(Long houseId, String tag);

    ServiceResult removeTag(Long houseId, String tag);

    ServiceResult updateStatus(Long id, int status);

    ServiceResult removePhoto(Long id);

    ServiceResult updateCover(Long coverId, Long targetId);

    ServiceMultiResult<HouseDTO> adminQuery(DatatableSearch searchBody);

    ServiceMultiResult<HouseDTO> query(RentSearch rentSearch);

}
