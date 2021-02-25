package com.example.jwtserver.config.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.jwtserver.config.auth.PrincipalDetails;
import com.example.jwtserver.model.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Date;

// 스프링 시큐리티에서 UsernamePasswordAuthenticationFilter 가 있음.
// /login 요청해서 username, password 전송하면 (post)
// UsernamePasswordAuthenticationFilter 동작을 함.

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;


    // login 요청을 하면 로그인 시도를 위해서 실행되는 함수
    @SneakyThrows
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        System.out.println("JwtAuthenticationFilter : 로그인 시도중...");

        // 1. username, password 를 받아서

//        System.out.println(request.getInputStream().toString()); // 스트림 바이트 안에 결국 아이디와 패스워드가 들어있다. 가장 원시적인 방법

//        BufferedReader br = request.getReader();
//        String input = null;
//
//        while((input = br.readLine()) != null){
//            System.out.println(input);
//        }

        ObjectMapper om = new ObjectMapper(); // json을 파싱해주는 라이브러리
        User user = om.readValue(request.getInputStream(), User.class);
        System.out.println("user = " + user);

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword());

        // PrincipalDetailsService 의 loadByUsername() 함수가 실행된 후 정상이면 authentication 이 리턴됨.
        // DB에 있는 username과 password가 일치한다.
        Authentication authentication =
                authenticationManager.authenticate(authenticationToken);

        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
        System.out.println("principalDetails.getUsername() = " + principalDetails.getUsername());

        System.out.println("========================================================================");

        // 2. 정상인지 로그인 시도를 해보는 거에요. authenticationManager 로 로그인 시도를 하면 PrincipalDetailsService가 호출된다.
        // 그럼 loadUserByUsername이 자동으로 실행된다.

        // 3. PrincipalDetails를 세션에 담고  // ?? 새션안쓴다며? 새션에 담지 않으면 권한 관리가 안되기 때문에 담아준다. // ex] user, manager, admin... 만약 권한 관리가 필요 없다면 안해도 된다.

        // 4. JWT 토큰을 만들어서 응답해주면 됨.
        return  authentication;
        // authentication가 리턴될 때 Authentication 객체가 sesstion 영역에 저장됨. => 로그인이 되었다는 뜻 .
        // 리턴의 이유는 권한 관리를 security가 대신 해주기 때문에 편하려고 하는거임.
        // 굳이 jtw 토큰을 사용하면서 세션을 만들 이유가 없음. 근데 단지 권한 처리 때문에 session 을 넣어준다.

    }

    // attemptAuthentication 실행 후 인증이 정상적으로 되었으면 successfulAuthentication 함수가 실행되요.
    // JWT 토큰을 만들어서 request 요청한 사용자에게 JWT 토큰을 response 해주면 됨.
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {

        System.out.println(" successfulAuthentication 실행됨 : 인증이 완료되었다는 뜻임.");

        PrincipalDetails principal = (PrincipalDetails) authResult.getPrincipal();

        // RSA방식은 아니고 hash 암호 방식
        String jwtToken = JWT.create()
                .withSubject("cos토큰")
                .withExpiresAt(new Date(System.currentTimeMillis() + (60000*10))) // 현재 시간 + 토큰 남은 기간 // 참고 60000 = 1분
                .withClaim("id", principal.getUser().getId())
                .withClaim("username", principal.getUser().getUsername()) // withClamim은 내가 넣고 싶은 것은 아무거나 넣어도 된다.
                .sign(Algorithm.HMAC512("cos"));


       response.addHeader("Authorization", "Bearer " + jwtToken); // "Bearer(여기에 꼭 한 칸을 띄어야 한다) " // 사용자한테 header에 담겨서 응답이 된다.
    }
}
