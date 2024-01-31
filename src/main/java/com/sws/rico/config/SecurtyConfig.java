package com.sws.rico.config;

import com.sws.rico.filter.JwtAuthenticationFilter;
import com.sws.rico.token.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurtyConfig {
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisTemplate<String, String> redisTemplate;
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http.formLogin().loginPage("/login").defaultSuccessUrl("/")
//                .usernameParameter("email").failureUrl("/login")
//                        .and().logout().logoutUrl("/logout");

        http.csrf().disable().httpBasic().disable() //.logout().logoutUrl("/1313131678979876545646")
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        http.authorizeRequests()
                        .antMatchers("/", "/itemsv2", "/signup", "/login", "/register").permitAll()
                        .antMatchers("/h2-console/**").permitAll()
                        .antMatchers("/images/**", "/img/**", "/js/**", "/css/**", "/fonts/**").permitAll()
                        .antMatchers("/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                        .and().headers().frameOptions().disable()
                        .and().addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider,redisTemplate),UsernamePasswordAuthenticationFilter.class);

        // http.exceptionHandling()
        //         .authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint("/login"));


        return http.build();
    }
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
