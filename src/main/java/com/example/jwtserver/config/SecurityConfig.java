package com.example.jwtserver.config;

import com.example.jwtserver.config.jwt.JwtAuthenticationFilter;
import com.example.jwtserver.config.jwt.JwtAuthorizationFilter;
import com.example.jwtserver.filter.MyFilter3;
import com.example.jwtserver.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.context.SecurityContextPersistenceFilter;
import org.springframework.web.filter.CorsFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final CorsFilter corsFilter;
    private final UserRepository userRepository;

    @Bean
    public BCryptPasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
//        http.addFilterBefore(new MyFilter3(), SecurityContextPersistenceFilter.class);
        http.csrf().disable();
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS) // 세션을 사용하지 않겠다라는 의미. 즉 stateless한 서버를 만들겠다는 의미
        .and()
        .addFilter(corsFilter) // 크로스 오리진 요청이 와도 전부 허용 // @CrossOrigin(인증이 필요한 요청은 다 거부됨, 인증이 필요한 요청은 다 거부됨), 인증이 필요한 기능을 사용할 경우 필터에 등록을 해줘야 한다.
        .formLogin().disable() // formLogin 안한다.
        .httpBasic().disable() // 기본적인 http 로그인 방식을 사용하지 않는다.
        .addFilter(new JwtAuthenticationFilter(authenticationManager()))  // AuthenticationManager 를 파라미터로 던저줘야 한다. 로그인을 진행하는 필터이기 때문에
        .addFilter(new JwtAuthorizationFilter(authenticationManager(), userRepository))
        .authorizeRequests()
        .antMatchers("/api/v1/user/**")
        .access("hasRole('ROLE_USER') or hasRole('ROLE_MANAGER') or hasRole('ROLE_ADMIN')")
        .antMatchers("/api/v1/manager/**")
        .access("hasRole('ROLE_MANAGER') or hasRole('ROLE_ADMIN')")
        .antMatchers("/api/v1/admin/**")
        .access("hasRole('ROLE_ADMIN')")
        .anyRequest().permitAll();
    }
}
