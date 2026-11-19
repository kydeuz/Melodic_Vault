package pe.cibertec.melodicvault.controller;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pe.cibertec.melodicvault.dto.*;
import pe.cibertec.melodicvault.excepcion.RecursoNoEncontradoException;
import pe.cibertec.melodicvault.interfacesService.IArtistaService;
import pe.cibertec.melodicvault.modelo.Artista;
import pe.cibertec.melodicvault.modelo.Usuario;
import pe.cibertec.melodicvault.service.ArtistaGestionService;
import pe.cibertec.melodicvault.service.ArtistaMapper;
import pe.cibertec.melodicvault.service.PermisosArtista;

import java.util.List;

@RestController
@RequestMapping("/api/artistas")
public class ArtistaController {

    private final IArtistaService artistaService;
    private final ArtistaMapper mapper;
    private final ArtistaGestionService gestion;
    private final PermisosArtista permisos;

    public ArtistaController(IArtistaService artistaService, ArtistaMapper mapper,
                             ArtistaGestionService gestion, PermisosArtista permisos) {
        this.artistaService = artistaService;
        this.mapper = mapper;
        this.gestion = gestion;
        this.permisos = permisos;
    }


    @GetMapping
    public PaginaResponse<ArtistaResumen> listar(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication auth) {

        int p = Math.max(page, 0);
        int s = Math.min(Math.max(size, 1), 50);

        Page<Artista> pagina = artistaService.listar(
                PageRequest.of(p, s, Sort.by(Sort.Direction.DESC, "idArtista")));

        Integer idUsuario = mapper.idUsuario(emailDe(auth));
        List<ArtistaResumen> content = pagina.getContent().stream()
                .map(a -> mapper.resumen(a, idUsuario)).toList();

        return new PaginaResponse<>(content, pagina.getNumber(), pagina.getSize(),
                pagina.getTotalElements(), pagina.getTotalPages());
    }

    @GetMapping("/{idOSlug}")
    public ArtistaDetalle detalle(@PathVariable String idOSlug, Authentication auth) {
        var artista = idOSlug.matches("\\d{1,9}")
                ? artistaService.listarId(Integer.parseInt(idOSlug))
                : artistaService.buscarPorSlug(idOSlug);

        Artista a = artista.orElseThrow(
                () -> new RecursoNoEncontradoException("Artista no encontrado"));

        return mapper.detalle(a, mapper.idUsuario(emailDe(auth)));
    }

    private String emailDe(Authentication auth) {
        if (auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken) {
            return null;
        }
        return auth.getName();   // eemail como principal
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ArtistaDetalle crear(@Valid @RequestBody ArtistaCrearRequest req, Authentication auth) {
        Usuario usuario = permisos.usuarioActual(auth);
        return mapper.detalle(gestion.crear(req, usuario), usuario.getIdUsuario());
    }

    @PutMapping("/{id}")
    public ArtistaDetalle editar(@PathVariable int id,
                                 @Valid @RequestBody ArtistaEditarRequest req, Authentication auth) {
        Usuario usuario = permisos.usuarioActual(auth);
        return mapper.detalle(gestion.editar(id, req, usuario), usuario.getIdUsuario());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable int id, Authentication auth) {
        gestion.eliminar(id, permisos.usuarioActual(auth));
    }
}