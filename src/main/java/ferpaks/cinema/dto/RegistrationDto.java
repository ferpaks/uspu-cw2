package ferpaks.cinema.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegistrationDto {
	@NotBlank(message = "Имя пользователя не может быть пустым")
	private String username;

	@NotBlank(message = "Пароль не может быть пустым")
	@Size(min = 8, message = "Пароль должен быть не короче 8 символов")
	private String password;

	@NotBlank(message = "Подтверждение пароля не может быть пустым")
	private String confirmPassword;
}
