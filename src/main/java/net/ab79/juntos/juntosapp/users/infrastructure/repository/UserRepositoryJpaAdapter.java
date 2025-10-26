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
    UserEntity entity = new UserEntity();
    entity.setName(user.getName());
    entity.setEmail(user.getEmail());
    entity.setPassword(user.getPassword());
    entity.setRole(user.getRole());
    UserEntity saved = jpaRepository.save(entity);
    return new User(saved.getId(), saved.getName(), saved.getEmail(), saved.getPassword(), saved.getRole());
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
    return jpaRepository.findAll()
        .stream()
        .map(this::toModel)
        .toList();
  }

  @Override
  public void delete(UUID id) {
    jpaRepository.deleteById(id);
  }

  @Override
  public User update(User user){
    UserEntity entity = jpaRepository.findById(user.getId())
    .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

    //Atualiza apenas os campos permitidos
    entity.setName(user.getName());
    entity.setEmail(user.getEmail());

    // Atualiza senha apenas se for fornecida
    if(user.getPassword() != null && !user.getPassword().isBlank()){
      entity.setPassword(user.getPassword());
    }

    entity.setRole(user.getRole());

    UserEntity updated = jpaRepository.save(entity);
    return new User(updated.getId(), updated.getName(), updated.getEmail(), updated.getPassword(), updated.getRole());
  }

  private User toModel(UserEntity entity) {
    return new User(entity.getId(), entity.getName(), entity.getEmail(), entity.getPassword(), entity.getRole());
  }
}
