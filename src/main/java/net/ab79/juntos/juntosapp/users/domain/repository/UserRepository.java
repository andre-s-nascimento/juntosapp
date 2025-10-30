package net.ab79.juntos.juntosapp.users.domain.repository;

import net.ab79.juntos.juntosapp.users.domain.model.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository {
  User save(User user);

  Optional<User> findById(UUID id);

  Optional<User> findByEmail(String email);

  List<User> findAll();

  void delete(UUID id);

  User update(User user);
}
