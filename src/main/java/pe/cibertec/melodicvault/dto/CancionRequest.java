package pe.cibertec.melodicvault.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CancionRequest(
        @NotNull(message = "El álbum es obligatorio")
        Integer albumId,

        @NotBlank(message = "El título es obligatorio")
        @Size(max = 100, message = "El título no puede superar 100 caracteres")
        String titulo,

        @NotNull(message = "El número de pista es obligatorio")
        @Min(value = 1, message = "La pista debe ser 1 o mayor")
        Integer numeroPista) {}