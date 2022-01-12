package com.example.house.mapper;

import com.example.house.domain.Role;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class RoleMapperTest {
    @Autowired
    private RoleMapper roleMapper;

    @Test
    void findRolesByUserId() {
        System.out.println(roleMapper.findRolesByUserId(2L));
    }

    @Test
    void save(){
        Role role = new Role();
        role.setUserId(5L);
        role.setName("USER");
        roleMapper.save(role);
    }
}