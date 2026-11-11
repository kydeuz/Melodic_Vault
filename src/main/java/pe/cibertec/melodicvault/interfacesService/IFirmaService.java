package pe.cibertec.melodicvault.interfacesService;

import pe.cibertec.melodicvault.modelo.Firma;

import java.util.List;

public interface IFirmaService {
    Firma firmar(Integer idArtista, String email);
    List<Firma> misFirmas(String email);
}