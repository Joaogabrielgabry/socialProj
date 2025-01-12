package br.com.projeto.sistema.security.services;

import br.com.projeto.sistema.security.entities.Lista;
import br.com.projeto.sistema.security.repositories.ListaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ListaService {

    @Autowired
    private ListaRepository listaRepository;

    public List<Lista> findAll() {
        return listaRepository.findAll();
    }

    public Optional<Lista> findById(Long id) {
        return listaRepository.findById(id);
    }

    public Lista save(Lista lista) {
        return listaRepository.save(lista);
    }

    public void deleteById(Long id) {
        listaRepository.deleteById(id);
    }
}
