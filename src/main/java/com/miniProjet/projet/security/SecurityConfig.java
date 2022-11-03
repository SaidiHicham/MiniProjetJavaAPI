package com.miniProjet.projet.security;

import com.miniProjet.projet.filter.CustomAuthFilter;
import com.miniProjet.projet.filter.CustomAuthzFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserDetailsService userDetailsService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder);
    }

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {


        CustomAuthFilter caf = new CustomAuthFilter(authenticationManagerBean());
        caf.setFilterProcessesUrl("/api/auth");

        httpSecurity.csrf().disable();
        httpSecurity.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        httpSecurity.authorizeRequests().antMatchers("/api/users/generate/**").permitAll();
        httpSecurity.authorizeRequests().antMatchers("/api/users/batch/**").permitAll();
        httpSecurity.authorizeRequests().antMatchers("/api/auth").permitAll();
        httpSecurity.authorizeRequests().antMatchers("/v3/**").permitAll();
        httpSecurity.authorizeRequests().antMatchers("/swagger-ui/**").permitAll();
        httpSecurity.authorizeRequests().antMatchers("/actuator/**").permitAll();
        httpSecurity.authorizeRequests().antMatchers("/api/users/me").hasAnyAuthority("user");
        httpSecurity.authorizeRequests().antMatchers("/api/users/me").hasAnyAuthority("admin");
        httpSecurity.authorizeRequests().antMatchers("/api/users/me/**").hasAnyAuthority("admin");
        httpSecurity.authorizeRequests().antMatchers("/api/users/**").hasAnyAuthority("admin");
        httpSecurity.authorizeRequests().anyRequest().authenticated();
        httpSecurity.addFilter(caf);
        httpSecurity.addFilterBefore(new CustomAuthzFilter(), UsernamePasswordAuthenticationFilter.class);



    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception{
        return super.authenticationManagerBean();
    }

}