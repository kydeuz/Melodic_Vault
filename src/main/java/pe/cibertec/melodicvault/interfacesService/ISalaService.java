package pe.cibertec.melodicvault.interfacesService;

import pe.cibertec.melodicvault.dto.SalaRequest;
import pe.cibertec.melodicvault.dto.SalaResponse;
import pe.cibertec.melodicvault.modelo.Artista;
import pe.cibertec.melodicvault.modelo.Sala;
import pe.cibertec.melodicvault.modelo.Usuario;

public interface ISalaService {

    /** Crea la sala LIVE "Primer live con su gente" cuando el artista llega a la meta. */
    Sala crearLiveInicial(Artista artista);

    SalaResponse crear(SalaRequest req, Usuario usuario);

    void eliminar(int id, Usuario usuario);
}
