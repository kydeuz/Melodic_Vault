package pe.cibertec.melodicvault.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.cibertec.melodicvault.dto.ArtistaCrearRequest;
import pe.cibertec.melodicvault.dto.ArtistaEditarRequest;
import pe.cibertec.melodicvault.excepcion.ConflictoException;
import pe.cibertec.melodicvault.excepcion.DatosInvalidosException;
import pe.cibertec.melodicvault.excepcion.RecursoNoEncontradoException;
import pe.cibertec.melodicvault.interfaces.IArtista;
import pe.cibertec.melodicvault.interfacesService.IGeneroService;
import pe.cibertec.melodicvault.modelo.Artista;
import pe.cibertec.melodicvault.modelo.Genero;
import pe.cibertec.melodicvault.modelo.Usuario;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;


@Service
public class ArtistaGestionService {

    private static final String MSG_VERSION =
            "Otro usuario modificó este artista. Recarga e intenta de nuevo.";

    private final IArtista artistaRepo;
    private final IGeneroService generoService;
    private final PermisosArtista permisos;
    private final Path carpetaClips;

    public ArtistaGestionService(IArtista artistaRepo, IGeneroService generoService,
                                 PermisosArtista permisos,
                                 @Value("${melodicvault.clips.carpeta:clips}") String carpetaClips) {
        this.artistaRepo = artistaRepo;
        this.generoService = generoService;
        this.permisos = permisos;
        this.carpetaClips = Paths.get(carpetaClips);
    }

    @Transactional
    public Artista crear(ArtistaCrearRequest req, Usuario usuario) {
        Genero genero = generoOError(req.generoId());

        if (artistaRepo.existsByUsuarioIdUsuario(usuario.getIdUsuario())) {
            throw new ConflictoException("Ya tienes un perfil de artista");
        }
        if (artistaRepo.existsBySlug(req.slug())) {
            throw new ConflictoException("Ese enlace ya está en uso");
        }

        Artista a = new Artista();
        a.setSlug(req.slug());
        a.setNombre(req.nombre());
        a.setGenero(genero);
        a.setSpotifyUrl(req.spotifyUrl());
        a.setYoutubeUrl(req.youtubeUrl());
        a.setInstagramUrl(req.instagramUrl());
        a.setTiktokUrl(req.tiktokUrl());
        a.setUsuario(usuario);

        try {
            return artistaRepo.saveAndFlush(a);
        } catch (DataIntegrityViolationException e) {   // dos peticiones a la vez con el mismo enlace
            throw new ConflictoException("Ese enlace ya está en uso");
        }
    }

    @Transactional
    public Artista editar(int id, ArtistaEditarRequest req, Usuario usuario) {
        Artista a = artistaRepo.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Artista no encontrado"));
        permisos.exigirDuenoOAdmin(a, usuario);
        Genero genero = generoOError(req.generoId());

        // la version que mando Angular debe ser la actual
        if (!a.getVersion().equals(req.version())) {
            throw new ConflictoException(MSG_VERSION);
        }

        a.setNombre(req.nombre());
        a.setGenero(genero);
        if (req.clipInicioSeg() != null) a.setClipInicioSeg(req.clipInicioSeg());
        a.setSpotifyUrl(req.spotifyUrl());
        a.setYoutubeUrl(req.youtubeUrl());
        a.setInstagramUrl(req.instagramUrl());
        a.setTiktokUrl(req.tiktokUrl());

        // flush para que la version nueva salga en la respuesta
        return artistaRepo.saveAndFlush(a);
    }

    @Transactional
    public void eliminar(int id, Usuario usuario) {
        Artista a = artistaRepo.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Artista no encontrado"));
        permisos.exigirDuenoOAdmin(a, usuario);

        String clipUrl = a.getClipUrl();
        artistaRepo.delete(a);       // Albumes, canciones, firmas y salas caen por ON DELETE CASCADE
        artistaRepo.flush();
        borrarClip(clipUrl);
    }

    private Genero generoOError(Integer generoId) {
        return generoService.buscarPorId(generoId).orElseThrow(
                () -> new DatosInvalidosException(Map.of("generoId", "El género no existe")));
    }

    private void borrarClip(String clipUrl) {
        if (clipUrl == null || clipUrl.isBlank()) return;
        try {
            Path nombre = Paths.get(clipUrl).getFileName();   // "/clips/luna.mp3" -> "luna.mp3"
            if (nombre != null) Files.deleteIfExists(carpetaClips.resolve(nombre));
        } catch (IOException | InvalidPathException e) {
            // el archivo huerfano no debe tumbar el borrado del artista
        }
    }
}