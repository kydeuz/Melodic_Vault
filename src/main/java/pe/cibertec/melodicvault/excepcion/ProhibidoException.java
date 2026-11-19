package pe.cibertec.melodicvault.excepcion;

public class ProhibidoException extends RuntimeException {
    public ProhibidoException(String mensaje) { super(mensaje); }
}
