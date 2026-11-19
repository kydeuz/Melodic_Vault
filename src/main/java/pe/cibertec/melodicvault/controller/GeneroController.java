package pe.cibertec.melodicvault.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pe.cibertec.melodicvault.dto.GeneroResponse;
import pe.cibertec.melodicvault.interfacesService.IGeneroService;

import java.util.List;

@RestController
@RequestMapping("/api/generos")
public class GeneroController {

    private final IGeneroService generoService;

    public GeneroController(IGeneroService generoService) {
        this.generoService = generoService;
    }

    @GetMapping
    public List<GeneroResponse> listar() {
        return generoService.listar().stream()
                .map(g -> new GeneroResponse(g.getIdGenero(), g.getNombre()))
                .toList();
    }
}