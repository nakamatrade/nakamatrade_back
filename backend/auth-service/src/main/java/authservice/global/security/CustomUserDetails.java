package authservice.global.security;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import authservice.user.domain.User;

import java.util.Collection;
import java.util.List;

@Getter
@RequiredArgsConstructor
public class CustomUserDetails implements UserDetails {
	
	private static final long serialVersionUID = 7057111263654209465L;
	
	private final User user;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(user.getRole().getType()));
    }

    @Override
    public String getPassword() {
    	return user.getPassword();
    }

    @Override
    public String getUsername() {
    	return user.getUsername();
    }

    @Override
    public boolean isAccountNonLocked() {
        return !user.isLocked();
    }

    @Override public boolean isAccountNonExpired() {
    	return true;
    }
    
    @Override public boolean isCredentialsNonExpired() {
    	return true;
    }
    @Override public boolean isEnabled() {
    	return true;
    }
}
