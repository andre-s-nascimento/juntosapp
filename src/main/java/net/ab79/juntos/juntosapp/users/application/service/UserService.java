package net.ab79.juntos.juntosapp.users.application.service;

import java.util.List;
import java.util.UUID;

import net.ab79.juntos.juntosapp.users.domain.model.Role;
import net.ab79.juntos.juntosapp.users.domain.model.User;
import net.ab79.juntos.juntosapp.users.domain.repository.UserRepository;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
  private final UserRepository userRepository;
  private final BCryptPasswordEncoder passwordEncoder;

  public UserService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
  }

  public User registerUser(String name, String email, String password) {
    String encodedPassword = passwordEncoder.encode(password);
    User user = new User(UUID.randomUUID(), name, email, encodedPassword, Role.USER);
    return userRepository.save(user);
  }

  public User registerAdmin(String name, String email, String password) {
    String encodedPassword = passwordEncoder.encode(password);
    User user = new User(UUID.randomUUID(), name, email, encodedPassword, Role.ADMIN);
    return userRepository.save(user);
  }

  public List<User> listAll() {
    return userRepository.findAll();
  }

  public User getById(UUID id) {
    return userRepository
        .findById(id)
        .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
  }

  public User getByEmail(String email) {
    return userRepository
        .findByEmail(email)
        .orElseThrow(() -> new RuntimeException("Usuario com email: " + email + " não encontrado"));
  }
}
