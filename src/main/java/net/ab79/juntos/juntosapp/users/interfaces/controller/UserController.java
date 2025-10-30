package net.ab79.juntos.juntosapp.users.interfaces.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.ab79.juntos.juntosapp.users.application.service.UserService;
import net.ab79.juntos.juntosapp.users.domain.model.Role;
import net.ab79.juntos.juntosapp.users.domain.model.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@Tag(
    name = "Usuários",
    description = "Gerenciamento de usuários (CRUD) com controle de acesso por papéis.")
public class UserController {
  private final UserService userService;

  public UserController(UserService userService) {
    this.userService = userService;
  }

  @Operation(
      summary = "Cria um novo usuário comum",
      responses = {
        @ApiResponse(responseCode = "201", description = "Usuário criado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos")
      })
  @PostMapping
  public ResponseEntity<User> createUser(@RequestBody Map<String, String> body) {
    String name = body.get("name");
    String email = body.get("email");
    String password = body.get("password");

    if (password == null || password.isBlank()) {
      return ResponseEntity.badRequest().build();
    }

    User created = userService.registerUser(name, email, password);
    return ResponseEntity.status(HttpStatus.CREATED).body(created);
  }

  @Operation(
      summary = "Cria um novo usuário comum",
      responses = {
        @ApiResponse(responseCode = "201", description = "Usuário criado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos")
      })
  @PostMapping("/admin")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<User> createAdmin(@RequestBody Map<String, String> body) {
    String name = body.get("name");
    String email = body.get("email");
    String password = body.get("password");

    if (password == null || password.isBlank()) {
      return ResponseEntity.badRequest().build();
    }

    User created = userService.registerAdmin(name, email, password);
    return ResponseEntity.status(HttpStatus.CREATED).body(created);
  }

  @Operation(summary = "Lista todos os usuários (somente ADMIN)")
  @GetMapping
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<List<User>> listUsers() {
    List<User> users = userService.listAll();
    return ResponseEntity.ok(users);
  }

  @Operation(summary = "Busca usuário por ID (somente ADMIN)")
  @GetMapping("/id/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<User> getUser(@PathVariable UUID id) {
    User user = userService.getById(id);
    return ResponseEntity.ok(user);
  }

  @Operation(summary = "Busca usuário por email (somente ADMIN)")
  @GetMapping("/email/{email}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<User> getUserByEmail(@PathVariable String email) {
    User user = userService.getByEmail(email);
    return ResponseEntity.ok(user);
  }

  @Operation(summary = "Atualiza dados de um usuário (somente ADMIN)")
  @PutMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<User> updateUser(
      @PathVariable UUID id, @RequestBody Map<String, String> body) {
    String name = body.get("name");
    String email = body.get("email");
    String password = body.get("password");
    String roleStr = body.get("role");

    Role role = null;
    if (roleStr != null && !roleStr.isBlank()) {
      try {
        role = Role.valueOf(roleStr.toUpperCase());
      } catch (IllegalArgumentException e) {
        return ResponseEntity.badRequest().build();
      }
    }

    User updated = userService.updateUser(id, name, email, password, role);
    return ResponseEntity.ok(updated);
  }

  @Operation(summary = "Remove usuário por ID (somente ADMIN)")
  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<?> deleteUser(@PathVariable UUID id) {
    userService.deleteUser(id);

    return ResponseEntity.noContent().build();
  }
}
