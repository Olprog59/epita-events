package com.formation.events.services;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements IEmailService {

  @Value("${app.email.verification.url}")
  private String verificationUrl;

  private final JavaMailSender javaMailSender;

  @Override
  @Async
  public void sendVerificationEmail(String email, String token) {
    try {
      System.out.println("Envoi du mail à " + email);

      MimeMessage mimeMessage = javaMailSender.createMimeMessage();
      MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

      helper.setTo(email);
      helper.setSubject("Vérification de votre email - Events");
      helper.setFrom("samuel.michaux@gmail.com");

      String htmlContent = loadEmailTemplate(verificationUrl + "/" + token);

      helper.setText(htmlContent, true);

      javaMailSender.send(mimeMessage);
      System.out.println("Email envoyé avec succès à " + email);
    } catch (MessagingException | IOException e) {
      e.printStackTrace();
    }

  }

  private String loadEmailTemplate(String verificationUrl) throws IOException {
    ClassPathResource resource = new ClassPathResource("templates/email-verification.html");
    String template = Files.readString(resource.getFile().toPath(), StandardCharsets.UTF_8);
    return template.replace("${verificationUrl}", verificationUrl);
  }

}
