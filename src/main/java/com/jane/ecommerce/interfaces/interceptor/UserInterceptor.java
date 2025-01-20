package com.jane.ecommerce.interfaces.interceptor;

import com.jane.ecommerce.domain.user.User;
import com.jane.ecommerce.domain.user.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.math.BigDecimal;

@Slf4j
@RequiredArgsConstructor
@Component
public class UserInterceptor implements HandlerInterceptor {

    private final UserService userService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        HttpSession session = request.getSession(false);

        if (session == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            log.warn("UserInterceptor: User unauthorized");
            return false;
        }

        // 임시 유저 정보 주입
        if (session.getAttribute("user") == null) {
            session.setAttribute("user", User.create("admin@abc.com", "", BigDecimal.ZERO));
        }

        User user = (User) session.getAttribute("user");

        if (user == null || !userService.isUserExists(user.getUsername())) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            log.warn("UserInterceptor: User unauthorized");
            return false;
        }

        request.setAttribute("user", user); // Request 에 유저 정보 저장

        return true; // true: 다음 인터셉터나 컨트롤러로 요청을 전달, false: 요청을 중단하고 응답
    }
}
