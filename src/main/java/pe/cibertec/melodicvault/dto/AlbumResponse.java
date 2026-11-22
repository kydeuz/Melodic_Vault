package pe.cibertec.melodicvault.dto;

import java.util.List;

public record AlbumResponse(Integer id, Integer artistaId, String titulo,
                            Integer anio, List<CancionResumen> canciones) {}

