package com.example.cashcard.config;

import com.example.cashcard.model.CashCardUser;
import com.example.cashcard.repo.CashCardUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Configuration
//  Tell Spring to use this class to configure spring and spring boot so that any bean in this class will be available for auto configuration
public class SecurityConfig {

    @Bean
    //  Creating a bean to configure Spring Security's Filter Chain
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf().disable();
//        http.cors(Customizer.withDefaults());
//        http.httpBasic();
        //  all Http requests to cashcards/ are required to be authenticated using HTTP Basic Authentication security
        http.authorizeHttpRequests(request -> request
                //  Add role-based authorization
                .requestMatchers("/home/**").hasRole("OWNER")
//                .hasRole("CARD-OWNER")
                        .requestMatchers("/index/**").permitAll()
                );
//        http.formLogin();
        http.formLogin(
                form -> form
                        .loginPage("http://localhost:3000/login")
                        .defaultSuccessUrl("/home")
        );
//        http.logout(
//                request -> request
//                    .logoutUrl("/home/logout")
//                    .logoutSuccessUrl("/index/login?logout")
//        );
        return http.build();
    }

//    This bean allows cross origin resource sharing
//    i.e. in our case for spring to allow access from react
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowCredentials(true);
        corsConfiguration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));
        corsConfiguration.setAllowedOrigins(List.of("http://localhost:3000"));
        corsConfiguration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE"));

        UrlBasedCorsConfigurationSource urlBasedCorsConfigurationSource = new UrlBasedCorsConfigurationSource();
        urlBasedCorsConfigurationSource.registerCorsConfiguration("/**", corsConfiguration);
        return urlBasedCorsConfigurationSource;
    }

    @Bean
    public CorsFilter corsFilter() {
        return new CorsFilter(corsConfigurationSource());
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

//  We need a UserDetailsService bean to be configured in order to allow spring to call this service
//  to retrieve the user from the database as a UserDetails object for Spring to authenticate the user
    @Bean
    public UserDetailsService getValidUser() {
        return new UserDetailsService() {

            @Autowired
            CashCardUserRepository cashCardUserRepository;

//          Retrieving UserDetails object by looking up the user by username from the database
            @Override
            public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
                Optional<CashCardUser> cashCardUser = cashCardUserRepository.findByUsername(username);

                cashCardUser.orElseThrow(() -> new UsernameNotFoundException("Not Found: " + username));

                return cashCardUser.map(MyUserDetails::new).get();
            }
        };
    }

//  -----------------------------------------------------------------------------------------------------
//  CUSTOM AUTHENTICATION PROVIDER --> To use your own authentication methods and configure
//  Spring to use your custom authentication method to authenticate users trying to login.
//  -----------------------------------------------------------------------------------------------------
//  @Autowired
//  NoEncodingAuthenticationProvider noEncodingAuthenticationProvider;

//  This bean overrides the spring-security's default authentication by registering a new
//  authentication provider NoEncodingAuthenticationProvider which implements custom
//  authentication method to validate the user credentials.
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity httpSecurity) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder =
                httpSecurity.getSharedObject(AuthenticationManagerBuilder.class);

//        authenticationManagerBuilder.authenticationProvider(noEncodingAuthenticationProvider);
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());

        authenticationManagerBuilder.authenticationProvider(daoAuthenticationProvider);

        return authenticationManagerBuilder.build();
    }

//    ------------------------------------------------------------------------------------------
//    In Memory User Details Service Bean
//    ------------------------------------------------------------------------------------------

//    @Bean
//    //  Makes this bean available for configuration with user credentials
//    public UserDetailsService testOnlyUsers(PasswordEncoder passwordEncoder) {
//        User.UserBuilder users = User.builder();
//        //  Build a user credentials entity
//        UserDetails sarah = users
//                .username("sarah1")
//                .password(passwordEncoder.encode("abc123"))
//                .roles("CARD-OWNER")
//                .build();
//        UserDetails hankOwnsNoCards = users
//                .username("hank-owns-no-cards")
//                .password(passwordEncoder.encode("qrs456"))
//                .roles("NON-OWNER")
//                .build();
//        UserDetails kumar = users
//                .username("kumar2")
//                .password(passwordEncoder.encode("xyz789"))
//                .roles("CARD-OWNER")
//                .build();
//        return new InMemoryUserDetailsManager(sarah, hankOwnsNoCards, kumar);
//    }
}