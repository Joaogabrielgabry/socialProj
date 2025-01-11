package br.com.projeto.sistema.security.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import br.com.projeto.sistema.security.entities.User;


@Repository("user")
public interface UserRepository extends JpaRepository<User, Integer> {
	Optional<User> findByUsername(String username);
	Boolean existsByUsername(String username);
	Boolean existsByEmail(String email);
	@Query(value = "select * from users where email = :email;", nativeQuery = true)
	public User buscarEmail(String email);
}
