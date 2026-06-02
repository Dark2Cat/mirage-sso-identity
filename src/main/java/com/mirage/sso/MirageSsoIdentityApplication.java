package com.mirage.sso;

import org.apache.ibatis.annotations.Mapper;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@MapperScan(basePackages = "com.mirage.sso", annotationClass = Mapper.class)
@ConfigurationPropertiesScan
@SpringBootApplication
public class MirageSsoIdentityApplication {

    public static void main(String[] args) {
        SpringApplication.run(MirageSsoIdentityApplication.class, args);
    }
}
