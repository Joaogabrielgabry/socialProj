package br.com.projeto.sistema.security.controllers;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
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
    public ResponseEntity<?> registerUser(@Valid @RequestPart SignupRequestDTO signUpRequest) throws IOException {
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
                    case "A" -> {
                        Role userRoleA = roleRepository.findByName(RoleEnum.ROLE_USER_A)
                                .orElseThrow(() -> new RuntimeException("Erro: Role não encontrada."));
                        roles.add(userRoleA);
                    }
                    case "B" -> {
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
}
