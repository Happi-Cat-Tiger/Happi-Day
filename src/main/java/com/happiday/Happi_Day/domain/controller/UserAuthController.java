package com.happiday.Happi_Day.domain.controller;

import com.happiday.Happi_Day.domain.entity.user.User;
import com.happiday.Happi_Day.domain.entity.user.dto.*;
import com.happiday.Happi_Day.domain.repository.UserRepository;
import com.happiday.Happi_Day.domain.service.JpaUserDetailsManager;
import com.happiday.Happi_Day.domain.service.TokenService;
import com.happiday.Happi_Day.domain.service.UserService;
import com.happiday.Happi_Day.exception.CustomException;
import com.happiday.Happi_Day.exception.ErrorCode;
import com.happiday.Happi_Day.jwt.JwtTokenDto;
import com.happiday.Happi_Day.jwt.JwtTokenResponse;
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

        JwtTokenResponse tokenResponse = tokenService.setToken(dto.getUsername());
        JwtTokenDto token = new JwtTokenDto(tokenResponse.getAccessToken());

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
    public ResponseEntity<String> findPassword(@RequestBody UserFindDto dto) {
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

    @GetMapping("/terms")
    public ResponseEntity<TermsDto> getTerms() {
        TermsDto dto = new TermsDto("제1조(목적)이 약관은 주식회사 AMP(이하 \"회사\"라 한다)가 운영하는 인터넷 웹사이트인 Duckzill(https://shop.duckzill.xn--com%29%28-j192bvz8f/ \"사이트\"라 한다)에서 제공하는 인터넷 관련 서비스(이하 \"서비스\"라 한다)를 이용함에 있어 서비스와 서비스 이용자의 권리, 의무 및 책임사항, 기타 필요한 사항을 규정함을 목적으로 합니다.※ 「PC통신, 무선 등을 이용하는 전자상거래에 대해서도 그 성질에 반하지 않는 한 이 약관을 준용합니다.」",
                "회사는 회원가입, 민원 등 고객상담 처리, 본인확인(14세 미만 아동 확인) 등 의사소통을 위한 정보 활용 및 이벤트 등과 같은 마케팅용도 활용, 회원의 서비스 이용에 대한 통계, 이용자들의 개인정보를 통한 서비스 개발을 위해 아래와 같은 개인정보를 수집하고 있습니다 1. - 목적 : 이용자 식별 및 본인여부 확인- 항목 : 이름, 아이디, 비밀번호,닉네임, 이메일, 휴대폰번호, 주소, 전화번호 등");
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

}