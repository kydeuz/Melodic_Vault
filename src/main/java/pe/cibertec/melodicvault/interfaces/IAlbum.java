package pe.cibertec.melodicvault.interfaces;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.cibertec.melodicvault.modelo.Album;

import java.util.List;

public interface IAlbum extends JpaRepository<Album, Integer> {

    List<Album> findByArtistaIdArtistaOrderByAnioDesc(Integer idArtista);

}