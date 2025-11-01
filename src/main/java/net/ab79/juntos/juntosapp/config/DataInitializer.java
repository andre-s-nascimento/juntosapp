package net.ab79.juntos.juntosapp.config;

import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import net.ab79.juntos.juntosapp.users.domain.model.Role;
import net.ab79.juntos.juntosapp.users.domain.model.User;
import net.ab79.juntos.juntosapp.users.domain.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class DataInitializer implements CommandLineRunner {

  private final UserRepository userRepository;
  private final BCryptPasswordEncoder passwordEncoder;

  public DataInitializer(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
  }

  @Override
  public void run(String... args) throws Exception {
    // ADMIN
    createUserIfNotExists("Admin", "admin@juntosapp.com.br", "1234", Role.ADMIN);

    // USERS
    createUserIfNotExists("Adam B", "adamb@juntosapp.com.br", "4321", Role.USER);
    createUserIfNotExists("Adam Bravo", "adambravo@juntosapp.com.br", "4321", Role.USER);
  }

  private void createUserIfNotExists(String name, String email, String rawPassword, Role role) {
    Optional<User> existingUser = userRepository.findByEmail(email);
    if (existingUser.isEmpty()) {
      String encodedPassword = passwordEncoder.encode(rawPassword);
      User user = new User(null, name, email, encodedPassword, role);
      userRepository.save(user);
      log.info("Usuário criado: " + email + " [" + role + "]");
    }
  }
}
