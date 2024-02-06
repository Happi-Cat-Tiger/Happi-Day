package com.happiday.Happi_Day.domain.controller;

import com.happiday.Happi_Day.domain.entity.user.CustomUserDetails;
import com.happiday.Happi_Day.domain.entity.user.RoleType;
import com.happiday.Happi_Day.domain.entity.user.User;
import com.happiday.Happi_Day.domain.entity.user.dto.UserFindDto;
import com.happiday.Happi_Day.domain.entity.user.dto.UserLoginDto;
import com.happiday.Happi_Day.domain.entity.user.dto.UserNumDto;
import com.happiday.Happi_Day.domain.entity.user.dto.UserRegisterDto;
import com.happiday.Happi_Day.domain.repository.UserRepository;
import com.happiday.Happi_Day.domain.service.JpaUserDetailsManager;
import com.happiday.Happi_Day.domain.service.TokenService;
import com.happiday.Happi_Day.domain.service.UserService;
import com.happiday.Happi_Day.exception.CustomException;
import com.happiday.Happi_Day.exception.ErrorCode;
import com.happiday.Happi_Day.jwt.JwtTokenDto;
import com.happiday.Happi_Day.jwt.JwtTokenUtils;
import com.happiday.Happi_Day.utils.DefaultImageUtils;
import com.happiday.Happi_Day.utils.SecurityUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class UserAuthController {

    private final UserRepository userRepository;
    private final JpaUserDetailsManager manager;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;
    private final UserService userService;
    private final DefaultImageUtils defaultImageUtils;

    @PostMapping("/signup")
    public ResponseEntity<String> registerUser(@Validated @RequestBody UserRegisterDto dto) {
        userService.createUser(dto);
        return new ResponseEntity<>("회원가입을 완료했습니다.", HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<JwtTokenDto> login(@Validated @RequestBody UserLoginDto dto) {
        UserDetails userDetails = manager.loadUserByUsername(dto.getUsername());
        if (!passwordEncoder.matches(dto.getPassword(), userDetails.getPassword()))
            throw new CustomException(ErrorCode.PASSWORD_NOT_MATCHED);

        String accessToken = tokenService.setToken(dto.getUsername());
        JwtTokenDto token = new JwtTokenDto();
        token.setToken(accessToken);

        User user = userRepository.findByUsername(dto.getUsername()).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        user.setLastLoginAt(LocalDateTime.now());
        userRepository.save(user);
        return new ResponseEntity<>(token, HttpStatus.OK);
    }

    @GetMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request, HttpServletResponse response) {
        String username = SecurityUtils.getCurrentUsername();
        tokenService.logout(username);
        new SecurityContextLogoutHandler().logout(request, response, SecurityContextHolder.getContext().getAuthentication());
        return new ResponseEntity<>("로그아웃되었습니다.", HttpStatus.OK);
    }

    @PostMapping("/admin")
    public ResponseEntity<?> registerAdmin(@Validated @RequestBody UserRegisterDto dto) {
        userService.createAdmin(dto);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PostMapping("/find")
    public ResponseEntity<String> findPassword(@RequestBody UserFindDto dto) throws Exception {
        return new ResponseEntity<>(userService.findPassword(dto), HttpStatus.OK);
    }

    @PostMapping("/check")
    public ResponseEntity<Boolean> checkEmail(@RequestBody UserNumDto dto) {
        return new ResponseEntity<>(userService.checkEmail(dto), HttpStatus.OK);
    }

    @PostMapping("/password")
    public ResponseEntity<String> changePassword(@RequestBody UserLoginDto dto) {
        userService.changePassword(dto);
        return new ResponseEntity<>("비밀번호가 변경되었습니다.", HttpStatus.OK);
    }

}