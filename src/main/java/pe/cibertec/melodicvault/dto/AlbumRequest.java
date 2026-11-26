package pe.cibertec.melodicvault.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AlbumRequest(
        @NotNull(message = "El artista es obligatorio")
        Integer artistaId,

        @NotBlank(message = "El título es obligatorio")
        @Size(max = 100, message = "El título no puede superar 100 caracteres")
        String titulo,

        @NotNull(message = "El año es obligatorio")
        Integer anio) {}
