package pe.cibertec.melodicvault.excepcion;

import java.util.Map;

public class DatosInvalidosException extends RuntimeException {
    private final Map<String, String> campos;
    public DatosInvalidosException(Map<String, String> campos) {
        super("Datos inválidos");
        this.campos = campos;
    }
    public Map<String, String> getCampos() { return campos; }
}