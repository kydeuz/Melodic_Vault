package pe.cibertec.melodicvault.dto;

import java.util.List;

public record ArtistaResumen(
        Integer id, String slug, String nombre, Integer generoId, String genero,
        Integer firmas, Integer meta, Boolean canalHabilitado, Integer miNumero,
        String clipUrl, String clipTitulo, Integer clipInicioSeg,
        SalaResumen proximaSala, List<CiudadFirmas> ciudades) {}
