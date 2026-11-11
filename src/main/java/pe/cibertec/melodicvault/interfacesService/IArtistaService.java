package pe.cibertec.melodicvault.interfacesService;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pe.cibertec.melodicvault.modelo.Artista;

import java.util.Optional;

public interface IArtistaService {

    Page<Artista> listar(Pageable pageable);

    Optional<Artista> listarId(int id);

    Optional<Artista> buscarPorSlug(String slug);

    Optional<Artista> buscarPorUsuario(int idUsuario);

    Artista save(Artista artista);

    void delete(int id);
}