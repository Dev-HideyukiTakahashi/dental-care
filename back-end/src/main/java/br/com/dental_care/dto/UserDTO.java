package br.com.dental_care.dto;

import java.util.ArrayList;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserDTO {

  @Schema(description = "ID of the user", example = "2", accessMode = Schema.AccessMode.READ_ONLY)
  private Long id;

  @Schema(description = "Name of the user", example = "John Doe")
  @NotBlank(message = "Nome é um campo obrigatório.")
  private String name;

  @Schema(description = "Email of the user", example = "john.doe@example.com")
  @Email(message = "Por favor, insira um e-mail válido.")
  private String email;

  @Schema(description = "Password of the user", example = "#Password123")
  @Size(min = 6, max = 20, message = "A senha deve ter entre 6 e 20 caracteres.")
  @Pattern(regexp = ".*[A-Z].*", message = "A senha deve conter pelo menos uma letra maiúscula.")
  @Pattern(regexp = ".*[a-z].*", message = "A senha deve conter pelo menos uma letra minúscula.")
  @Pattern(regexp = ".*[0-9].*", message = "A senha deve conter pelo menos um número.")
  @Pattern(regexp = ".*[!@#$%^&*(),.?\":{}|<>].*", message = "A senha deve conter pelo menos um caractere especial.")
  private String password;

  @Schema(description = "Phone number of the user", example = "(11) 99710-2376")
  @Pattern(regexp = "^\\(?\\d{2}\\)?\\s?9?\\d{4}-?\\d{4}$", message = "Telefone inválido. Formato esperado: (XX) 9XXXX-XXXX.")
  private String phone;

  @Schema(description = "Roles assigned to the user")
  private final List<RoleDTO> roles = new ArrayList<>();

  public void addRole(RoleDTO role){
    roles.add(role);
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((id == null) ? 0 : id.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    UserDTO other = (UserDTO) obj;
    if (id == null) {
      if (other.id != null)
        return false;
    } else if (!id.equals(other.id))
      return false;
    return true;
  };
}
