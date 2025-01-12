package br.com.projeto.sistema.security.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.projeto.sistema.security.entities.Item;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findByListaId(Long listaId);
}
