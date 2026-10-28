package com.smartpark;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.boot.SpringApplication;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class SmartParkApplicationTest {

    @Test
    @DisplayName("main delega el arranque en SpringApplication.run")
    void mainDelegatesToSpringApplication() {
        try (MockedStatic<SpringApplication> spring = Mockito.mockStatic(SpringApplication.class)) {
            String[] args = {"--x=1"};
            assertDoesNotThrow(() -> SmartParkApplication.main(args));
            spring.verify(() -> SpringApplication.run(SmartParkApplication.class, args));
        }
    }

    @Test
    @DisplayName("la clase se puede instanciar")
    void canInstantiate() {
        new SmartParkApplication();
    }
}
