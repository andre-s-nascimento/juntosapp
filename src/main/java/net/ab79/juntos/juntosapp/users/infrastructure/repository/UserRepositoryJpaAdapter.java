package net.ab79.juntos.juntosapp.users.infrastructure.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import net.ab79.juntos.juntosapp.users.domain.model.User;
import net.ab79.juntos.juntosapp.users.domain.repository.UserRepository;
import net.ab79.juntos.juntosapp.users.infrastructure.entity.UserEntity;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepositoryJpaAdapter implements UserRepository {

  private final UserJpaRepository jpaRepository;

  public UserRepositoryJpaAdapter(UserJpaRepository jpaRepository) {
    this.jpaRepository = jpaRepository;
  }

  @Override
  public User save(User user) {
    UserEntity entity = toEntity(user);
    UserEntity saved = jpaRepository.save(entity);
    return toModel(saved);
  }

  @Override
  public Optional<User> findById(UUID id) {
    return jpaRepository.findById(id).map(this::toModel);
  }

  @Override
  public Optional<User> findByEmail(String email) {
    return jpaRepository.findByEmail(email).map(this::toModel);
  }

  @Override
  public List<User> findAll() {
    return jpaRepository.findAll().stream().map(this::toModel).toList();
  }

  @Override
  public void delete(UUID id) {
    if (!jpaRepository.existsById(id)) {
      throw new RuntimeException("Usuário não encontrado para exclusão: " + id);
    }
    jpaRepository.deleteById(id);
  }

  @Override
  public User update(User user) {
    UserEntity entity =
        jpaRepository
            .findById(user.getId())
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado: " + user.getId()));

    // Atualiza apenas campos válidos
    if (user.getName() != null && !user.getName().isBlank()) {
      entity.setName(user.getName());
    }

    if (user.getEmail() != null && !user.getEmail().isBlank()) {
      entity.setEmail(user.getEmail());
    }

    if (user.getPassword() != null && !user.getPassword().isBlank()) {
      entity.setPassword(user.getPassword());
    }

    if (user.getRole() != null) {
      entity.setRole(user.getRole());
    }

    UserEntity updated = jpaRepository.save(entity);
    return toModel(updated);
  }

  private User toModel(UserEntity entity) {
    return new User(
        entity.getId(),
        entity.getName(),
        entity.getEmail(),
        entity.getPassword(),
        entity.getRole());
  }

  private UserEntity toEntity(User user) {
    UserEntity entity = new UserEntity();
    entity.setId(user.getId());
    entity.setName(user.getName());
    entity.setEmail(user.getEmail());
    entity.setPassword(user.getPassword());
    entity.setRole(user.getRole());
    return entity;
  }
}
