package pe.cibertec.melodicvault.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.cibertec.melodicvault.interfacesService.IArtistaService;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.cibertec.melodicvault.interfaces.IArtista;
import pe.cibertec.melodicvault.modelo.Artista;

import java.util.Optional;

@Service
public class ArtistaService implements IArtistaService {

    @Autowired
    private IArtista data;

    @Override
    public Page<Artista> listar(Pageable pageable) {
        return data.findAll(pageable);
    }

    @Override
    public Optional<Artista> listarId(int id) {
        return data.findById(id);
    }

    @Override
    public Optional<Artista> buscarPorSlug(String slug) {
        return data.findBySlug(slug);
    }

    @Override
    public Optional<Artista> buscarPorUsuario(int idUsuario) {
        return data.findByUsuarioIdUsuario(idUsuario);
    }

    @Override
    @Transactional
    public Artista save(Artista artista) {
        return data.save(artista);
    }

    @Override
    @Transactional
    public void delete(int id) {
        data.deleteById(id);
    }
}