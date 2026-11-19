package pe.cibertec.melodicvault.dto;

import java.util.List;

public record ArtistaDetalle(
        Integer id, String slug, String nombre, Integer generoId, String genero,
        Integer firmas, Integer meta, Boolean canalHabilitado, Integer miNumero,
        String clipUrl, String clipTitulo, Integer clipInicioSeg,
        String spotifyUrl, String youtubeUrl, String instagramUrl, String tiktokUrl,
        Integer version, SalaResumen proximaSala, List<CiudadFirmas> ciudades,
        List<SalaResumen> salas, List<AlbumResumen> albumes) {}
