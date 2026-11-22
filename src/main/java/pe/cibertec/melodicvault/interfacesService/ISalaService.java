package pe.cibertec.melodicvault.interfacesService;

import pe.cibertec.melodicvault.modelo.Artista;
import pe.cibertec.melodicvault.modelo.Sala;

public interface ISalaService {

    /** Crea la sala LIVE "Primer live con su gente" cuando el artista llega a la meta. */
    Sala crearLiveInicial(Artista artista);
}
