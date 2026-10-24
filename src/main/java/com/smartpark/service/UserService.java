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
    private static final Pattern PERSON_NAME_PATTERN = Pattern.compile("^[\\p{L} ]+$");
    private static final String DEFAULT_EMAIL = "adminParqueo@gmail.com";
    private static final String DEFAULT_PASSWORD = "SmartPark2026%";

    private static final int NAME_MIN = 2;
    private static final int NAME_MAX = 40;
    private static final int EMAIL_MAX = 80;
    private static final int PASSWORD_MIN = 8;
    private static final int PASSWORD_MAX = 30;

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
    // Devuelve el usuario autenticado o null si las credenciales no son válidas.
    public RegisteredUser authenticate(String email, String password) {
        String normalizedEmail = normalizeEmail(email);
        return usuarioRepository.findByEmailIgnoreCase(normalizedEmail)
                .filter(user -> Objects.equals(user.getPassword(), password))
                .orElse(null);
    }

    // La cuenta de administrador es única: solo la identifica el correo reservado.
    public boolean esAdmin(String email) {
        return DEFAULT_EMAIL.equalsIgnoreCase(normalizeEmail(email));
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
        validarNombre(nombre, "El nombre");
        validarNombre(apellido, "El apellido");
        if (email.length() > EMAIL_MAX) {
            throw new IllegalArgumentException("El correo no puede superar los " + EMAIL_MAX + " caracteres.");
        }
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new IllegalArgumentException("Ingresa un correo electrónico válido.");
        }
        validarPassword(password);
        if (esAdmin(email)) {
            throw new IllegalArgumentException("Este correo está reservado para el administrador.");
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

    // Valida longitud y que el nombre o apellido contenga solo letras y espacios.
    private void validarNombre(String valor, String etiqueta) {
        if (valor.length() < NAME_MIN || valor.length() > NAME_MAX) {
            throw new IllegalArgumentException(
                    etiqueta + " debe tener entre " + NAME_MIN + " y " + NAME_MAX + " caracteres.");
        }
        if (!PERSON_NAME_PATTERN.matcher(valor).matches()) {
            throw new IllegalArgumentException(etiqueta + " solo puede contener letras y espacios.");
        }
    }

    // Valida longitud y composición de la contraseña con un mensaje específico por regla.
    private void validarPassword(String password) {
        if (password.length() < PASSWORD_MIN || password.length() > PASSWORD_MAX) {
            throw new IllegalArgumentException(
                    "La contraseña debe tener entre " + PASSWORD_MIN + " y " + PASSWORD_MAX + " caracteres.");
        }
        if (password.chars().anyMatch(Character::isWhitespace)) {
            throw new IllegalArgumentException("La contraseña no puede contener espacios.");
        }
        if (password.chars().noneMatch(Character::isUpperCase)) {
            throw new IllegalArgumentException("La contraseña debe incluir al menos una letra mayúscula.");
        }
        if (password.chars().noneMatch(Character::isLowerCase)) {
            throw new IllegalArgumentException("La contraseña debe incluir al menos una letra minúscula.");
        }
        if (password.chars().noneMatch(Character::isDigit)) {
            throw new IllegalArgumentException("La contraseña debe incluir al menos un número.");
        }
        if (password.chars().allMatch(Character::isLetterOrDigit)) {
            throw new IllegalArgumentException("La contraseña debe incluir al menos un carácter especial.");
        }
    }

    // Limpia y convierte el correo a minúsculas para comparaciones consistentes.
    private String normalizeEmail(String email) {
        return email == null ? "" : email.trim().toLowerCase(Locale.ROOT);
    }
}