package pe.cibertec.melodicvault.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.cibertec.melodicvault.interfaces.IAlbum;
import pe.cibertec.melodicvault.interfacesService.IAlbumService;
import pe.cibertec.melodicvault.modelo.Album;
import java.util.Optional;

@Service
public class AlbumService implements IAlbumService {

    @Autowired
    private IAlbum data;

    @Override
    public Optional<Album> listarId(int id) {
        return data.findById(id);
    }

    @Override
    @Transactional
    public Album save(Album album) {
        return data.save(album);
    }

    @Override
    @Transactional
    public void delete(int id) {
        data.deleteById(id);
    }
}