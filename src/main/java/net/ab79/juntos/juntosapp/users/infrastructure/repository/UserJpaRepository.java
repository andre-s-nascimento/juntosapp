package net.ab79.juntos.juntosapp.users.infrastructure.repository;

import java.util.Optional;
import java.util.UUID;
import net.ab79.juntos.juntosapp.users.infrastructure.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserJpaRepository extends JpaRepository<UserEntity, UUID> {
  Optional<UserEntity> findByEmail(String email);
}
