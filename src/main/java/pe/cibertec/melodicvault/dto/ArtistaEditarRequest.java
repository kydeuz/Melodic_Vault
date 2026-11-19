package pe.cibertec.melodicvault.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ArtistaEditarRequest(
        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 80, message = "El nombre no puede superar 80 caracteres")
        String nombre,
        @NotNull(message = "Debe seleccionar un género")
        Integer generoId,
        @Min(value = 0, message = "El segundo de inicio no puede ser negativo")
        Integer clipInicioSeg,
        @NotNull(message = "La versión es obligatoria")
        Integer version,
        @Size(max = 255) String spotifyUrl,
        @Size(max = 255) String youtubeUrl,
        @Size(max = 255) String instagramUrl,
        @Size(max = 255) String tiktokUrl) {}