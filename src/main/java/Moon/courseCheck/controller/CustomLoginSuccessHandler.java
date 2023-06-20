package Moon.courseCheck.controller;

import Moon.courseCheck.service.JwtService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Controller;

import java.io.IOException;

@Controller
@Slf4j
public class CustomLoginSuccessHandler implements AuthenticationSuccessHandler {
    private final JwtService jwtService;

    public CustomLoginSuccessHandler(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        log.info("로그인 성공 -> 사용자명 : {}",request.getParameter("username"));
        String jwtToken = jwtService.generateToken(authentication);
        Cookie cookie = new Cookie("jwtToken", jwtToken);
        cookie.setHttpOnly(true); // HttpOnly 속성 설정
        cookie.setMaxAge(3600); // 토큰의 만료 시간 설정 (예: 1시간)
        cookie.setPath("/"); // 쿠키의 유효 범위 설정 (루트 경로로 설정하면 전체 사이트에서 접근 가능)
        response.addCookie(cookie);
        response.sendRedirect("/home");
    }
}
