package com.example.house.security;

import com.example.house.domain.User;
import com.example.house.service.users.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;


public class AuthProvider implements AuthenticationProvider {
    @Autowired
    private IUserService iUserService;
    @Autowired
    private PasswordEncoder passwordEncoder;



    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String userName = authentication.getName(); //网页上的用户名
        String inputPassword = (String) authentication.getCredentials();//密码

        User user = iUserService.findUserByName(userName);
        if(user==null)
            throw new AuthenticationCredentialsNotFoundException("authError");
        if(passwordEncoder.matches(inputPassword,user.getPassword()))
            return new UsernamePasswordAuthenticationToken(user,null,user.getAuthorities());
        throw new BadCredentialsException("authError");
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return true;
    }
}
