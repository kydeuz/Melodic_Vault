package pe.cibertec.melodicvault.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record SalaRequest(
        @NotNull(message = "El artista es obligatorio")
        Integer artistaId,

        @NotBlank(message = "El tipo es obligatorio")
        @Pattern(regexp = "GIRA|LIVE", message = "El tipo debe ser GIRA o LIVE")
        String tipo,

        @NotBlank(message = "El título es obligatorio")
        @Size(max = 100, message = "El título no puede superar 100 caracteres")
        String titulo,

        @Size(max = 120, message = "El lugar no puede superar 120 caracteres")
        String lugar,

        @NotNull(message = "La fecha es obligatoria")
        @Future(message = "La fecha debe ser futura")
        LocalDateTime fecha) {}
