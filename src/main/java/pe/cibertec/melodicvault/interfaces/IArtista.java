package pe.cibertec.melodicvault.interfaces;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import pe.cibertec.melodicvault.modelo.Artista;

import java.util.Optional;


public interface IArtista extends JpaRepository<Artista, Integer> {

    Optional<Artista> findBySlug(String slug);

    boolean existsBySlug(String slug);

    // dueño del perfil: sirve para el artistaId del login y para el 409 "Ya tienes un perfil"
    Optional<Artista> findByUsuarioIdUsuario(Integer idUsuario);

    boolean existsByUsuarioIdUsuario(Integer idUsuario);

    // contador atómico de firmas; no toca @Version (lo pide el ER)
    @Modifying(clearAutomatically = true)
    @Query("update Artista a set a.firmas = a.firmas + 1 where a.idArtista = :id")
    int incrementarFirmas(@Param("id") Integer id);

    @Override
    @EntityGraph(attributePaths = "genero")
    Page<Artista> findAll(Pageable pageable);
}