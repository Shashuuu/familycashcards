package com.example.cashcard.config;
//
//import com.example.cashcard.model.CashCardUser;
//import com.example.cashcard.repo.CashCardUserRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.authentication.AuthenticationProvider;
//import org.springframework.security.authentication.AuthenticationServiceException;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.AuthenticationException;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//import org.springframework.stereotype.Component;
//
//import java.util.Optional;

//  ---------------------------------------------------------------------------------------
//  Custom Authentication Provider Implementation
//  ---------------------------------------------------------------------------------------

//
//@Component
public class NoEncodingAuthenticationProvider
//        implements AuthenticationProvider
{

//    @Autowired
//    CashCardUserRepository cashCardUserRepository;
//
//    @Override
//    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
//        String username = authentication.getName();
//
//        Optional<CashCardUser> cashCardUser = cashCardUserRepository.findByUsername(username);
//        cashCardUser.orElseThrow(() -> new UsernameNotFoundException("Not Found: " + username));
//        MyUserDetails myUserDetails = cashCardUser.map(MyUserDetails::new).get();
//
//        String password = authentication.getCredentials().toString();
//
//        if(password.equals(myUserDetails.getPassword())) {
//            return new UsernamePasswordAuthenticationToken(
//                    username, password, myUserDetails.getAuthorities()
//            );
//        }
////        else {
////            throw new AuthenticationServiceException("Custom Authentication Failed");
////        }
//
//        return null;
//    }
//
//    @Override
//    public boolean supports(Class<?> authentication) {
//        return authentication.equals(UsernamePasswordAuthenticationToken.class);
//    }
}
