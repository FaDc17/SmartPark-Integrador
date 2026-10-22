package com.smartpark.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.smartpark.model.RegisteredUser;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<RegisteredUser, Long> {
    boolean existsByEmailIgnoreCase(String email);

    Optional<RegisteredUser> findByEmailIgnoreCase(String email);
}
