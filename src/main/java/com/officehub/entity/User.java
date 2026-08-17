package com.officehub.entity;

import java.time.LocalDateTime;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String fullName;

	@Column(nullable = false, unique = true)
	private String email;

	@Column(nullable = false)
	private String password;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Role role;

	@Column(nullable = false)
	private LocalDateTime createdAt;
	
	@Column(length = 20)
	private String phoneNumber;

	@Column(length = 100)
	private String designation;

	private String profileImage;
	
	@Column(nullable = false)
	private boolean emailNotifications = true;

	@Column(nullable = false)
	private boolean taskNotifications = true;

	@Column(nullable = false)
	private boolean chatNotifications = true;
	
	
	
	public Organization getOrganization() {
	    return organization;
	}

	public void setOrganization(Organization organization) {
	    this.organization = organization;
	}
	
	
	
	@ManyToOne
    @JoinColumn(name = "organization_id")
    private Organization organization;

}