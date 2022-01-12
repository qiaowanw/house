package com.example.house.mapper;

import com.example.house.domain.Role;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RoleMapper {
    int save(Role role);
    List<Role> findRolesByUserId(@Param("userId") Long userId);
}