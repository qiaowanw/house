package com.example.house.service.search;

import com.example.house.base.ServiceMultiResult;
import com.example.house.dto.HouseBucketDTO;
import com.example.house.form.RentSearch;


public interface ISearchService {
    void index(Long houseId); //索引目标房源，将mysql中房源数据索引到es的里面

    void remove(Long houseId);

    ServiceMultiResult<Long> query(RentSearch rentSearch);

    ServiceMultiResult<HouseBucketDTO> mapAggregate(String cityEnName);
}
