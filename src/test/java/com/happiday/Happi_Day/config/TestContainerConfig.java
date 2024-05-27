//package com.happiday.Happi_Day.config;
//
//import org.junit.jupiter.api.DisplayName;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Profile;
//import org.testcontainers.containers.GenericContainer;
//import org.testcontainers.utility.DockerImageName;
//
//@DisplayName("Redis Test Containers")
//@Profile("test")
//@Configuration
//public class TestContainerConfig{
//
//    private static final String REDIS_DOCKER_IMAGE = "redis:5.0.3-alpine";
//
//    static {
//        GenericContainer<?> REDIS_CONTAINER =
//                new GenericContainer<>(DockerImageName.parse(REDIS_DOCKER_IMAGE))
//                        .withExposedPorts(6379)
//                        .withReuse(true);
//
//        REDIS_CONTAINER.start();
//
//        System.setProperty("spring.data.redis.host", REDIS_CONTAINER.getHost());
//        System.setProperty("spring.data.redis.port", REDIS_CONTAINER.getMappedPort(6379).toString());
//    }
//}