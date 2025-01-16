package com.jane.ecommerce.interfaces.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class LoggingFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        log.info("LoggingFilter Request: {} {}", req.getMethod(), req.getRequestURI());
        log.info("LoggingFilter Response: {}", res.getStatus());

        chain.doFilter(request, response); // 다음 필터나 서블릿으로 요청 전달
    }
}
