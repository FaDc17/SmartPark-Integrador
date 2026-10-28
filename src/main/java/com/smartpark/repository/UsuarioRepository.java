package com.smartpark.repository;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.smartpark.model.RegisteredUser;

public interface UsuarioRepository extends JpaRepository<RegisteredUser, Long> {
    // Comprueba si existe un usuario con ese correo sin distinguir mayúsculas.
    boolean existsByEmailIgnoreCase(String email);
    // Busca un usuario por correo sin distinguir mayúsculas.
    Optional<RegisteredUser> findByEmailIgnoreCase(String email);
}
