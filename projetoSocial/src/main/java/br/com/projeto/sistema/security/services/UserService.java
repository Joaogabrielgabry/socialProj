package br.com.projeto.sistema.security.services;

import org.springframework.stereotype.Service;

@Service
public class UserService {

//	@Autowired
//    private UserRepository userRepository;
//
//    @Autowired
//    private BCryptPasswordEncoder passwordEncoder;
    
//    public boolean adicionarEndereco(String username, String password, EnderecoRequestDTO enderecoDto) {
//        Optional<User> userOpt = userRepository.findByUsername(username);
//
//        if (userOpt.isPresent()) {
//            User user = userOpt.get();
//            if (passwordEncoder.matches(password, user.getPassword())) {
//                Endereco endereco = new Endereco();
//              endereco = enderecoService.criarEndereco(enderecoDto); 
//                endereco = enderecoService.consultarEndereco(enderecoDto).toEndereco();
//                enderecoRepository.save(endereco);
//                user.setEndereco(endereco);
//                userRepository.save(user);   
//                
//                return true;
//            }
//        }
//        return false;
//    }
}
