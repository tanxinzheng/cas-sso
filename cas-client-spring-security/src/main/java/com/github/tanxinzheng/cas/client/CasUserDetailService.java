package com.github.tanxinzheng.cas.client;

import org.jasig.cas.client.authentication.AttributePrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

/**
 * Created by Jeng on 2015/10/15.
 */
public class CasUserDetailService implements AuthenticationUserDetailsService {

    @Override
    public UserDetails loadUserDetails(Authentication authentication) throws UsernameNotFoundException {
        String username = authentication.getName();
        AttributePrincipal principal = (AttributePrincipal) authentication.getPrincipal();
        Map attributes = principal.getAttributes();
        String userid=(String)attributes.get("userid");
        String username1 = (String)attributes.get("username");
        String email = (String)attributes.get("email");
        authentication.getPrincipal();
        Collection<SimpleGrantedAuthority> collection = new ArrayList<>();
        collection.add(new SimpleGrantedAuthority("ROLE_USER"));//给CAS用户默认角色为ROLE_USER
        return new User(username, "", collection);
    }
}
