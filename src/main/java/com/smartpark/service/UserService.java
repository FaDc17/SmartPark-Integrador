package com.smartpark.service;

import com.smartpark.model.RegisteredUser;
import com.smartpark.repository.UsuarioRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.regex.Pattern;

@Service
public class UserService {
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");
    private static final String DEFAULT_EMAIL = "admin";
    private static final String DEFAULT_PASSWORD = "123456";

    private final UsuarioRepository usuarioRepository;

    public UserService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    // Conserva la cuenta inicial sin duplicarla ni modificar una existente.
    @PostConstruct
    public void ensureDefaultUser() {
        if (!usuarioRepository.existsByEmailIgnoreCase(DEFAULT_EMAIL)) {
            usuarioRepository.save(new RegisteredUser(
                    "Administrador", "Sistema", DEFAULT_EMAIL, DEFAULT_PASSWORD));
        }
    }

    // Comprueba las credenciales contra las cuentas persistidas en PostgreSQL.
    public boolean authenticate(String email, String password) {
        String normalizedEmail = normalizeEmail(email);
        return usuarioRepository.findByEmailIgnoreCase(normalizedEmail)
                .map(user -> Objects.equals(user.getPassword(), password))
                .orElse(false);
    }

    // Valida los datos y guarda el usuario en PostgreSQL.
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
        if (usuarioRepository.existsByEmailIgnoreCase(email)) {
            throw new IllegalArgumentException("Ya existe una cuenta registrada con este correo electrónico.");
        }
        RegisteredUser saved = new RegisteredUser(nombre, apellido, email, password);
        return usuarioRepository.save(saved);
    }

    // Consulta los usuarios persistidos en PostgreSQL.
    public List<RegisteredUser> findAll() {
        return usuarioRepository.findAll();
    }

    // Limpia y convierte el correo a minúsculas para comparaciones consistentes.
    private String normalizeEmail(String email) {
        return email == null ? "" : email.trim().toLowerCase(Locale.ROOT);
    }
}
