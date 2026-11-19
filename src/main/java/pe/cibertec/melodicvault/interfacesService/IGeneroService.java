package pe.cibertec.melodicvault.interfacesService;

import pe.cibertec.melodicvault.modelo.Genero;

import java.util.List;
import java.util.Optional;

public interface IGeneroService {
    List<Genero> listar();

    Optional<Genero> buscarPorId(int id);
}
