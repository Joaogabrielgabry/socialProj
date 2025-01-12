package br.com.projeto.sistema.security.controllers;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.projeto.sistema.security.dto.JwtResponseDTO;
import br.com.projeto.sistema.security.dto.LoginRequestDTO;
import br.com.projeto.sistema.security.dto.MessageResponseDTO;
import br.com.projeto.sistema.security.dto.SignupRequestDTO;
import br.com.projeto.sistema.security.entities.Role;
import br.com.projeto.sistema.security.entities.User;
import br.com.projeto.sistema.security.enums.RoleEnum;
import br.com.projeto.sistema.security.jwt.JwtUtils;
import br.com.projeto.sistema.security.repositories.RoleRepository;
import br.com.projeto.sistema.security.repositories.UserRepository;
import br.com.projeto.sistema.security.services.UserDetailsImpl;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder encoder;
    @Autowired
    JwtUtils jwtUtils;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequestDTO loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream().map(item -> item.getAuthority())
                .collect(Collectors.toList());

        return ResponseEntity.ok(
                new JwtResponseDTO(jwt, userDetails.getId(), userDetails.getUsername(), userDetails.getEmail(), roles));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequestDTO signUpRequest) {
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            return ResponseEntity.badRequest().body(new MessageResponseDTO("Erro: Username já utilizado!"));
        }

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity.badRequest().body(new MessageResponseDTO("Erro: Email já utilizado!"));
        }

        User user = new User(signUpRequest.getUsername(), signUpRequest.getEmail(),
                encoder.encode(signUpRequest.getPassword()));

        Set<String> strRoles = signUpRequest.getRole();
        Set<Role> roles = new HashSet<>();

        if (strRoles == null) {
            Role userRoleA = roleRepository.findByName(RoleEnum.ROLE_USER_A)
                    .orElseThrow(() -> new RuntimeException("Erro: Role não encontrada."));
            roles.add(userRoleA);
        } else {
            strRoles.forEach(role -> {
                switch (role) {
                    case "ROLE_USER_A" -> {
                        Role userRoleA = roleRepository.findByName(RoleEnum.ROLE_USER_A)
                                .orElseThrow(() -> new RuntimeException("Erro: Role não encontrada."));
                        roles.add(userRoleA);
                    }
                    case "ROLE_USER_B" -> {
                        Role userRoleB = roleRepository.findByName(RoleEnum.ROLE_USER_B)
                                .orElseThrow(() -> new RuntimeException("Erro: Role não encontrada."));
                        roles.add(userRoleB);
                    }
                }
            });
        }

        user.setRoles(roles);
        userRepository.save(user);
        return ResponseEntity.ok(new MessageResponseDTO("Usuário registrado com sucesso!"));
    }

    @SecurityRequirement(name = "bearer Auth")
    @PreAuthorize("hasRole('ROLE_USER_A')")
    @GetMapping("/users")
    public ResponseEntity<Object> getAllUsers() {
        List<User> users = userRepository.findAll();
        return ResponseEntity.ok(users);
    }

    @SecurityRequirement(name = "bearer Auth")
    @PreAuthorize("hasRole('ROLE_USER_A') or hasRole('ROLE_USER_B')")
    @GetMapping("/users/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Integer id) {
        return userRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseThrow();
    }

    @SecurityRequirement(name = "bearer Auth")
    @PreAuthorize("hasRole('ROLE_USER_A') or hasRole('ROLE_USER_B')")
    @PutMapping("/users/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Integer id, @Valid @RequestBody SignupRequestDTO userDetails) {
        return userRepository.findById(id).map(user -> {
            user.setUsername(userDetails.getUsername());
            user.setEmail(userDetails.getEmail());
            if (userDetails.getPassword() != null && !userDetails.getPassword().isEmpty()) {
                user.setPassword(encoder.encode(userDetails.getPassword()));
            }
            Set<String> strRoles = userDetails.getRole();
            Set<Role> roles = new HashSet<>();

            if (strRoles == null || strRoles.isEmpty()) {
                Role defaultRole = roleRepository.findByName(RoleEnum.ROLE_USER_A)
                        .orElseThrow(() -> new RuntimeException("Erro: Role não encontrada."));
                roles.add(defaultRole);
            } else {
                for (String role : strRoles) {
                    Role userRole = roleRepository.findByName(RoleEnum.valueOf(role))
                            .orElseThrow(() -> new RuntimeException("Erro: Role " + role + " não encontrada."));
                    roles.add(userRole);
                }
            }
            user.setRoles(roles);
            userRepository.save(user);
            return ResponseEntity.ok(new MessageResponseDTO("Usuário atualizado com sucesso!"));
        }).orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new MessageResponseDTO("Usuário não encontrado!")));
    }

    @SecurityRequirement(name = "bearer Auth")
    @PreAuthorize("hasRole('ROLE_USER_A') or hasRole('ROLE_USER_B')")
    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Integer id) {
        return userRepository.findById(id).map(user -> {
            userRepository.delete(user);
            return ResponseEntity.ok(new MessageResponseDTO("Usuário excluído com sucesso!"));
        }).orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new MessageResponseDTO("Usuário não encontrado!")));
    }
}
