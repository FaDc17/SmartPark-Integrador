package com.smartpark.service;

import com.smartpark.model.RegisteredUser;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.regex.Pattern;

@Service
public class UserService {
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");
    private static final String DEFAULT_EMAIL = "admin";
    private static final String DEFAULT_PASSWORD = "123456";

    private final List<RegisteredUser> users = new ArrayList<>();

    public UserService() {
        users.add(new RegisteredUser("Administrador", "Sistema", DEFAULT_EMAIL, DEFAULT_PASSWORD));
    }

    public boolean authenticate(String email, String password) {
        String normalizedEmail = normalizeEmail(email);
        return (DEFAULT_EMAIL.equalsIgnoreCase(normalizedEmail) && DEFAULT_PASSWORD.equals(password))
                || users.stream().anyMatch(user -> normalizeEmail(user.getEmail()).equals(normalizedEmail)
                && Objects.equals(user.getPassword(), password));
    }

    public RegisteredUser save(RegisteredUser user) {
        if (user == null) {
            throw new IllegalArgumentException("El usuario no puede ser nulo.");
        }
        String nombre = user.getNombre() == null ? "" : user.getNombre().trim();
        String apellido = user.getApellido() == null ? "" : user.getApellido().trim();
        String email = normalizeEmail(user.getEmail());
        String password = user.getPassword() == null ? "" : user.getPassword();

        if (nombre.isEmpty() || apellido.isEmpty() || email.isEmpty() || password.isEmpty()) {
            throw new IllegalArgumentException("Completa todos los campos.");
        }
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new IllegalArgumentException("Ingresa un correo electrónico válido.");
        }
        if (password.length() < 6) {
            throw new IllegalArgumentException("La contraseña debe tener al menos 6 caracteres.");
        }
        if (DEFAULT_EMAIL.equalsIgnoreCase(email) || users.stream().anyMatch(u -> normalizeEmail(u.getEmail()).equals(email))) {
            throw new IllegalArgumentException("Ya existe una cuenta registrada con este correo electrónico.");
        }
        RegisteredUser saved = new RegisteredUser(nombre, apellido, email, password);
        users.add(saved);
        return saved;
    }

    public List<RegisteredUser> findAll() {
        return new ArrayList<>(users);
    }

    private String normalizeEmail(String email) {
        return email == null ? "" : email.trim().toLowerCase(Locale.ROOT);
    }
}
