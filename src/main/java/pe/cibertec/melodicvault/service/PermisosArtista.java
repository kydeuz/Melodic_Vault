package pe.cibertec.melodicvault.service;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import pe.cibertec.melodicvault.excepcion.CredencialesInvalidasException;
import pe.cibertec.melodicvault.excepcion.ProhibidoException;
import pe.cibertec.melodicvault.interfaces.IArtista;
import pe.cibertec.melodicvault.interfaces.IUsuario;
import pe.cibertec.melodicvault.modelo.Artista;
import pe.cibertec.melodicvault.modelo.Usuario;

@Component
public class PermisosArtista {

    private final IUsuario usuarioRepo;
    private final IArtista artistaRepo;

    public PermisosArtista(IUsuario usuarioRepo, IArtista artistaRepo) {
        this.usuarioRepo = usuarioRepo;
        this.artistaRepo = artistaRepo;
    }

    /** Usuario del token. Si no existe (por ejemplo, fue borrado) responde 401. */
    public Usuario usuarioActual(Authentication auth) {
        if (auth == null || auth instanceof AnonymousAuthenticationToken) {
            throw new CredencialesInvalidasException("Sesión inválida o vencida. Entra de nuevo.");
        }
        return usuarioRepo.findByEmail(auth.getName()).orElseThrow(
                () -> new CredencialesInvalidasException("Sesión inválida o vencida. Entra de nuevo."));
    }

    /** 403 si no es ADMIN ni el dueño del artista. */
    public void exigirDuenoOAdmin(Artista artista, Usuario usuario) {
        if ("ADMIN".equals(usuario.getRol())) return;
        boolean esDueno = artistaRepo.findByUsuarioIdUsuario(usuario.getIdUsuario())
                .map(propio -> propio.getIdArtista().equals(artista.getIdArtista()))
                .orElse(false);
        if (!esDueno) {
            throw new ProhibidoException("No tienes permiso sobre este artista");
        }
    }
}