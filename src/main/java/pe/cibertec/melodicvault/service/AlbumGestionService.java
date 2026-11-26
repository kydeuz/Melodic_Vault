package pe.cibertec.melodicvault.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.cibertec.melodicvault.dto.AlbumRequest;
import pe.cibertec.melodicvault.dto.AlbumResponse;
import pe.cibertec.melodicvault.dto.CancionResumen;
import pe.cibertec.melodicvault.excepcion.DatosInvalidosException;
import pe.cibertec.melodicvault.excepcion.RecursoNoEncontradoException;
import pe.cibertec.melodicvault.interfaces.IAlbum;
import pe.cibertec.melodicvault.interfaces.IArtista;
import pe.cibertec.melodicvault.interfaces.ICancion;
import pe.cibertec.melodicvault.modelo.Album;
import pe.cibertec.melodicvault.modelo.Artista;
import pe.cibertec.melodicvault.modelo.Usuario;

import java.util.List;
import java.util.Map;

@Service
public class AlbumGestionService {

    private final IAlbum albumRepo;
    private final IArtista artistaRepo;
    private final ICancion cancionRepo;
    private final PermisosArtista permisos;

    public AlbumGestionService(IAlbum albumRepo, IArtista artistaRepo,
                               ICancion cancionRepo, PermisosArtista permisos) {
        this.albumRepo = albumRepo;
        this.artistaRepo = artistaRepo;
        this.cancionRepo = cancionRepo;
        this.permisos = permisos;
    }

    @Transactional
    public AlbumResponse crear(AlbumRequest req, Usuario usuario) {
        Artista artista = artistaRepo.findById(req.artistaId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Artista no encontrado"));
        permisos.exigirDuenoOAdmin(artista, usuario);

        Album album = new Album();
        album.setArtista(artista);
        album.setTitulo(req.titulo());
        album.setAnio(req.anio());
        return aResponse(albumRepo.save(album));
    }

    @Transactional
    public AlbumResponse editar(int id, AlbumRequest req, Usuario usuario) {
        Album album = buscarAlbum(id);
        permisos.exigirDuenoOAdmin(album.getArtista(), usuario);

        // el contrato no permite mover un álbum a otro artista
        if (!album.getArtista().getIdArtista().equals(req.artistaId())) {
            throw new DatosInvalidosException(
                    Map.of("artistaId", "No se puede cambiar el álbum de artista"));
        }

        album.setTitulo(req.titulo());
        album.setAnio(req.anio());
        return aResponse(album);
    }

    @Transactional
    public void eliminar(int id, Usuario usuario) {
        Album album = buscarAlbum(id);
        permisos.exigirDuenoOAdmin(album.getArtista(), usuario);
        albumRepo.delete(album);   // sus canciones caen por ON DELETE CASCADE
    }

    private Album buscarAlbum(int id) {
        return albumRepo.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Álbum no encontrado"));
    }

    private AlbumResponse aResponse(Album album) {
        List<CancionResumen> canciones = cancionRepo
                .findByAlbumIdAlbumOrderByNumeroPista(album.getIdAlbum()).stream()
                .map(c -> new CancionResumen(c.getIdCancion(), c.getTitulo(), c.getNumeroPista()))
                .toList();
        return new AlbumResponse(album.getIdAlbum(), album.getArtista().getIdArtista(),
                album.getTitulo(), album.getAnio(), canciones);
    }
}
