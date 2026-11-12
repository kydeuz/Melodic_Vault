package pe.cibertec.melodicvault.dto;

public record RegistroResponse(
        int id,
        String email,
        String rol,
        String ciudad
) {}