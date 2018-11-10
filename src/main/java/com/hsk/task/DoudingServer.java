package com.hsk.task;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import springfox.documentation.swagger2.annotations.EnableSwagger2;


@EnableSwagger2
@EnableScheduling	
@SpringBootApplication
@EnableTransactionManagement
@MapperScan(value="com.hsk.task.mapper")
public class DoudingServer extends SpringBootServletInitializer{
	
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(DoudingServer.class);
    }
	
	public static void main(String[] args){ 
	       SpringApplication.run(DoudingServer.class, args); 
	    }
}
