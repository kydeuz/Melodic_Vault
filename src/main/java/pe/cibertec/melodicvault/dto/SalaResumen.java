package pe.cibertec.melodicvault.dto;

import java.time.LocalDateTime;

public record SalaResumen(Integer id, String tipo, String titulo,
                          String lugar, LocalDateTime fecha) {}