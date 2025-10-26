package net.ab79.juntos.juntosapp.users.interfaces.controller;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.ab79.juntos.juntosapp.users.application.service.UserService;
import net.ab79.juntos.juntosapp.users.domain.model.Role;
import net.ab79.juntos.juntosapp.users.domain.model.User;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {
  private final UserService userService;

  public UserController(UserService userService) {
    this.userService = userService;
  }

  @PostMapping
  public User createUser(@RequestBody Map<String, String> body) {
    String name = body.get("name");
    String email = body.get("email");
    String password = body.get("password");
    
    // Validação da senha
    if (password == null || password.isBlank()) {
        throw new IllegalArgumentException("Senha é obrigatória");
    }
    
    return userService.registerUser(name, email, password);
  }

  @PostMapping("/admin")
  @PreAuthorize("hasRole('ADMIN')")
  public User createAdmin(@RequestBody Map<String, String> body) { // ← MUDAR para Map
    String name = body.get("name");
    String email = body.get("email");
    String password = body.get("password");
    
    // Validação da senha
    if (password == null || password.isBlank()) {
        throw new IllegalArgumentException("Senha é obrigatória para administrador");
    }
    
    return userService.registerAdmin(name, email, password);
  }

  @GetMapping
  @PreAuthorize("hasRole('ADMIN')")
  public List<User> listUsers() {
    return userService.listAll();
  }

  @GetMapping("/id/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public User getUser(@PathVariable UUID id) {
    return userService.getById(id);
  }

@GetMapping("/email/{email}")
  @PreAuthorize("hasRole('ADMIN')")
public User getUserByEmail(@PathVariable String email) {
    return userService.getByEmail(email);
}

  @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
  public User updateUser(@PathVariable UUID id, @RequestBody Map<String, String> body) { // ← Usar Map também
    String name = body.get("name");
    String email = body.get("email");
    String password = body.get("password"); // pode ser null
    Role role = Role.valueOf(body.get("role"));
    
    return userService.updateUser(id, name, email, password, role);
  }

}
