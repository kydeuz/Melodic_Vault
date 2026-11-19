package pe.cibertec.melodicvault.dto;

import java.util.List;

public record AlbumResumen(Integer id, String titulo, Integer anio,
                           List<CancionResumen> canciones) {}