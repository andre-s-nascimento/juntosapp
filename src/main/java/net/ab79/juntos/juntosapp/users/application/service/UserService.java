package net.ab79.juntos.juntosapp.users.application.service;

import java.util.List;
import java.util.UUID;
import net.ab79.juntos.juntosapp.users.domain.exception.EmailAlreadyExistsException;
import net.ab79.juntos.juntosapp.users.domain.exception.UserNotFoundException;
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
    if (userRepository.findByEmail(email).isPresent()) {
      throw new EmailAlreadyExistsException("Email " + email + " já cadastrado");
    }

    String encodedPassword = passwordEncoder.encode(password);
    User user = new User(null, name, email, encodedPassword, Role.USER);
    return userRepository.save(user);
  }

  public User registerAdmin(String name, String email, String password) {
    if (userRepository.findByEmail(email).isPresent()) {
      throw new EmailAlreadyExistsException("Email " + email + " já cadastrado");
    }

    String encodedPassword = passwordEncoder.encode(password);
    User user = new User(null, name, email, encodedPassword, Role.ADMIN);
    return userRepository.save(user);
  }

  public List<User> listAll() {
    return userRepository.findAll();
  }

  public User getById(UUID id) {
    return userRepository
        .findById(id)
        .orElseThrow(() -> new UserNotFoundException("Usuário não encontrado"));
  }

  public User getByEmail(String email) {
    return userRepository
        .findByEmail(email)
        .orElseThrow(() -> new UserNotFoundException("Usuário com email informado não encontrado"));
  }

  public User updateUser(UUID id, String name, String email, String password, Role role) {
    User existingUser =
        userRepository
            .findById(id)
            .orElseThrow(() -> new UserNotFoundException("Usuário não encontrado com ID: " + id));

    // Verifica se o novo email pertence a outro usuário
    if (!existingUser.getEmail().equalsIgnoreCase(email)) {
      userRepository
          .findByEmail(email)
          .ifPresent(
              user -> {
                if (!user.getId().equals(id)) {
                  throw new EmailAlreadyExistsException(email);
                }
              });
    }

    // Atualiza dados
    String updatedPassword = existingUser.getPassword();
    if (password != null && !password.isBlank()) {
      updatedPassword = passwordEncoder.encode(password);
    }

    Role updatedRole = (role != null) ? role : existingUser.getRole();

    User updatedUser =
        new User(
            existingUser.getId(),
            name != null ? name : existingUser.getName(),
            email != null ? email : existingUser.getEmail(),
            updatedPassword,
            updatedRole);

    // O JPA faz o update automaticamente com o save()
    return userRepository.save(updatedUser);
  }

  public void deleteUser(UUID id) {
    User user =
        userRepository
            .findById(id)
            .orElseThrow(() -> new UserNotFoundException("Usuário não encontrado com ID: " + id));

    userRepository.delete(user.getId());
  }
}
