package net.ab79.juntos.juntosapp.users.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import java.util.UUID;

public class User {
  private UUID id;
  private String name;
  private String email;
  @JsonIgnore private String password;

  @Enumerated(EnumType.STRING)
  private Role role;

  public User(UUID id, String name, String email, String password, Role role) {
    this.id = id;
    this.name = name;
    this.email = email;
    this.password = password;
    this.role = role;
  }

  public User() {
    // necessário para Jackson e JPA converterem corretamente
  }

  public UUID getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getEmail() {
    return email;
  }

  public String getPassword() {
    return password;
  }

  public Role getRole() {
    return role;
  }

  public void setRole(Role role) {
    this.role = role;
  }

  public void updateName(String name) {
    if (name == null || name.isBlank()) throw new IllegalArgumentException("Nome Inválido");

    this.name = name;
  }
}
