package pe.cibertec.melodicvault.interfacesService;

import pe.cibertec.melodicvault.modelo.Album;
import java.util.Optional;

public interface IAlbumService {

    Optional<Album> listarId(int id);

    Album save(Album album);

    void delete(int id);
}
