package com.w.t;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


/**
 * @Packagename com.w
 * @Classname Application
 * @Description
 * @Authors Mr.Wu
 * @Date 2020/10/12 09:10
 * @Version 1.0
 */
@SpringBootApplication
//@MapperScan("com.w.t.dao")
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
