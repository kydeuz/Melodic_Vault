package pe.cibertec.melodicvault.interfaces;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.cibertec.melodicvault.modelo.Genero;

import java.util.List;

public interface IGenero extends JpaRepository<Genero , Integer > {

}
