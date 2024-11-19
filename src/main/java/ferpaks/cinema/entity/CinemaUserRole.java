package ferpaks.cinema.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

@Getter
@AllArgsConstructor
public enum CinemaUserRole {
	USER("Пользователь"),
	STAFF("Сотрудник"),
	MANAGER("Менеджер"),
	ADMIN("Администратор");

	private final String name;

	public GrantedAuthority getGrantedAuthority() {
		switch (this) {
			case ADMIN:
				return new SimpleGrantedAuthority("ROLE_"+ADMIN.name);
			case MANAGER:
				return new SimpleGrantedAuthority("ROLE_"+MANAGER.name);
			case STAFF:
				return new SimpleGrantedAuthority("ROLE_"+STAFF.name);
			case USER:
			default:
				return new SimpleGrantedAuthority("ROLE_"+USER.name);
		}
	}
}