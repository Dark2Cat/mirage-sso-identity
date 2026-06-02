package com.mirage.sso;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@MapperScan("com.mirage.sso")
@ConfigurationPropertiesScan
@SpringBootApplication
public class MirageSsoIdentityApplication {

    public static void main(String[] args) {
        SpringApplication.run(MirageSsoIdentityApplication.class, args);
    }
}
