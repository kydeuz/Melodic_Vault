package pe.cibertec.melodicvault.interfaces;


import org.springframework.data.repository.CrudRepository;
import pe.cibertec.melodicvault.modelo.Cancion;

import java.util.List;

public interface ICancion extends CrudRepository<Cancion, Integer> {

    List<Cancion> findByAlbumIdAlbumOrderByNumeroPista(Integer idAlbum);

}