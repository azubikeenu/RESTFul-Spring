package com.azubike.ellpisis.app.ws.shared.utils;

import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import com.azubike.ellpisis.app.ws.shared.dto.UserDto;
import com.azubike.ellpisis.app.ws.ui.model.response.ErrorMessages;

@Component
public class EmailService {
	@Autowired
	private EmailConfiguration emailConfig;
	final String SUBJECT = "One last step to complete your verification ";
	final String PASSWORD_RESET_SUBJECT = "One last step to complete your verification ";

	final String FROM = "admin@photo-app.com";
	final String TEXT_BODY = "Please verify your email address "
			+ "\n Thank you for registering in our mobile app. To complete your registeration process "
			+ "\n Open the url on your browser window "
			+ "\n http://localhost:8080/mobile-app-ws/users/email-verification?token=${tokenValue}"
			+ "\n Thanks once again ";

	final String HTML_BODY = "<h2>Please verify your email address</h2> "
			+ "<p>Thank you for registering in our mobile app. To complete your registeration process</p> "
			+ "\n Click on the following link "
			+ "\n<a href='http://localhost:8080/mobile-app-ws/users/email-verification?token=${tokenValue}'>please verify</a>"
			+ "\n Thanks once again ";

	final String PASSWORD_RESET_HTML_BODY = "<h2>A request to reset your password</h2> " + "<p>Hi $firstName</p> "
			+ "<p>Someone has requested to change your password if you are not the one please kindly ignore else :</p> "
			+ "\n Click on the following link "
			+ "\n<a href='http://localhost:8083/verification-service/password-reset.html?token=${tokenValue}'>click to reset your password</a>"
			+ "\n Thanks once again ";

	public void verifyEmail(UserDto userDto) throws Exception {
		// Create a mail sender
		JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
		mailSender.setHost(emailConfig.getHost());
		mailSender.setPort(emailConfig.getPort());
		mailSender.setUsername(emailConfig.getUserName());
		mailSender.setPassword(emailConfig.getPassword());
		String htmlBodyWithToken = HTML_BODY.replace("${tokenValue}", userDto.getEmailVerificationToken());
		String textBodyWithToken = TEXT_BODY.replace("${tokenValue}", userDto.getEmailVerificationToken());
		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message);
		helper.setFrom(FROM, "PhotoApp");
		helper.setText(htmlBodyWithToken, true);
		helper.setTo(userDto.getEmail());
		helper.setSubject(SUBJECT);
		try {
			mailSender.send(message);
		} catch (Exception ex) {
			throw new Exception(ErrorMessages.COULD_NOT_SEND_EMAIL.getErrorMessages());
		}

	}

	public boolean sendPasswordRequestEmail(String firstName, String email, String token) throws Exception {
		JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
		mailSender.setHost(emailConfig.getHost());
		mailSender.setPort(emailConfig.getPort());
		mailSender.setUsername(emailConfig.getUserName());
		mailSender.setPassword(emailConfig.getPassword());
		String htmlBodyWithToken = PASSWORD_RESET_HTML_BODY.replace("${tokenValue}", token);
		htmlBodyWithToken = htmlBodyWithToken.replace("$firstName", firstName);
		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message);
		helper.setFrom(FROM, "PhotoApp");
		helper.setText(htmlBodyWithToken, true);
		helper.setTo(email);
		helper.setSubject(SUBJECT);
		try {
			mailSender.send(message);
			return true;
		} catch (Exception ex) {
			return false;
		}
	}
}
