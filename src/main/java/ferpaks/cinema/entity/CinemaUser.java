package ferpaks.cinema.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Collections;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CinemaUser implements UserDetails {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Setter(AccessLevel.NONE)
	private Long id;

	@Column(unique = true, nullable = false)
	@NotBlank(message = "Имя пользователя не может быть пустым.")
	@Size(min = 3, max = 32, message = "Имя пользователя должно быть от 3 до 32 символов.")
	@Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "Имя пользователя должно содержать только латинские буквы, цифры или знак подчёркивания.")
	private String username;

	@Column(nullable = false)
	@NotBlank(message = "Пароль не может быть пустым")
	@Size(min = 8, message = "Пароль должен быть не короче 8 символов")
	@Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=]).{8,}$", message = "Пароль должен содержать как минимум одну цифру, минимум одну малую и одну великую буквы, а также минимум один специальный символ из списка (@#$%^&+=)")
	private String password;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private CinemaUserRole role;

	@Column(nullable = false)
	@DecimalMin(value = "0.0", message = "Баланс не может быть отрицательным")
	private BigDecimal balanceInRubles = BigDecimal.ZERO;

	@Override
	public boolean isAccountNonExpired() {
		return UserDetails.super.isAccountNonExpired();
	}

	@Override
	public boolean isAccountNonLocked() {
		return UserDetails.super.isAccountNonLocked();
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return UserDetails.super.isCredentialsNonExpired();
	}

	@Override
	public boolean isEnabled() {
		return UserDetails.super.isEnabled();
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return Collections.singleton(new SimpleGrantedAuthority("ROLE_" + this.role.getName()));
	}
}