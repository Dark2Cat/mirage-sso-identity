package com.mirage.sso.role;

import java.time.LocalDateTime;
import java.util.List;

import com.mirage.sso.permission.PermissionEntity;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserRoleMapper {
    @Select("""
            select r.*
            from sso_role r
            join sso_user_role ur on ur.role_id = r.id
            where ur.user_id = #{userId}
            order by r.code
            """)
    List<RoleEntity> selectRolesByUserId(@Param("userId") Long userId);

    @Select("""
            select p.*
            from sso_permission p
            join sso_role_permission rp on rp.permission_id = p.id
            join sso_user_role ur on ur.role_id = rp.role_id
            where ur.user_id = #{userId} and p.status = 'ACTIVE'
            order by p.sort_order, p.code
            """)
    List<PermissionEntity> selectPermissionsByUserId(@Param("userId") Long userId);

    @Delete("delete from sso_user_role where user_id = #{userId}")
    void deleteByUserId(@Param("userId") Long userId);

    @Insert("insert into sso_user_role (user_id, role_id, created_at) values (#{userId}, #{roleId}, #{createdAt})")
    void insertUserRole(@Param("userId") Long userId, @Param("roleId") Long roleId, @Param("createdAt") LocalDateTime createdAt);
}
