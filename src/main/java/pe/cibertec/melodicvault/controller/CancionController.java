package pe.cibertec.melodicvault.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pe.cibertec.melodicvault.dto.CancionRequest;
import pe.cibertec.melodicvault.dto.CancionResponse;
import pe.cibertec.melodicvault.service.CancionGestionService;
import pe.cibertec.melodicvault.service.PermisosArtista;

@RestController
@RequestMapping("/api/canciones")
public class CancionController {

    private final CancionGestionService gestion;
    private final PermisosArtista permisos;

    public CancionController(CancionGestionService gestion, PermisosArtista permisos) {
        this.gestion = gestion;
        this.permisos = permisos;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CancionResponse crear(@Valid @RequestBody CancionRequest req, Authentication auth) {
        return gestion.crear(req, permisos.usuarioActual(auth));
    }

    @PutMapping("/{id}")
    public CancionResponse editar(@PathVariable int id,
                                  @Valid @RequestBody CancionRequest req, Authentication auth) {
        return gestion.editar(id, req, permisos.usuarioActual(auth));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable int id, Authentication auth) {
        gestion.eliminar(id, permisos.usuarioActual(auth));
    }
}