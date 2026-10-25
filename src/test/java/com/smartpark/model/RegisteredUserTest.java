package com.smartpark.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RegisteredUserTest {

    @Test
    @DisplayName("Happy: constructor con parámetros")
    void parameterizedConstructor() {
        RegisteredUser u = new RegisteredUser("Ana", "Perez", "a@b.com", "Pass123!");
        assertEquals("Ana", u.getNombre());
        assertEquals("Perez", u.getApellido());
        assertEquals("a@b.com", u.getEmail());
        assertEquals("Pass123!", u.getPassword());
        assertNull(u.getId());
    }

    @Test
    @DisplayName("Happy: constructor vacío y setters/getters")
    void settersAndGetters() {
        RegisteredUser u = new RegisteredUser();
        u.setId(7L);
        u.setNombre("Luis");
        u.setApellido("Soto");
        u.setEmail("l@s.com");
        u.setPassword("Secret1$");
        assertEquals(7L, u.getId());
        assertEquals("Luis", u.getNombre());
        assertEquals("Soto", u.getApellido());
        assertEquals("l@s.com", u.getEmail());
        assertEquals("Secret1$", u.getPassword());
    }
}
