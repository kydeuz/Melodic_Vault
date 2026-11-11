package pe.cibertec.melodicvault.service;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import pe.cibertec.melodicvault.interfaces.IGenero;
import pe.cibertec.melodicvault.interfacesService.IGeneroService;
import pe.cibertec.melodicvault.modelo.Genero;

import java.util.List;

@Service
public class GeneroService implements IGeneroService {

    private final IGenero generoRepo;

    public GeneroService(IGenero generoRepo) {
        this.generoRepo = generoRepo;
    }

    @Override
    public List<Genero> listar() {
        return generoRepo.findAll(Sort.by(Sort.Direction.ASC, "nombre"));
    }
}