package api.controla_preju.entities;

import api.controla_preju.entities.enums.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "USERS")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class User implements UserDetails {

    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(nullable = false, unique = true, length = 150)
    private String email;
    @Column(nullable = false, length = 30)
    private String name;
    @Column(nullable = false, length = 100)
    private String password;
    @Column(nullable = false, length = 1)
    private Character status;
    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
    @Column(nullable = false)
    private LocalDateTime createdAt;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role;

    public User(String email, String name, String password) {
        this.email = email;
        this.name = name;
        this.password = password;
        this.status = 'A';
        this.createdAt = LocalDateTime.now();
        this.role = Role.USER;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (this.role == Role.ADMIN) {
            return List.of(new SimpleGrantedAuthority("ROLE_ADMIN"), new SimpleGrantedAuthority("ROLE_USER"));
        } else {
            return List.of(new SimpleGrantedAuthority("ROLE_USER"));
        }
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return this.status == 'A';
    }

    public void setStatus(Character status) {
        this.status = status;
    }

}
