package net.ab79.juntos.juntosapp.users.interfaces.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@Tag(
    name = "Usuários",
    description = "Gerenciamento de usuários (CRUD) com controle de acesso por papéis.")
public class UserController {

  private static final String PASSWORD = "password";
  private static final String EMAIL = "email";
  private static final String NAME = "name";
  private final UserService userService;

  public UserController(UserService userService) {
    this.userService = userService;
  }

  // ---------------- CREATE USER ----------------
  @Operation(
      summary = "Cria um novo usuário comum",
      description = "Cria um usuário com papel padrão USER.")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "201",
            description = "Usuário criado com sucesso",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = User.class))),
        @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content),
        @ApiResponse(
            responseCode = "409",
            description = "E-mail já está em uso",
            content =
                @Content(
                    mediaType = "application/json",
                    schema =
                        @Schema(
                            example = "{\"error\": \"Email já está em uso: exemplo@teste.com\"}")))
      })
  @PostMapping
  public ResponseEntity<User> createUser(@RequestBody Map<String, String> body) {
    String name = body.get(NAME);
    String email = body.get(EMAIL);
    String password = body.get(PASSWORD);

    if (password == null || password.isBlank()) {
      return ResponseEntity.badRequest().build();
    }

    User created = userService.registerUser(name, email, password);
    return ResponseEntity.status(HttpStatus.CREATED).body(created);
  }

  // ---------------- CREATE ADMIN ----------------
  @Operation(
      summary = "Cria um novo usuário administrador",
      description = "Cria um usuário com o papel ADMIN. Requer permissão de ADMIN.")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "201",
            description = "Administrador criado com sucesso",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = User.class))),
        @ApiResponse(
            responseCode = "400",
            description = "Dados inválidos",
            content =
                @Content(
                    mediaType = "application/json",
                    schema =
                        @Schema(
                            example = "{\"error\": \"Senha é obrigatória para administrador\"}"))),
        @ApiResponse(
            responseCode = "403",
            description = "Acesso negado",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(example = "{\"error\": \"Permissão negada\"}"))),
        @ApiResponse(
            responseCode = "409",
            description = "E-mail já está em uso",
            content =
                @Content(
                    mediaType = "application/json",
                    schema =
                        @Schema(
                            example = "{\"error\": \"Email já está em uso: exemplo@teste.com\"}")))
      })
  @PostMapping("/admin")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<User> createAdmin(@RequestBody Map<String, String> body) {
    String name = body.get(NAME);
    String email = body.get(EMAIL);
    String password = body.get(PASSWORD);

    if (password == null || password.isBlank()) {
      return ResponseEntity.badRequest().build();
    }

    User created = userService.registerAdmin(name, email, password);
    return ResponseEntity.status(HttpStatus.CREATED).body(created);
  }

  // ---------------- LIST USERS ----------------
  @Operation(
      summary = "Lista todos os usuários",
      description = "Disponível apenas para administradores.")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Usuários listados com sucesso",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = User.class))),
        @ApiResponse(
            responseCode = "403",
            description = "Acesso negado",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(example = "{\"error\": \"Acesso não autorizado\"}")))
      })
  @GetMapping
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<List<User>> listUsers() {
    return ResponseEntity.ok(userService.listAll());
  }

  // ---------------- GET USER BY ID ----------------
  @Operation(
      summary = "Busca usuário por ID",
      description = "Somente ADMIN pode consultar usuários.")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Usuário encontrado",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = User.class))),
        @ApiResponse(
            responseCode = "404",
            description = "Usuário não encontrado",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(example = "{\"error\": \"Usuário não encontrado\"}")))
      })
  @GetMapping("/id/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<User> getUser(
      @Parameter(description = "ID do usuário a ser buscado", required = true) @PathVariable("id")
          UUID id) {

    User user = userService.getById(id);
    return ResponseEntity.ok(user);
  }

  // ---------------- GET USER BY EMAIL ----------------
  @Operation(
      summary = "Busca usuário por e-mail",
      description = "Somente ADMIN pode consultar por e-mail.")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Usuário encontrado",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = User.class))),
        @ApiResponse(
            responseCode = "404",
            description = "Usuário não encontrado",
            content =
                @Content(
                    mediaType = "application/json",
                    schema =
                        @Schema(
                            example =
                                "{\"error\": \"Usuário com e-mail informado não encontrado\"}")))
      })
  @GetMapping("/email/{email}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<User> getUserByEmail(
      @Parameter(description = "E-mail do usuário a ser buscado", required = true)
          @PathVariable(EMAIL)
          String email) {

    User user = userService.getByEmail(email);
    return ResponseEntity.ok(user);
  }

  // ---------------- UPDATE USER ----------------
  @Operation(
      summary = "Atualiza dados de um usuário",
      description =
          "Apenas ADMIN pode atualizar usuários. Retorna 409 se o e-mail já estiver em uso.")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Usuário atualizado com sucesso",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = User.class))),
        @ApiResponse(
            responseCode = "400",
            description = "Dados inválidos",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(example = "{\"error\": \"Dados inválidos\"}"))),
        @ApiResponse(
            responseCode = "404",
            description = "Usuário não encontrado",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(example = "{\"error\": \"Usuário não encontrado\"}"))),
        @ApiResponse(
            responseCode = "409",
            description = "E-mail já está em uso",
            content =
                @Content(
                    mediaType = "application/json",
                    schema =
                        @Schema(
                            example = "{\"error\": \"Email já está em uso: exemplo@teste.com\"}")))
      })
  @PutMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<User> updateUser(
      @PathVariable UUID id, @RequestBody Map<String, String> body) {
    String name = body.get(NAME);
    String email = body.get(EMAIL);
    String password = body.get(PASSWORD);
    String roleStr = body.get("role");

    Role role = null;
    if (roleStr != null && !roleStr.isBlank()) {
      try {
        role = Role.valueOf(roleStr.toUpperCase());
      } catch (IllegalArgumentException _) {
        throw new IllegalArgumentException("Papel inválido: " + roleStr);
      }
    }

    User updated = userService.updateUser(id, name, email, password, role);
    return ResponseEntity.ok(updated);
  }

  // ---------------- DELETE USER ----------------
  @Operation(summary = "Remove usuário por ID", description = "Apenas ADMIN pode excluir usuários.")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "204",
            description = "Usuário removido com sucesso",
            content = @Content),
        @ApiResponse(
            responseCode = "404",
            description = "Usuário não encontrado",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(example = "{\"error\": \"Usuário não encontrado\"}")))
      })
  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
    userService.deleteUser(id);
    return ResponseEntity.noContent().build();
  }
}
