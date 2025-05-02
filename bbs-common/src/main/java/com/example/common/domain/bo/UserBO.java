package com.example.common.domain.bo;

import cn.hutool.core.util.ObjectUtil;
import com.example.common.domain.dto.UserDTO;
import com.example.common.utils.ConstantUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserBO implements UserDetails {
    private static final DateTimeFormatter DEADLINE_FORMATTER = DateTimeFormatter.ofPattern(ConstantUtils.DATE_FORMAT);
    private UserDTO userDTO;

    @Override
    @JsonIgnore
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<GrantedAuthority> authorities = new HashSet<>();
        if (ObjectUtil.isNotNull(userDTO.getRole())) {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + userDTO.getRole().getName()));
        }
        if (ObjectUtil.isNotNull(userDTO.getAuthority())) {
            userDTO.getAuthority().getAuthorities().forEach(authority -> {
                authorities.add(new SimpleGrantedAuthority(authority));
            });
        }
        return authorities;
    }

    @Override
    @JsonIgnore
    public String getPassword() {
        return userDTO.getPassword();
    }

    @Override
    @JsonIgnore
    public String getUsername() {
        return userDTO.getUsername();
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isEnabled() {
        if (!userDTO.getBanned()) return true;

        LocalDateTime deadline = LocalDateTime.parse(userDTO.getDeadline(), DEADLINE_FORMATTER);

        if (LocalDateTime.now().isAfter(deadline)) {
            return true;
        }

        return false;
    }
}
