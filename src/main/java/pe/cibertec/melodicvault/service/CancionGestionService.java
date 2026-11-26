package pe.cibertec.melodicvault.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.cibertec.melodicvault.dto.CancionRequest;
import pe.cibertec.melodicvault.dto.CancionResponse;
import pe.cibertec.melodicvault.excepcion.ConflictoException;
import pe.cibertec.melodicvault.excepcion.DatosInvalidosException;
import pe.cibertec.melodicvault.excepcion.RecursoNoEncontradoException;
import pe.cibertec.melodicvault.interfaces.IAlbum;
import pe.cibertec.melodicvault.interfaces.ICancion;
import pe.cibertec.melodicvault.modelo.Album;
import pe.cibertec.melodicvault.modelo.Cancion;
import pe.cibertec.melodicvault.modelo.Usuario;

import java.util.Map;

@Service
public class CancionGestionService {

    private static final String MSG_PISTA_REPETIDA = "La pista ya existe en este álbum";

    private final ICancion cancionRepo;
    private final IAlbum albumRepo;
    private final PermisosArtista permisos;

    public CancionGestionService(ICancion cancionRepo, IAlbum albumRepo, PermisosArtista permisos) {
        this.cancionRepo = cancionRepo;
        this.albumRepo = albumRepo;
        this.permisos = permisos;
    }

    @Transactional
    public CancionResponse crear(CancionRequest req, Usuario usuario) {
        Album album = albumRepo.findById(req.albumId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Álbum no encontrado"));
        permisos.exigirDuenoOAdmin(album.getArtista(), usuario);
        exigirPistaLibre(album.getIdAlbum(), req.numeroPista());

        Cancion cancion = new Cancion();
        cancion.setAlbum(album);
        cancion.setTitulo(req.titulo());
        cancion.setNumeroPista(req.numeroPista());
        return aResponse(cancionRepo.save(cancion));
    }

    @Transactional
    public CancionResponse editar(int id, CancionRequest req, Usuario usuario) {
        Cancion cancion = buscarCancion(id);
        Album album = cancion.getAlbum();
        permisos.exigirDuenoOAdmin(album.getArtista(), usuario);

        // el contrato no permite mover una canción a otro álbum
        if (!album.getIdAlbum().equals(req.albumId())) {
            throw new DatosInvalidosException(
                    Map.of("albumId", "No se puede cambiar la canción de álbum"));
        }
        // solo se valida si cambió la pista; si no, chocaría consigo misma
        if (!cancion.getNumeroPista().equals(req.numeroPista())) {
            exigirPistaLibre(album.getIdAlbum(), req.numeroPista());
        }

        cancion.setTitulo(req.titulo());
        cancion.setNumeroPista(req.numeroPista());
        return aResponse(cancion);
    }

    @Transactional
    public void eliminar(int id, Usuario usuario) {
        Cancion cancion = buscarCancion(id);
        permisos.exigirDuenoOAdmin(cancion.getAlbum().getArtista(), usuario);
        cancionRepo.delete(cancion);
    }

    private Cancion buscarCancion(int id) {
        return cancionRepo.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Canción no encontrada"));
    }

    private void exigirPistaLibre(Integer idAlbum, Integer numeroPista) {
        if (cancionRepo.existsByAlbumIdAlbumAndNumeroPista(idAlbum, numeroPista)) {
            throw new ConflictoException(MSG_PISTA_REPETIDA);
        }
    }

    private CancionResponse aResponse(Cancion cancion) {
        return new CancionResponse(cancion.getIdCancion(), cancion.getAlbum().getIdAlbum(),
                cancion.getTitulo(), cancion.getNumeroPista());
    }
}
