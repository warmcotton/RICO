package com.sws.rico.config;

import com.sws.rico.filter.JwtAuthenticationFilter;
import com.sws.rico.token.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurtyConfig {
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisTemplate<String, String> redisTemplate;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;
    private final CustomAuthenticationEntryPoint authenticationEntryPoint;
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf().disable().httpBasic().disable().formLogin().disable() //.logout().logoutUrl("/1313131678979876545646")
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);



        http.authorizeRequests()
                .antMatchers("/refresh","/register","/register/supplier","/reviews/*","/login","/actuator/**").permitAll()
                .antMatchers(HttpMethod.GET, "/category","/items","/items/banner","/items/latest","/items/popular","/items/category","/item/*").permitAll()
                .antMatchers("/images/**", "/img/**", "/js/**", "/css/**", "/fonts/**").permitAll()
                .antMatchers(HttpMethod.POST,"/item").hasAuthority("SUPPLIER")
                .antMatchers(HttpMethod.DELETE, "/item/*").hasAuthority("SUPPLIER")
                .antMatchers(HttpMethod.PUT, "/item/*").hasAuthority("SUPPLIER")
                .antMatchers(HttpMethod.GET, "/user/myitems").hasAuthority("SUPPLIER")
                .antMatchers("/admin/**").hasAuthority("ADMIN")
                .anyRequest().authenticated()
                .and().headers().frameOptions().disable()
                .and().addFilter(corsFilter())
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider,redisTemplate),UsernamePasswordAuthenticationFilter.class);

        http.exceptionHandling().authenticationEntryPoint(authenticationEntryPoint).accessDeniedHandler(customAccessDeniedHandler);

        return http.build();
    }
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOrigin("http://localhost:3000");
        config.addAllowedOrigin("http://192.168.219.108:3000");
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        source.registerCorsConfiguration("/**",config);
        return new CorsFilter(source);
    }
}
