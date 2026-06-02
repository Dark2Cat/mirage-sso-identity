package com.mirage.sso.role;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class RoleServiceTest {

    @Resource
    private RoleService roleService;

    @Test
    void testRolesByUserId() {
        List<RoleEntity> roles = roleService.rolesByUserId(1001L);
        assertEquals(2, roles.size());
    }
}