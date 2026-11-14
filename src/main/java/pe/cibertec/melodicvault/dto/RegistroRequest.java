package pe.cibertec.melodicvault.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegistroRequest(

        @NotBlank(message = "El correo es obligatorio")
        @Email(message = "El correo no es válido")
        @Size(max = 100)
        String email,

        @NotBlank(message = "La contraseña es obligatoria")
        @Size(min = 6, max = 72, message = "Entre 6 y 72 caracteres")
        String password,

        @NotBlank(message = "La ciudad es obligatoria")
        @Pattern(regexp = "Lima|Callao|Arequipa|Trujillo|Cusco|Piura|Chiclayo",
                message = "Ciudad no válida")
        String ciudad
) {}