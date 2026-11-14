package pe.cibertec.melodicvault.dto;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(String error, Map<String, String> campos) {}