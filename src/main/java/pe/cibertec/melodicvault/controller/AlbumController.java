package pe.cibertec.melodicvault.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pe.cibertec.melodicvault.dto.AlbumRequest;
import pe.cibertec.melodicvault.dto.AlbumResponse;
import pe.cibertec.melodicvault.service.AlbumGestionService;
import pe.cibertec.melodicvault.service.PermisosArtista;

@RestController
@RequestMapping("/api/albumes")
public class AlbumController {

    private final AlbumGestionService gestion;
    private final PermisosArtista permisos;

    public AlbumController(AlbumGestionService gestion, PermisosArtista permisos) {
        this.gestion = gestion;
        this.permisos = permisos;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AlbumResponse crear(@Valid @RequestBody AlbumRequest req, Authentication auth) {
        return gestion.crear(req, permisos.usuarioActual(auth));
    }

    @PutMapping("/{id}")
    public AlbumResponse editar(@PathVariable int id,
                                @Valid @RequestBody AlbumRequest req, Authentication auth) {
        return gestion.editar(id, req, permisos.usuarioActual(auth));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable int id, Authentication auth) {
        gestion.eliminar(id, permisos.usuarioActual(auth));
    }
}
