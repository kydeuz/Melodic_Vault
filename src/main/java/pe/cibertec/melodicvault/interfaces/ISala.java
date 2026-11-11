package pe.cibertec.melodicvault.interfaces;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.cibertec.melodicvault.modelo.Sala;

import java.time.LocalDateTime;
import java.util.List;

public interface ISala extends JpaRepository<Sala, Integer> {

    // salas futuras del artista; la primera es proximaSala
    List<Sala> findByArtistaIdArtistaAndFechaAfterOrderByFechaAsc(Integer idArtista, LocalDateTime ahora);
}