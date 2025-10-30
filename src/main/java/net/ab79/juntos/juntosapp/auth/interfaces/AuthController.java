package net.ab79.juntos.juntosapp.auth.interfaces;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import net.ab79.juntos.juntosapp.auth.application.AuthService;

import java.util.Map;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/api/auth")
@Tag(name="Autenticação", description="Endpoints de login e geração de token JWT")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService){
        this.authService = authService;
    }

        @Operation(
        summary = "Autentica o usuário",
        description = "Recebe email e senha e retorna um token JWT válido por 1 hora.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Autenticação bem-sucedida",
                content = @Content(mediaType = "application/json", schema = @Schema(example = "{ \"token\": \"eyJhbGciOiJIUzI1NiIs...\" }"))),
            @ApiResponse(responseCode = "400", description = "Credenciais inválidas", content = @Content)
        }
    )
    @PostMapping("/login")
    public Map<String,String> login(@RequestBody Map<String, String> body) {
        String token = authService.login(body.get("email"), body.get("password"));
        
        return Map.of("token", token);
    }
    

}
