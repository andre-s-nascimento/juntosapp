package net.ab79.juntos.juntosapp.users.infrastructure.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import net.ab79.juntos.juntosapp.users.domain.exception.UserNotFoundException;
import net.ab79.juntos.juntosapp.users.domain.model.User;
import net.ab79.juntos.juntosapp.users.domain.repository.UserRepository;
import net.ab79.juntos.juntosapp.users.infrastructure.entity.UserEntity;

@Repository
@Transactional
public class UserRepositoryJpaAdapter implements UserRepository {

    private final UserJpaRepository jpaRepository;

    public UserRepositoryJpaAdapter(UserJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public User save(User user) {
        // 🔹 Evita o erro de "merge" quando o ID já vem preenchido
        UserEntity entity = toEntity(user);

        // Se o ID estiver setado mas não existir no banco, zera o ID para forçar INSERT
        if (entity.getId() != null && !jpaRepository.existsById(entity.getId())) {
            entity.setId(null);
        }

        UserEntity saved = jpaRepository.saveAndFlush(entity);
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
            throw new UserNotFoundException("Usuário não encontrado para exclusão: " + id);
        }
        jpaRepository.deleteById(id);
    }

    @Override
    public User update(User user) {
        UserEntity entity = jpaRepository
                .findById(user.getId())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado: " + user.getId()));

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

        UserEntity updated = jpaRepository.saveAndFlush(entity);
        return toModel(updated);
    }

    private User toModel(UserEntity entity) {
        return new User(entity.getId(), entity.getName(), entity.getEmail(), entity.getPassword(), entity.getRole());
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
