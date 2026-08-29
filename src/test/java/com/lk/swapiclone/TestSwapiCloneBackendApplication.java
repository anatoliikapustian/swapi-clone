package com.lk.swapiclone;

import org.springframework.boot.SpringApplication;

public class TestSwapiCloneBackendApplication {

  public static void main(String[] args) {
    SpringApplication.from(SwapiCloneBackendApplication::main).with(TestcontainersConfiguration.class).run(args);
  }
}
