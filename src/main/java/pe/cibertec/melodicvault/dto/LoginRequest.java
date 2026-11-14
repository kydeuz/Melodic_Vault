package pe.cibertec.melodicvault.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequest(
        @NotBlank(message = "El correo es obligatorio")
        String email,

        @NotBlank(message = "La contraseña es obligatoria")
        String password
) {}