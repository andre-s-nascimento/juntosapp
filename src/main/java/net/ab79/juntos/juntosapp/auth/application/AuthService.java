package net.ab79.juntos.juntosapp.auth.application;

import net.ab79.juntos.juntosapp.users.domain.model.User;
import net.ab79.juntos.juntosapp.users.domain.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
  private final UserRepository userRepository;
  private final JwtService jwtService;
  private final BCryptPasswordEncoder passwordEncoder;

  public AuthService(
      UserRepository userRepository, JwtService jwtService, BCryptPasswordEncoder passwordEncoder) {
    this.userRepository = userRepository;
    this.jwtService = jwtService;
    this.passwordEncoder = passwordEncoder;
  }

  public String login(String email, String password) {
    User user =
        userRepository
            .findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

    if (!passwordEncoder.matches(password, user.getPassword()))
      throw new RuntimeException("Senha incorreta");

    return jwtService.generateToken(user.getEmail(), user.getRole().name());
  }
}
