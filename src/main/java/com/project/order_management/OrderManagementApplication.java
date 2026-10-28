package com.project.order_management;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication

//@PropertySource(value = "file:C:\\Users\\swast\\workspace\\etc\\secrets\\application.properties",ignoreResourceNotFound = false)
public class OrderManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderManagementApplication.class, args);
    }

}
