package pe.cibertec.melodicvault.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pe.cibertec.melodicvault.dto.SalaRequest;
import pe.cibertec.melodicvault.dto.SalaResponse;
import pe.cibertec.melodicvault.interfacesService.ISalaService;
import pe.cibertec.melodicvault.service.PermisosArtista;

@RestController
@RequestMapping("/api/salas")
public class SalaController {

    private final ISalaService salaService;
    private final PermisosArtista permisos;

    public SalaController(ISalaService salaService, PermisosArtista permisos) {
        this.salaService = salaService;
        this.permisos = permisos;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SalaResponse crear(@Valid @RequestBody SalaRequest req, Authentication auth) {
        return salaService.crear(req, permisos.usuarioActual(auth));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable int id, Authentication auth) {
        salaService.eliminar(id, permisos.usuarioActual(auth));
    }
}
