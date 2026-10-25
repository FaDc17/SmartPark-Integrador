package com.smartpark.controller;

import com.smartpark.model.RegisteredUser;
import com.smartpark.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock UserService userService;
    @InjectMocks AuthController controller;

    private MockHttpSession session;
    private RedirectAttributesModelMap redirect;

    @BeforeEach
    void setUp() {
        session = new MockHttpSession();
        redirect = new RedirectAttributesModelMap();
    }

    // ---------- index ----------
    @Test
    void indexRedirectsToLogin() {
        assertEquals("redirect:/login", controller.index());
    }

    // ---------- GET /login ----------
    @Test
    @DisplayName("Happy: muestra la página de login con un usuario vacío")
    void loginPage() {
        Model model = new ExtendedModelMap();
        assertEquals("login", controller.loginPage(session, model));
        assertInstanceOf(RegisteredUser.class, model.getAttribute("user"));
    }

    @Test
    @DisplayName("Borde: con sesión iniciada redirige al dashboard")
    void loginPageAlreadyLoggedIn() {
        session.setAttribute("loggedIn", true);
        assertEquals("redirect:/dashboard", controller.loginPage(session, new ExtendedModelMap()));
    }

    // ---------- POST /login ----------
    @Test
    @DisplayName("Happy: login de administrador asigna rol ADMIN")
    void loginAdmin() {
        RegisteredUser admin = new RegisteredUser("A", "B", "admin@x.com", "p");
        when(userService.authenticate("admin@x.com", "p")).thenReturn(admin);
        when(userService.esAdmin("admin@x.com")).thenReturn(true);

        assertEquals("redirect:/dashboard", controller.login("admin@x.com", "p", session, redirect));
        assertEquals(true, session.getAttribute("loggedIn"));
        assertEquals("admin@x.com", session.getAttribute("username"));
        assertEquals("ADMIN", session.getAttribute("rol"));
    }

    @Test
    @DisplayName("Happy: login de usuario normal asigna rol CAJERO")
    void loginCajero() {
        RegisteredUser u = new RegisteredUser("A", "B", "u@x.com", "p");
        when(userService.authenticate("u@x.com", "p")).thenReturn(u);
        when(userService.esAdmin("u@x.com")).thenReturn(false);

        assertEquals("redirect:/dashboard", controller.login("u@x.com", "p", session, redirect));
        assertEquals("CAJERO", session.getAttribute("rol"));
    }

    @Test
    @DisplayName("Unhappy: email o password nulos/vacíos")
    void loginMissingFields() {
        String[][] cases = {{null, "p"}, {"e@x.com", null}, {"  ", "p"}, {"e@x.com", "   "}};
        for (String[] c : cases) {
            RedirectAttributesModelMap r = new RedirectAttributesModelMap();
            assertEquals("redirect:/login", controller.login(c[0], c[1], session, r));
            assertEquals("Ingresa tu usuario o correo electrónico y contraseña.", r.getFlashAttributes().get("error"));
        }
        assertNull(session.getAttribute("loggedIn"));
        verifyNoInteractions(userService);
    }

    @Test
    @DisplayName("Unhappy: credenciales incorrectas")
    void loginBadCredentials() {
        when(userService.authenticate("e@x.com", "mala")).thenReturn(null);
        assertEquals("redirect:/login", controller.login("e@x.com", "mala", session, redirect));
        assertEquals("Las credenciales son incorrectas o el usuario no está registrado.",
                redirect.getFlashAttributes().get("error"));
        assertNull(session.getAttribute("loggedIn"));
    }

    // ---------- GET /register ----------
    @Test
    void registerPage() {
        Model model = new ExtendedModelMap();
        assertEquals("register", controller.registerPage(model));
        assertInstanceOf(RegisteredUser.class, model.getAttribute("user"));
    }

    // ---------- POST /register ----------
    @Test
    @DisplayName("Happy: registro exitoso")
    void registerOk() {
        assertEquals("redirect:/login", controller.register("Ana", "Perez", "a@b.com", "Passw0rd!", redirect));
        ArgumentCaptor<RegisteredUser> c = ArgumentCaptor.forClass(RegisteredUser.class);
        verify(userService).save(c.capture());
        assertEquals("a@b.com", c.getValue().getEmail());
        assertEquals("Registro exitoso", redirect.getFlashAttributes().get("success"));
    }

    @Test
    @DisplayName("Unhappy: el servicio rechaza el registro")
    void registerError() {
        when(userService.save(any(RegisteredUser.class))).thenThrow(new IllegalArgumentException("Completa todos los campos."));
        assertEquals("redirect:/register", controller.register("", "", "", "", redirect));
        assertEquals("Completa todos los campos.", redirect.getFlashAttributes().get("error"));
        assertNull(redirect.getFlashAttributes().get("success"));
    }

    // ---------- logout ----------
    @Test
    @DisplayName("Happy: logout invalida la sesión")
    void logout() {
        session.setAttribute("loggedIn", true);
        assertEquals("redirect:/login", controller.logout(session));
        assertTrue(session.isInvalid());
    }
}
