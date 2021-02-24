package com.example.jwtserver.filter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class MyFilter3 implements Filter {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) servletRequest;
        HttpServletResponse res = (HttpServletResponse) servletResponse;

        req.setCharacterEncoding("UTF-8");

        // 토큰 : cos 이걸 만들어줘야 함. id, pwd 가 정상적으로 들어와서 로그인이 안료되면 토큰을 만들어주고 그걸 응답을 해준다.
        // 요청할 때마다 header에 Authorization에 value 값으로 토큰을 가자고 오겠죠?
        // 그때 토큰이 넘어오면 이 토큰이 내가 만든 토큰이 맞는지만 검증만 하면 됨. (RAS, HS256)

        if (req.getMethod().equals("POST")){
            System.out.println("포스트 요청됨");
            String headerAuth = req.getHeader("Authorization");
            System.out.println("headerAuth = " + headerAuth);

            System.out.println("필터 1");
            if (headerAuth.equals("cos")){
                filterChain.doFilter(req, res);
            } else {
                PrintWriter out = res.getWriter();
                out.println("No Authorization");
            }
        }
        System.out.println("필터 3");
    }
}
