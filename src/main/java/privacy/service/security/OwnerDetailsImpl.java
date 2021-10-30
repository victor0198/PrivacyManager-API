package privacy.service.security;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import privacy.models.Owner;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/** This class contains necessary information (such as: username, password, authorities)
 * to build an Authentication object.
 * If the authentication process is successful, we can get User’s information
 * such as username, password, authorities from an Authentication object. We
 * create an implementation of the UserDetails interface to get more information. **/
@AllArgsConstructor
@Getter
@Setter
public class OwnerDetailsImpl implements UserDetails {
    private static final long serialVersionUID = 1L; //no idea what it does
    private Long id;
    private String username;
    private String email;
    @JsonIgnore
    private String password;
    private Boolean enabled = true;
    private Boolean notLocked = true;
    private Collection<? extends GrantedAuthority> authorities;

    public OwnerDetailsImpl(Long ownerId, String username, String email, String password, List<GrantedAuthority> authorities) {
        this.id = ownerId;
        this.username = username;
        this.email = email;
        this.password = password;
        this.authorities = authorities;
    }

    public static OwnerDetailsImpl build(Owner owner) {
        List<GrantedAuthority> authorities = owner.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName().name()))
                .collect(Collectors.toList());

        return new OwnerDetailsImpl(
                owner.getOwnerId(),
                owner.getUsername(),
                owner.getEmail(),
                owner.getPassword(),
                authorities);
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
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
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        OwnerDetailsImpl owner = (OwnerDetailsImpl) o;
        return Objects.equals(id, owner.id);
    }
}
