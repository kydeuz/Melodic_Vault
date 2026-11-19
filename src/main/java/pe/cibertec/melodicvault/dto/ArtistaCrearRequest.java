package pe.cibertec.melodicvault.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ArtistaCrearRequest(
        @NotBlank(message = "El enlace es obligatorio")
        @Pattern(regexp = "^(?=.*[a-z])[a-z0-9-]{3,30}$",
                message = "Entre 3 y 30 caracteres: minúsculas, números y guiones, con al menos una letra")
        String slug,
        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 80, message = "El nombre no puede superar 80 caracteres")
        String nombre,
        @NotNull(message = "Debe seleccionar un género")
        Integer generoId,
        @Size(max = 255) String spotifyUrl,
        @Size(max = 255) String youtubeUrl,
        @Size(max = 255) String instagramUrl,
        @Size(max = 255) String tiktokUrl) {}
