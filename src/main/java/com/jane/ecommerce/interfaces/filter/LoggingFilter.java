package com.jane.ecommerce.interfaces.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class LoggingFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        log.info("LoggingFilter Request: {} {}", httpRequest.getMethod(), httpRequest.getRequestURI());
        log.info("LoggingFilter Response: {}", httpResponse.getStatus());

        HttpSession session = httpRequest.getSession(true); // 임시 세션 생성

        chain.doFilter(request, response); // 다음 필터나 서블릿으로 요청 전달
    }
}
