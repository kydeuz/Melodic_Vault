package pe.cibertec.melodicvault.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.cibertec.melodicvault.interfaces.ISala;
import pe.cibertec.melodicvault.interfacesService.ISalaService;
import pe.cibertec.melodicvault.modelo.Artista;
import pe.cibertec.melodicvault.modelo.Sala;

import java.time.LocalDateTime;

@Service
public class SalaService implements ISalaService {

    private static final String TITULO_LIVE_INICIAL = "Primer live con su gente";
    private static final int DIAS_HASTA_LIVE_INICIAL = 7;

    private final ISala salaRepo;

    public SalaService(ISala salaRepo) {
        this.salaRepo = salaRepo;
    }

    @Override
    @Transactional
    public Sala crearLiveInicial(Artista artista) {
        Sala sala = new Sala();
        sala.setArtista(artista);
        sala.setTipo("LIVE");
        sala.setTitulo(TITULO_LIVE_INICIAL);
        sala.setFecha(LocalDateTime.now().plusDays(DIAS_HASTA_LIVE_INICIAL));
        // lugar vacío: la entidad lo guarda como "Por confirmar" (@PrePersist)
        return salaRepo.save(sala);
    }
}
