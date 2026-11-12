package pe.cibertec.melodicvault.dto;

public record LoginResponse(
        String token,
        String email,
        String rol,
        String ciudad,
        Integer artistaId
) {}