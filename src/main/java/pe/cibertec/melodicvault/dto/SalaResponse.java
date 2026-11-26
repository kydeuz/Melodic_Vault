package pe.cibertec.melodicvault.dto;

import java.time.LocalDateTime;

public record SalaResponse(Integer id, Integer artistaId, String tipo, String titulo,
                           String lugar, LocalDateTime fecha) {}
