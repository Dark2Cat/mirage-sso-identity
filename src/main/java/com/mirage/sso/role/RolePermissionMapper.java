package com.mirage.sso.role;

import java.time.LocalDateTime;
import java.util.List;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface RolePermissionMapper {
    @Select("""
            select p.*
            from sso_permission p
            join sso_role_permission rp on rp.permission_id = p.id
            where rp.role_id = #{roleId}
            order by p.sort_order, p.code
            """)
    List<PermissionEntity> selectPermissionsByRoleId(@Param("roleId") Long roleId);

    @Select("select count(*) from sso_user_role where role_id = #{roleId}")
    long countUsersByRoleId(@Param("roleId") Long roleId);

    @Delete("delete from sso_role_permission where role_id = #{roleId}")
    void deleteByRoleId(@Param("roleId") Long roleId);

    @Insert("insert into sso_role_permission (role_id, permission_id, created_at) values (#{roleId}, #{permissionId}, #{createdAt})")
    void insertRolePermission(@Param("roleId") Long roleId, @Param("permissionId") Long permissionId, @Param("createdAt") LocalDateTime createdAt);
}
