package pe.cibertec.melodicvault.excepcion;

public class CorreoDuplicadoException extends RuntimeException {
    public CorreoDuplicadoException(String mensaje) { super(mensaje); }
}