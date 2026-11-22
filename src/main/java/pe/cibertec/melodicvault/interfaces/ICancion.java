package pe.cibertec.melodicvault.interfaces;

import org.springframework.data.repository.CrudRepository;
import pe.cibertec.melodicvault.modelo.Cancion;

import java.util.List;

public interface ICancion extends CrudRepository<Cancion, Integer> {

    List<Cancion> findByAlbumIdAlbumOrderByNumeroPista(Integer idAlbum);

    // para el 409: la pista no puede repetirse dentro del mismo álbum
    boolean existsByAlbumIdAlbumAndNumeroPista(Integer idAlbum, Integer numeroPista);
}