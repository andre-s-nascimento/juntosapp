package net.ab79.juntos.juntosapp.users.interfaces.controller;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.ab79.juntos.juntosapp.users.application.service.UserService;
import net.ab79.juntos.juntosapp.users.domain.model.User;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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
    return userService.registerUser(body.get("name"), body.get("email"), body.get("password"));
  }

  @PostMapping("/admin")
  @PreAuthorize("hasRole('ADMIN')")
  public User createAdmin(@RequestBody User user) {
    return userService.registerAdmin(user.getName(), user.getEmail(), user.getPassword());
  }

  @GetMapping
  public List<User> listUsers() {
    return userService.listAll();
  }

  @GetMapping("/{id}")
  public User getUser(@PathVariable UUID id) {
    return userService.getById(id);
  }

  @GetMapping("/{email}")
  public User getUserByEmail(@PathVariable String email) {
    return userService.getByEmail(email);
  }
}
