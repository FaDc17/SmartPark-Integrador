package com.smartpark.service;

import com.smartpark.model.RegisteredUser;
import com.smartpark.repository.UsuarioRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    private static final String ADMIN = "adminParqueo@gmail.com";

    @Mock UsuarioRepository repo;
    @InjectMocks UserService service;

    private RegisteredUser valid() {
        return new RegisteredUser("Ana", "Perez", "ana@mail.com", "Passw0rd!");
    }

    private String errorOf(RegisteredUser u) {
        return assertThrows(IllegalArgumentException.class, () -> service.save(u)).getMessage();
    }

    // ---------- ensureDefaultUser ----------
    @Test
    @DisplayName("Happy: crea el admin si no existe")
    void defaultUserCreated() {
        when(repo.existsByEmailIgnoreCase(ADMIN)).thenReturn(false);
        service.ensureDefaultUser();
        ArgumentCaptor<RegisteredUser> c = ArgumentCaptor.forClass(RegisteredUser.class);
        verify(repo).save(c.capture());
        assertEquals(ADMIN, c.getValue().getEmail());
        assertEquals("SmartPark2026%", c.getValue().getPassword());
    }

    @Test
    @DisplayName("Borde: no duplica el admin si ya existe")
    void defaultUserExists() {
        when(repo.existsByEmailIgnoreCase(ADMIN)).thenReturn(true);
        service.ensureDefaultUser();
        verify(repo, never()).save(any());
    }

    // ---------- authenticate ----------
    @Test
    @DisplayName("Happy: autenticación correcta (email normalizado)")
    void authenticateOk() {
        RegisteredUser u = valid();
        when(repo.findByEmailIgnoreCase("ana@mail.com")).thenReturn(Optional.of(u));
        assertSame(u, service.authenticate("  ANA@mail.com ", "Passw0rd!"));
    }

    @Test
    @DisplayName("Unhappy: contraseña incorrecta devuelve null")
    void authenticateWrongPassword() {
        when(repo.findByEmailIgnoreCase("ana@mail.com")).thenReturn(Optional.of(valid()));
        assertNull(service.authenticate("ana@mail.com", "otra"));
    }

    @Test
    @DisplayName("Unhappy: usuario inexistente devuelve null")
    void authenticateUnknownUser() {
        when(repo.findByEmailIgnoreCase("x@x.com")).thenReturn(Optional.empty());
        assertNull(service.authenticate("x@x.com", "p"));
    }

    @Test
    @DisplayName("Unhappy: email null se normaliza a vacío")
    void authenticateNullEmail() {
        when(repo.findByEmailIgnoreCase("")).thenReturn(Optional.empty());
        assertNull(service.authenticate(null, "p"));
    }

    // ---------- esAdmin ----------
    @Test
    void esAdmin() {
        assertTrue(service.esAdmin(ADMIN));
        assertTrue(service.esAdmin("  ADMINPARQUEO@GMAIL.COM "));
        assertFalse(service.esAdmin("otro@mail.com"));
        assertFalse(service.esAdmin(null));
    }

    // ---------- save: happy ----------
    @Test
    @DisplayName("Happy: guarda usuario válido con datos normalizados")
    void saveOk() {
        when(repo.existsByEmailIgnoreCase("ana@mail.com")).thenReturn(false);
        when(repo.save(any(RegisteredUser.class))).thenAnswer(i -> i.getArgument(0));
        RegisteredUser r = service.save(new RegisteredUser("  Ana María ", " Pérez ", "  ANA@Mail.com ", "Passw0rd!"));
        assertEquals("Ana María", r.getNombre());
        assertEquals("Pérez", r.getApellido());
        assertEquals("ana@mail.com", r.getEmail());
    }

    // ---------- save: unhappy ----------
    @Test
    @DisplayName("Unhappy: usuario nulo")
    void saveNull() {
        assertEquals("El usuario no puede ser nulo.", errorOf(null));
    }

    @Test
    @DisplayName("Unhappy: campos nulos o vacíos -> 'Completa todos los campos.'")
    void saveEmptyFields() {
        assertEquals("Completa todos los campos.", errorOf(new RegisteredUser(null, "P", "a@b.com", "Passw0rd!")));
        assertEquals("Completa todos los campos.", errorOf(new RegisteredUser("A", null, "a@b.com", "Passw0rd!")));
        assertEquals("Completa todos los campos.", errorOf(new RegisteredUser("A", "P", null, "Passw0rd!")));
        assertEquals("Completa todos los campos.", errorOf(new RegisteredUser("A", "P", "a@b.com", null)));
        assertEquals("Completa todos los campos.", errorOf(new RegisteredUser("  ", "P", "a@b.com", "Passw0rd!")));
        assertEquals("Completa todos los campos.", errorOf(new RegisteredUser("A", "  ", "a@b.com", "Passw0rd!")));
        assertEquals("Completa todos los campos.", errorOf(new RegisteredUser("A", "P", "  ", "Passw0rd!")));
        assertEquals("Completa todos los campos.", errorOf(new RegisteredUser("A", "P", "a@b.com", "")));
    }

    @Test
    @DisplayName("Unhappy: nombre/apellido demasiado cortos o largos")
    void saveNameLength() {
        assertEquals("El nombre debe tener entre 2 y 40 caracteres.",
                errorOf(new RegisteredUser("A", "Perez", "a@b.com", "Passw0rd!")));
        assertEquals("El nombre debe tener entre 2 y 40 caracteres.",
                errorOf(new RegisteredUser("A".repeat(41), "Perez", "a@b.com", "Passw0rd!")));
        assertEquals("El apellido debe tener entre 2 y 40 caracteres.",
                errorOf(new RegisteredUser("Ana", "P", "a@b.com", "Passw0rd!")));
    }

    @Test
    @DisplayName("Unhappy: nombre/apellido con caracteres inválidos")
    void saveNameChars() {
        assertEquals("El nombre solo puede contener letras y espacios.",
                errorOf(new RegisteredUser("Ana3", "Perez", "a@b.com", "Passw0rd!")));
        assertEquals("El apellido solo puede contener letras y espacios.",
                errorOf(new RegisteredUser("Ana", "Pe-rez", "a@b.com", "Passw0rd!")));
    }

    @Test
    @DisplayName("Unhappy: correo demasiado largo")
    void saveEmailTooLong() {
        String email = "a".repeat(75) + "@b.com"; // 81 caracteres
        assertEquals("El correo no puede superar los 80 caracteres.",
                errorOf(new RegisteredUser("Ana", "Perez", email, "Passw0rd!")));
    }

    @ParameterizedTest
    @CsvSource({"sinarroba", "a@b", "a b@c.com", "@b.com", "a@@b.com"})
    @DisplayName("Unhappy: correo con formato inválido")
    void saveEmailInvalid(String email) {
        assertEquals("Ingresa un correo electrónico válido.",
                errorOf(new RegisteredUser("Ana", "Perez", email, "Passw0rd!")));
    }

    @ParameterizedTest
    @CsvSource({
            "Ab1!,                          'La contraseña debe tener entre 8 y 30 caracteres.'",
            "Ab1!Ab1!Ab1!Ab1!Ab1!Ab1!Ab1!Ab1!, 'La contraseña debe tener entre 8 y 30 caracteres.'",
            "'Pass w0rd!',                  'La contraseña no puede contener espacios.'",
            "passw0rd!,                     'La contraseña debe incluir al menos una letra mayúscula.'",
            "PASSW0RD!,                     'La contraseña debe incluir al menos una letra minúscula.'",
            "Password!,                     'La contraseña debe incluir al menos un número.'",
            "Passw0rd1,                     'La contraseña debe incluir al menos un carácter especial.'"
    })
    @DisplayName("Unhappy: cada regla de contraseña tiene su mensaje")
    void savePasswordRules(String password, String expected) {
        assertEquals(expected, errorOf(new RegisteredUser("Ana", "Perez", "a@b.com", password)));
    }

    @Test
    @DisplayName("Unhappy: correo reservado para administrador")
    void saveReservedEmail() {
        assertEquals("Este correo está reservado para el administrador.",
                errorOf(new RegisteredUser("Ana", "Perez", ADMIN, "Passw0rd!")));
        verify(repo, never()).save(any());
    }

    @Test
    @DisplayName("Unhappy: correo ya registrado")
    void saveDuplicateEmail() {
        when(repo.existsByEmailIgnoreCase("ana@mail.com")).thenReturn(true);
        assertEquals("Ya existe una cuenta registrada con este correo electrónico.", errorOf(valid()));
        verify(repo, never()).save(any());
    }

    // ---------- findAll ----------
    @Test
    void findAll() {
        List<RegisteredUser> list = List.of(valid());
        when(repo.findAll()).thenReturn(list);
        assertSame(list, service.findAll());
    }
}
