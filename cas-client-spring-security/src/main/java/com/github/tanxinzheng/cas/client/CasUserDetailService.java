package com.github.tanxinzheng.cas.client;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by Jeng on 2015/10/15.
 */
public class CasUserDetailService implements AuthenticationUserDetailsService {

    @Override
    public UserDetails loadUserDetails(Authentication authentication) throws UsernameNotFoundException {
        String username = authentication.getName();
        Collection<SimpleGrantedAuthority> collection = new ArrayList<>();
        collection.add(new SimpleGrantedAuthority("ROLE_USER"));//给CAS用户默认角色为ROLE_USER
        return new User(username, "", collection);
    }
}
