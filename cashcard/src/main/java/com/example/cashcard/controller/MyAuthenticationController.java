package com.example.cashcard.controller;

import com.example.cashcard.model.CashCardUser;
import com.example.cashcard.repo.CashCardUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;


@RestController
@RequestMapping("/index")
public class MyAuthenticationController {

    private CashCardUserRepository cashCardUserRepository;
    private AuthenticationManager authenticationManager;

    @Autowired
    public MyAuthenticationController(CashCardUserRepository cashCardUserRepository, AuthenticationManager authenticationManager) {
        this.cashCardUserRepository = cashCardUserRepository;
        this.authenticationManager = authenticationManager;
    }

    BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

    @GetMapping
    private String welcomeMessage() {
        return "Welcome to Family CashCards Portal";
    }

    @PostMapping("/create")
    private ResponseEntity<?> createCashCardUser(@RequestBody CashCardUser newCashCardUser, UriComponentsBuilder ucb) throws ResponseStatusException {

        if(cashCardUserRepository.existsById(newCashCardUser.getUsername())) {
//            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username already exists");
            return ResponseEntity.badRequest().body("Username already exists");
        }

        CashCardUser encodedCashCardUser = new CashCardUser(newCashCardUser.getUsername(), bCryptPasswordEncoder.encode(newCashCardUser.getPassword()));
        CashCardUser savedCashCardUser = cashCardUserRepository.save(encodedCashCardUser);

//        Following lines hold the authentication of the newly created user in security context

//        UserDetails userDetails = new MyUserDetails(savedCashCardUser);
//        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails.getUsername(), userDetails.getPassword(), userDetails.getAuthorities());
//        SecurityContextHolder.getContext().setAuthentication(authentication);

//      after creation of user, redirect user to the home page of their account
        URI newUserHome = ucb
                .path("/home")
                .build()
                .toUri();

        return ResponseEntity.created(newUserHome).build();
    }

    @PostMapping("/login")
    private ResponseEntity<Void> login(@RequestBody CashCardUser cashCardUser) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(cashCardUser.getUsername(), cashCardUser.getPassword())
        );
//        SecurityContextHolder.getContext().setAuthentication(authentication);

        return ResponseEntity.ok().build();
    }
}
