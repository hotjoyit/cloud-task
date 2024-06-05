package com.fnf.cloudtaskapp;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.task.configuration.EnableTask;

@Slf4j
@EnableTask
@SpringBootApplication
public class EcOmsOrderApplication {

  public static void main(String[] args) {
    SpringApplication.run(EcOmsOrderApplication.class, args);
  }

}
