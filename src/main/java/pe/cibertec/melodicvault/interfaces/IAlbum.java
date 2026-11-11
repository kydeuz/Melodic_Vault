package pe.cibertec.melodicvault.interfaces;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.cibertec.melodicvault.modelo.Album;

public interface IAlbum extends JpaRepository<Album, Integer> {
}