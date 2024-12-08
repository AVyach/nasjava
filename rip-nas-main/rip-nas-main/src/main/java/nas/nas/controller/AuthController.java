package nas.nas.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import nas.nas.exception.ExternalServiceError;
import nas.nas.model.SignInData;
import nas.nas.model.SignUpData;
import nas.nas.service.AuthService;

import org.springframework.web.bind.annotation.CookieValue;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private int SessionMaxAge = 60*60*24;
    private AuthService authService = new AuthService();

    @PostMapping("/signup")
    public boolean signUp(@RequestBody SignUpData signUpData, HttpServletResponse response) {
        try {
            String token = this.authService.signUp(signUpData.getUsername(), signUpData.getPassword());
            Cookie cookie = new Cookie("token", token);
            cookie.setPath("/api");
            cookie.setMaxAge(SessionMaxAge);
            response.addCookie(cookie);
            return true;
        } catch (ExternalServiceError e) {
            return false;
        }
    }

    @PostMapping("/signin")
    public boolean signIn(@RequestBody SignInData signInData, HttpServletResponse response) {
        try {
            String token = this.authService.signIn(signInData.getUsername(), signInData.getPassword());
            Cookie cookie = new Cookie("token", token);
            cookie.setPath("/api");
            cookie.setMaxAge(SessionMaxAge);
            response.addCookie(cookie);
            return true;
        } catch (ExternalServiceError e) {
            return false;
        }
    }

    @PostMapping("/check")
    public boolean check(@CookieValue("token") String token) {
        try {
            boolean status = this.authService.check(token).isTokenValid();
            return status;
        } catch (ExternalServiceError e) {
            return false;
        }
    }
    
    @PostMapping("/logout")
    public boolean logout(@CookieValue("token") String token, HttpServletResponse response) {
        try {
            this.authService.logout(token);
            Cookie cookie = new Cookie("token", "");
            cookie.setPath("/api");
            cookie.setMaxAge(0);
            response.addCookie(cookie);
            return true;
        } catch (ExternalServiceError e) {
            return false;
        }
    }
    
    @PostMapping("/logoutall")
    public boolean logoutAll(@CookieValue("token") String token, HttpServletResponse response) {
        try {
            this.authService.logoutAll(token);
            Cookie cookie = new Cookie("token", "");
            cookie.setPath("/api");
            cookie.setMaxAge(0);
            response.addCookie(cookie);
            return true;
        } catch (ExternalServiceError e) {
            return false;
        }
    }
}
