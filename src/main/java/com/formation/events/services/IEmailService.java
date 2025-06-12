package com.formation.events.services;

public interface IEmailService {
  void sendVerificationEmail(String email, String token);
}
