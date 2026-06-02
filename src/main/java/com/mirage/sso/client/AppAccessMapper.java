package com.mirage.sso.client;

import java.time.LocalDateTime;
import java.util.List;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface AppAccessMapper {
    @Select("select app_code from sso_user_app where user_id = #{userId} order by app_code")
    List<String> selectUserApps(@Param("userId") Long userId);

    @Delete("delete from sso_user_app where user_id = #{userId}")
    void deleteByUserId(@Param("userId") Long userId);

    @Insert("insert into sso_user_app (user_id, app_code, created_at) values (#{userId}, #{appCode}, #{createdAt})")
    void insertUserApp(@Param("userId") Long userId, @Param("appCode") String appCode, @Param("createdAt") LocalDateTime createdAt);
}
