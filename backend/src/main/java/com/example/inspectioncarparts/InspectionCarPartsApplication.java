package com.example.inspectioncarparts;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@EnableSwagger2
@EnableTransactionManagement
@SpringBootApplication
@MapperScan("com.example.inspectioncarparts.mapper")
public class InspectionCarPartsApplication {
    public static void main(String[] args) {
        SpringApplication.run(InspectionCarPartsApplication.class, args);
    }
}