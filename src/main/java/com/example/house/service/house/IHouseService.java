package com.example.house.service.house;

import com.example.house.base.ServiceResult;
import com.example.house.dto.HouseDTO;
import com.example.house.form.HouseForm;

public interface IHouseService {
    ServiceResult<HouseDTO> save(HouseForm houseForm);
}
