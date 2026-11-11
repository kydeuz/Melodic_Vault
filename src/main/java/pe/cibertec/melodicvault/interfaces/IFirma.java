package pe.cibertec.melodicvault.interfaces;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pe.cibertec.melodicvault.modelo.Firma;
import java.time.LocalDateTime;
import java.util.List;

public interface IFirma extends JpaRepository<Firma, Integer> {

    boolean existsByUsuarioIdUsuarioAndArtistaIdArtista(Integer idUsuario, Integer idArtista);

    long countByUsuarioIdUsuarioAndFechaBetween(Integer idUsuario, LocalDateTime desde, LocalDateTime hasta);

    @EntityGraph(attributePaths = "artista")
    List<Firma> findByUsuarioIdUsuarioOrderByFechaAsc(Integer idUsuario);

    @Query("select f.ciudad, count(f) from Firma f where f.artista.idArtista = :id " +
            "group by f.ciudad order by count(f) desc")
    List<Object[]> ciudadesPorArtista(@Param("id") Integer idArtista);
}