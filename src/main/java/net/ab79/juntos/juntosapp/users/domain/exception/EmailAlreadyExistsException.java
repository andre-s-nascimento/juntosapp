package net.ab79.juntos.juntosapp.users.domain.exception;

public class EmailAlreadyExistsException extends RuntimeException {
  public EmailAlreadyExistsException(String email) {
    super("Email: " + email + " já está em uso.");
  }
}
