package com.mirage.sso;

import com.mirage.sso.audit.AuditLogMapper;
import com.mirage.sso.client.AppAccessMapper;
import com.mirage.sso.client.ClientMapper;
import com.mirage.sso.file.SsoMediaFileMapper;
import com.mirage.sso.organization.OrganizationMapper;
import com.mirage.sso.portal.AppMapper;
import com.mirage.sso.permission.PermissionMapper;
import com.mirage.sso.role.RolePermissionMapper;
import com.mirage.sso.role.RoleMapper;
import com.mirage.sso.role.UserRoleMapper;
import com.mirage.sso.user.UserMapper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest
@EnableAutoConfiguration(exclude = {
        DataSourceAutoConfiguration.class,
        FlywayAutoConfiguration.class
})
class MirageSsoIdentityApplicationTests {
    @MockBean
    private UserMapper userMapper;

    @MockBean
    private RoleMapper roleMapper;

    @MockBean
    private PermissionMapper permissionMapper;

    @MockBean
    private RolePermissionMapper rolePermissionMapper;

    @MockBean
    private UserRoleMapper userRoleMapper;

    @MockBean
    private OrganizationMapper organizationMapper;

    @MockBean
    private AppAccessMapper appAccessMapper;

    @MockBean
    private ClientMapper clientMapper;

    @MockBean
    private AppMapper appMapper;

    @MockBean
    private AuditLogMapper auditLogMapper;

    @MockBean
    private SsoMediaFileMapper ssoMediaFileMapper;

    @Test
    void contextLoads() {
    }
}
