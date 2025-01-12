package br.com.projeto.sistema.security.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.projeto.sistema.security.dto.ItemDTO;
import br.com.projeto.sistema.security.dto.ItemResponseDTO;
import br.com.projeto.sistema.security.dto.ListaDTO;
import br.com.projeto.sistema.security.dto.ListaResponseDTO;
import br.com.projeto.sistema.security.entities.Item;
import br.com.projeto.sistema.security.entities.Lista;
import br.com.projeto.sistema.security.repositories.ItemRepository;
import br.com.projeto.sistema.security.services.ListaService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping("/listas")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ListaController {

    @Autowired
    private ListaService listaService;

    @Autowired
    private ItemRepository itemRepository;

    @SecurityRequirement(name = "bearer Auth")
    @PreAuthorize("hasRole('ROLE_USER_A') or hasRole('ROLE_USER_B')")
    @PostMapping
    public ResponseEntity<?> createListaComItens(@RequestBody ListaDTO listaDTO) {
        Lista lista = new Lista();
        lista.setNome(listaDTO.getNome());
        lista.setDescricao(listaDTO.getDescricao());

        List<Item> itemsToSave = new ArrayList<>();
        for (ItemDTO itemDTO : listaDTO.getItens()) {
            Item item = new Item();
            item.setNome(itemDTO.getNome());
            item.setDescricao(itemDTO.getDescricao());
            item.setLista(lista);
            itemsToSave.add(item);
        }
        lista.setItens(itemsToSave);
        listaService.save(lista);
        return ResponseEntity.status(HttpStatus.CREATED).body("Lista criada com sucesso!");
    }

    @SecurityRequirement(name = "bearer Auth")
    @PreAuthorize("hasRole('ROLE_USER_A') or hasRole('ROLE_USER_B')")
    @GetMapping
    public ResponseEntity<List<ListaResponseDTO>> getAllListas() {
        List<Lista> listas = listaService.findAll();

        List<ListaResponseDTO> listaResponseDTOs = listas.stream().map(lista -> {
            ListaResponseDTO dto = new ListaResponseDTO();
            dto.setId(lista.getId());
            dto.setNome(lista.getNome());
            dto.setDescricao(lista.getDescricao());

            List<ItemResponseDTO> itemDTOs = lista.getItens().stream().map(item -> {
                return new ItemResponseDTO(item.getId(), item.getNome(), item.getDescricao());
            }).collect(Collectors.toList());

            dto.setItens(itemDTOs);
            return dto;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(listaResponseDTOs);
    }

    @SecurityRequirement(name = "bearer Auth")
    @PreAuthorize("hasRole('ROLE_USER_A') or hasRole('ROLE_USER_B')")
    @GetMapping("/{id}")
    public ResponseEntity<ListaResponseDTO> getListaById(@PathVariable Long id) {
        Optional<Lista> listaOptional = listaService.findById(id);

        if (listaOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        Lista lista = listaOptional.get();
        ListaResponseDTO dto = new ListaResponseDTO();
        dto.setId(lista.getId());
        dto.setNome(lista.getNome());
        dto.setDescricao(lista.getDescricao());

        List<ItemResponseDTO> itemDTOs = lista.getItens().stream().map(item -> {
            return new ItemResponseDTO(item.getId(), item.getNome(), item.getDescricao());
        }).collect(Collectors.toList());

        dto.setItens(itemDTOs);
        return ResponseEntity.ok(dto);
    }

    @SecurityRequirement(name = "bearer Auth")
    @PreAuthorize("hasRole('ROLE_USER_A') or hasRole('ROLE_USER_B')")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateLista(@PathVariable Long id, @RequestBody ListaDTO listaDTO) {
        Optional<Lista> listaOptional = listaService.findById(id);

        if (listaOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Lista não encontrada!");
        }

        Lista lista = listaOptional.get();
        lista.setNome(listaDTO.getNome());
        lista.setDescricao(listaDTO.getDescricao());

        List<Item> itemsToSave = new ArrayList<>();
        for (ItemDTO itemDTO : listaDTO.getItens()) {
            Item item = new Item();
            item.setNome(itemDTO.getNome());
            item.setDescricao(itemDTO.getDescricao());
            item.setLista(lista);
            itemsToSave.add(item);
        }

        lista.setItens(itemsToSave);
        listaService.save(lista);
        return ResponseEntity.ok("Lista atualizada com sucesso!");
    }

    @SecurityRequirement(name = "bearer Auth")
    @PreAuthorize("hasRole('ROLE_USER_A') or hasRole('ROLE_USER_B')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteLista(@PathVariable Long id) {
        Optional<Lista> listaOptional = listaService.findById(id);

        if (listaOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Lista não encontrada!");
        }

        listaService.deleteById(id);
        return ResponseEntity.ok("Lista removida com sucesso!");
    }

    @SecurityRequirement(name = "bearer Auth")
    @PreAuthorize("hasRole('ROLE_USER_A') or hasRole('ROLE_USER_B')")
    @DeleteMapping("/{id}/itens/{itemId}")
    public ResponseEntity<?> deleteItemFromLista(@PathVariable Long id, @PathVariable Long itemId) {
        Optional<Item> itemOptional = itemRepository.findById(itemId);

        if (itemOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Item não encontrado!");
        }

        Item item = itemOptional.get();
        if (!item.getLista().getId().equals(id)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("O item não pertence à lista especificada!");
        }

        itemRepository.delete(item);
        return ResponseEntity.ok("Item removido com sucesso!");
    }
}
