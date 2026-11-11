package pe.cibertec.melodicvault.interfaces;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import pe.cibertec.melodicvault.modelo.Usuario;

public interface IUsuario extends JpaRepository<Usuario, Integer> {
    Optional<Usuario> findByEmail(String email);
    boolean existsByEmail(String email);
}