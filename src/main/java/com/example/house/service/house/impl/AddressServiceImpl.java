package com.example.house.service.house.impl;

import com.example.house.base.ServiceMultiResult;
import com.example.house.base.ServiceResult;
import com.example.house.domain.Subway;
import com.example.house.domain.SubwayStation;
import com.example.house.domain.SupportAddress;
import com.example.house.dto.SubwayDTO;
import com.example.house.dto.SubwayStationDTO;
import com.example.house.dto.SupportAddressDTO;
import com.example.house.mapper.SubwayMapper;
import com.example.house.mapper.SubwayStationMapper;
import com.example.house.mapper.SupportAddressMapper;
import com.example.house.service.house.IAddressService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AddressServiceImpl implements IAddressService {
    @Autowired
    private SupportAddressMapper supportAddressMapper;
    @Autowired
    private SubwayStationMapper subwayStationMapper;
    @Autowired
    private SubwayMapper subwayMapper;
    @Autowired
    private ModelMapper modelMapper;

    @Override
    public Map<SupportAddress.Level, SupportAddressDTO> findCityAndRegion(String cityEnName, String regionEnName) {
        SupportAddress address = supportAddressMapper.findByEnNameAndBelongTo(regionEnName, cityEnName);
        Map<SupportAddress.Level, SupportAddressDTO> addressMap = new HashMap<>();
        SupportAddressDTO addressDTO = modelMapper.map(address,SupportAddressDTO.class);
        addressMap.put(SupportAddress.Level.CITY, addressDTO);
        addressMap.put(SupportAddress.Level.REGION, addressDTO);
        return addressMap;
    }

    @Override
    public ServiceMultiResult<SupportAddressDTO> findAllCities() {
        List<SupportAddress> addresses =
                supportAddressMapper.findAllByLevel(SupportAddress.Level.CITY.getValue());
        List<SupportAddressDTO> addressDTOS = new ArrayList<>();
        for(SupportAddress supportAddress: addresses){
            SupportAddressDTO target = modelMapper.map(supportAddress, SupportAddressDTO.class);
            addressDTOS.add(target);
        }
        return new ServiceMultiResult<>(addressDTOS.size(),addressDTOS);
    }

    @Override
    public ServiceMultiResult findAllRegionsByCityName(String cityEnName) {
        if(cityEnName == null)
            return new ServiceMultiResult<>(0,null);

        List<SupportAddress> regions = supportAddressMapper.findAllByLevelAndBelongTo(SupportAddress.Level.REGION.getValue(), cityEnName);
        List<SupportAddressDTO> result = new ArrayList<>();
        for(SupportAddress region : regions){
            SupportAddressDTO addressDTO = modelMapper.map(region, SupportAddressDTO.class);
            result.add(addressDTO);
        }
        return new ServiceMultiResult(regions.size(),result);
    }

    @Override
    public List<SubwayDTO> findAllSubwayByCity(String cityEnName) {
        if(cityEnName == null)
            return new ArrayList<>(null);
        List<Subway> subways = subwayMapper.findAllByCityEnName(cityEnName);
        List<SubwayDTO> result = new ArrayList<>();
        for(Subway subway : subways){
            SubwayDTO subwayDTO = modelMapper.map(subway, SubwayDTO.class);
            result.add(subwayDTO);
        }
        return result;
    }

    @Override
    public List<SubwayStationDTO> findAllStationBySubway(Long subwayId) {
        List<SubwayStationDTO> result = new ArrayList<>();
        List<SubwayStation> stations = subwayStationMapper.findAllBySubwayId(subwayId);
        if(stations.isEmpty())
            return result;
        for(SubwayStation station : stations){
            SubwayStationDTO subwayStationDTO = modelMapper.map(station, SubwayStationDTO.class);
            result.add(subwayStationDTO);
        }
        return result;
    }

    @Override
    public ServiceResult<SupportAddressDTO> findCity(String cityEnName) {
        if (cityEnName == null) {
            return ServiceResult.notFound();
        }

        SupportAddress supportAddress = supportAddressMapper.findByEnNameAndLevel(cityEnName, SupportAddress.Level.CITY.getValue());
        if (supportAddress == null) {
            return ServiceResult.notFound();
        }

        SupportAddressDTO addressDTO = modelMapper.map(supportAddress, SupportAddressDTO.class);
        return ServiceResult.of(addressDTO);
    }
}
