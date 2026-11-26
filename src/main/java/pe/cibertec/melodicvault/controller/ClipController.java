package pe.cibertec.melodicvault.controller;

import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pe.cibertec.melodicvault.dto.ClipResponse;
import pe.cibertec.melodicvault.service.ClipService;
import pe.cibertec.melodicvault.service.PermisosArtista;

@RestController
@RequestMapping("/api/artistas")
public class ClipController {

    private final ClipService clipService;
    private final PermisosArtista permisos;

    public ClipController(ClipService clipService, PermisosArtista permisos) {
        this.clipService = clipService;
        this.permisos = permisos;
    }

    @PostMapping(value = "/{id}/clip", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ClipResponse subir(@PathVariable int id,
                              @RequestParam("archivo") MultipartFile archivo,
                              Authentication auth) {
        return clipService.subir(id, archivo, permisos.usuarioActual(auth));
    }
}