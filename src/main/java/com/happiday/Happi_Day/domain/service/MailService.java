package com.happiday.Happi_Day.domain.service;

import com.happiday.Happi_Day.exception.CustomException;
import com.happiday.Happi_Day.exception.ErrorCode;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.security.SecureRandom;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender javaMailSender;

    @Value("${spring.mail.from.address}")
    private String fromAddress;
    @Value("${spring.mail.from.name}")
    private String fromName;

    // 인증코드 만들기
    public String createNumber() {
        SecureRandom random = new SecureRandom();
        int num = random.nextInt(1000000);
        return String.format("%06d", num);
    }

    public void sendEmail(String to, String code) {
        MimeMessage message = createMessage(to, code);
        try {
            javaMailSender.send(message);
        } catch (MailException es) {
            log.info(es.getMessage());
            throw new CustomException(ErrorCode.MAIL_SEND_ERROR);
        }
    }

    public MimeMessage createMessage(String to, String code) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();

            message.addRecipients(MimeMessage.RecipientType.TO, to);
            message.setSubject("HappiDay 이메일 인증 코드입니다.");
            message.setText("이메일 인증코드: " + code);
            message.setFrom(new InternetAddress(fromAddress, fromName));
            return message;
        } catch (MessagingException e) {
            throw new CustomException(ErrorCode.MAIL_FORMAT_ERROR);
        } catch (UnsupportedEncodingException e) {
            throw new CustomException(ErrorCode.MAIL_ENCODING_ERROR);
        }
    }
}
