package pe.cibertec.melodicvault.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.cibertec.melodicvault.dto.SalaRequest;
import pe.cibertec.melodicvault.dto.SalaResponse;
import pe.cibertec.melodicvault.excepcion.ConflictoException;
import pe.cibertec.melodicvault.excepcion.RecursoNoEncontradoException;
import pe.cibertec.melodicvault.interfaces.IArtista;
import pe.cibertec.melodicvault.interfaces.ISala;
import pe.cibertec.melodicvault.interfacesService.ISalaService;
import pe.cibertec.melodicvault.modelo.Artista;
import pe.cibertec.melodicvault.modelo.Sala;
import pe.cibertec.melodicvault.modelo.Usuario;

import java.time.LocalDateTime;

@Service
public class SalaService implements ISalaService {

    private static final String TIPO_LIVE = "LIVE";
    private static final String TITULO_LIVE_INICIAL = "Primer live con su gente";
    private static final int DIAS_HASTA_LIVE_INICIAL = 7;

    private final ISala salaRepo;
    private final IArtista artistaRepo;
    private final PermisosArtista permisos;

    public SalaService(ISala salaRepo, IArtista artistaRepo, PermisosArtista permisos) {
        this.salaRepo = salaRepo;
        this.artistaRepo = artistaRepo;
        this.permisos = permisos;
    }

    @Override
    @Transactional
    public Sala crearLiveInicial(Artista artista) {
        Sala sala = new Sala();
        sala.setArtista(artista);
        sala.setTipo(TIPO_LIVE);
        sala.setTitulo(TITULO_LIVE_INICIAL);
        sala.setFecha(LocalDateTime.now().plusDays(DIAS_HASTA_LIVE_INICIAL));
        // lugar vacío: la entidad lo guarda como "Por confirmar" (@PrePersist)
        return salaRepo.save(sala);
    }

    @Override
    @Transactional
    public SalaResponse crear(SalaRequest req, Usuario usuario) {
        Artista artista = artistaRepo.findById(req.artistaId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Artista no encontrado"));
        permisos.exigirDuenoOAdmin(artista, usuario);

        // un LIVE solo se publica si el artista ya llegó a la meta de firmas
        if (TIPO_LIVE.equals(req.tipo()) && !Boolean.TRUE.equals(artista.getCanalHabilitado())) {
            throw new ConflictoException("El canal de este artista aún no está habilitado");
        }

        Sala sala = new Sala();
        sala.setArtista(artista);
        sala.setTipo(req.tipo());
        sala.setTitulo(req.titulo());
        sala.setLugar(req.lugar());
        sala.setFecha(req.fecha());
        return aResponse(salaRepo.save(sala));
    }

    @Override
    @Transactional
    public void eliminar(int id, Usuario usuario) {
        Sala sala = salaRepo.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Sala no encontrada"));
        permisos.exigirDuenoOAdmin(sala.getArtista(), usuario);
        salaRepo.delete(sala);
    }

    private SalaResponse aResponse(Sala sala) {
        return new SalaResponse(sala.getIdSala(), sala.getArtista().getIdArtista(),
                sala.getTipo(), sala.getTitulo(), sala.getLugar(), sala.getFecha());
    }
}
