package org.example.expert.domain.user.entity;

import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.common.entity.Timestamped;
import org.example.expert.domain.user.enums.UserRole;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
@Table(name = "users")
public class User extends Timestamped {

	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(unique = true)
	private String email;
	private String password;
	@Enumerated(EnumType.STRING)
	private UserRole userRole;

	public User(String email, String password, UserRole userRole) {
		this.email = email;
		this.password = password;
		this.userRole = userRole;
	}

	private User(Long id, String email, UserRole userRole) {
		this.id = id;
		this.email = email;
		this.userRole = userRole;
	}

	public static User fromAuthUser(AuthUser authUser) {
		return new User(authUser.getId(), authUser.getEmail(), authUser.getUserRole());
	}

	public void changePassword(String password) {
		this.password = password;
	}

	public void updateRole(UserRole userRole) {
		this.userRole = userRole;
	}
}
