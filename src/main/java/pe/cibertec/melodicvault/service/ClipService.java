package pe.cibertec.melodicvault.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import pe.cibertec.melodicvault.dto.ClipResponse;
import pe.cibertec.melodicvault.excepcion.ArchivoInvalidoException;
import pe.cibertec.melodicvault.excepcion.RecursoNoEncontradoException;
import pe.cibertec.melodicvault.interfaces.IArtista;
import pe.cibertec.melodicvault.modelo.Artista;
import pe.cibertec.melodicvault.modelo.Usuario;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Set;

@Service
public class ClipService {

    private static final Set<String> FORMATOS_PERMITIDOS = Set.of("mp3", "m4a", "wav");
    private static final long TAMANO_MAXIMO_BYTES = 15L * 1024 * 1024;   // 15 MB
    private static final String URL_BASE = "/clips/";
    private static final String MSG_FORMATO = "Formato no permitido. Usa mp3, m4a o wav";

    private final IArtista artistaRepo;
    private final PermisosArtista permisos;
    private final Path carpetaClips;

    public ClipService(IArtista artistaRepo, PermisosArtista permisos,
                       @Value("${melodicvault.clips.carpeta:clips}") String carpetaClips) {
        this.artistaRepo = artistaRepo;
        this.permisos = permisos;
        this.carpetaClips = Paths.get(carpetaClips);
    }

    @Transactional
    public ClipResponse subir(int artistaId, MultipartFile archivo, Usuario usuario) {
        Artista artista = artistaRepo.findById(artistaId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Artista no encontrado"));
        permisos.exigirDuenoOAdmin(artista, usuario);

        String nombreOriginal = validar(archivo);
        String extension = extensionDe(nombreOriginal);
        String nombreGuardado = artista.getSlug() + "." + extension;   // luna.mp3

        borrarClipAnterior(artista.getClipUrl(), nombreGuardado);
        guardarEnDisco(archivo, nombreGuardado);

        artista.setClipUrl(URL_BASE + nombreGuardado);
        artista.setClipTitulo(sinExtension(nombreOriginal));
        artista.setClipInicioSeg(0);
        artistaRepo.save(artista);

        return new ClipResponse(artista.getClipUrl(), artista.getClipTitulo(),
                artista.getClipInicioSeg());
    }

    /** Devuelve el nombre original si el archivo es válido; si no, lanza 400. */
    private String validar(MultipartFile archivo) {
        if (archivo == null || archivo.isEmpty() || archivo.getOriginalFilename() == null) {
            throw new ArchivoInvalidoException("Debes adjuntar un archivo de audio");
        }
        if (archivo.getSize() > TAMANO_MAXIMO_BYTES) {
            throw new ArchivoInvalidoException("El archivo no puede superar 15 MB");
        }
        String nombre = Paths.get(archivo.getOriginalFilename()).getFileName().toString();
        if (!FORMATOS_PERMITIDOS.contains(extensionDe(nombre))) {
            throw new ArchivoInvalidoException(MSG_FORMATO);
        }
        return nombre;
    }

    private void guardarEnDisco(MultipartFile archivo, String nombreGuardado) {
        try (InputStream entrada = archivo.getInputStream()) {
            Files.createDirectories(carpetaClips);
            Files.copy(entrada, carpetaClips.resolve(nombreGuardado),
                    StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo guardar el clip", e);
        }
    }

    /** Si el clip anterior tenía otra extensión (luna.wav -> luna.mp3), borra el viejo. */
    private void borrarClipAnterior(String clipUrlAnterior, String nombreNuevo) {
        if (clipUrlAnterior == null || clipUrlAnterior.isBlank()) return;
        String nombreAnterior = Paths.get(clipUrlAnterior).getFileName().toString();
        if (nombreAnterior.equals(nombreNuevo)) return;
        try {
            Files.deleteIfExists(carpetaClips.resolve(nombreAnterior));
        } catch (IOException e) {
            // un archivo huérfano no debe impedir subir el nuevo
        }
    }

    private static String extensionDe(String nombre) {
        int punto = nombre.lastIndexOf('.');
        return punto < 0 ? "" : nombre.substring(punto + 1).toLowerCase();
    }

    private static String sinExtension(String nombre) {
        int punto = nombre.lastIndexOf('.');
        return punto < 0 ? nombre : nombre.substring(0, punto);
    }
}
