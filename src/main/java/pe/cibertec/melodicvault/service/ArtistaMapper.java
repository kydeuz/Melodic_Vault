package pe.cibertec.melodicvault.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import pe.cibertec.melodicvault.dto.*;
import pe.cibertec.melodicvault.interfaces.*;
import pe.cibertec.melodicvault.modelo.Artista;
import pe.cibertec.melodicvault.modelo.Firma;
import pe.cibertec.melodicvault.modelo.Usuario;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Component
public class ArtistaMapper {

    private static final ZoneId LIMA = ZoneId.of("America/Lima");

    private final ISala salaRepo;
    private final IFirma firmaRepo;
    private final IAlbum albumRepo;
    private final ICancion cancionRepo;
    private final IUsuario usuarioRepo;
    private final int meta;

    public ArtistaMapper(ISala salaRepo, IFirma firmaRepo, IAlbum albumRepo,
                         ICancion cancionRepo, IUsuario usuarioRepo,
                         @Value("${melodicvault.firmas.meta}") int meta) {
        this.salaRepo = salaRepo;
        this.firmaRepo = firmaRepo;
        this.albumRepo = albumRepo;
        this.cancionRepo = cancionRepo;
        this.usuarioRepo = usuarioRepo;
        this.meta = meta;
    }

    public Integer idUsuario(String email) {
        if (email == null) return null;
        return usuarioRepo.findByEmail(email).map(Usuario::getIdUsuario).orElse(null);
    }

    public ArtistaResumen resumen(Artista a, Integer idUsuario) {
        List<SalaResumen> salas = salasFuturas(a);
        return new ArtistaResumen(
                a.getIdArtista(), a.getSlug(), a.getNombre(),
                a.getGenero().getIdGenero(), a.getGenero().getNombre(),
                a.getFirmas(), meta, a.getCanalHabilitado(), miNumero(a, idUsuario),
                a.getClipUrl(), a.getClipTitulo(), a.getClipInicioSeg(),
                salas.isEmpty() ? null : salas.get(0), ciudades(a));
    }

    public ArtistaDetalle detalle(Artista a, Integer idUsuario) {
        List<SalaResumen> salas = salasFuturas(a);
        return new ArtistaDetalle(
                a.getIdArtista(), a.getSlug(), a.getNombre(),
                a.getGenero().getIdGenero(), a.getGenero().getNombre(),
                a.getFirmas(), meta, a.getCanalHabilitado(), miNumero(a, idUsuario),
                a.getClipUrl(), a.getClipTitulo(), a.getClipInicioSeg(),
                a.getSpotifyUrl(), a.getYoutubeUrl(), a.getInstagramUrl(), a.getTiktokUrl(),
                a.getVersion(),
                salas.isEmpty() ? null : salas.get(0), ciudades(a), salas, albumes(a));
    }

    private Integer miNumero(Artista a, Integer idUsuario) {
        if (idUsuario == null) return null;
        return firmaRepo.findByUsuarioIdUsuarioAndArtistaIdArtista(idUsuario, a.getIdArtista())
                .map(Firma::getNumero).orElse(null);
    }

    private List<SalaResumen> salasFuturas(Artista a) {
        return salaRepo.findByArtistaIdArtistaAndFechaAfterOrderByFechaAsc(
                        a.getIdArtista(), LocalDateTime.now(LIMA)).stream()
                .map(s -> new SalaResumen(s.getIdSala(), s.getTipo(), s.getTitulo(),
                        s.getLugar(), s.getFecha()))
                .toList();
    }

    private List<CiudadFirmas> ciudades(Artista a) {
        return firmaRepo.ciudadesPorArtista(a.getIdArtista()).stream()
                .map(f -> new CiudadFirmas((String) f[0], ((Number) f[1]).longValue()))
                .toList();
    }

    private List<AlbumResumen> albumes(Artista a) {
        return albumRepo.findByArtistaIdArtistaOrderByAnioDesc(a.getIdArtista()).stream()
                .map(al -> new AlbumResumen(al.getIdAlbum(), al.getTitulo(), al.getAnio(),
                        cancionRepo.findByAlbumIdAlbumOrderByNumeroPista(al.getIdAlbum()).stream()
                                .map(c -> new CancionResumen(c.getIdCancion(), c.getTitulo(), c.getNumeroPista()))
                                .toList()))
                .toList();
    }
}