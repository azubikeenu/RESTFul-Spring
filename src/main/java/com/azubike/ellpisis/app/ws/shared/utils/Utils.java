package com.azubike.ellpisis.app.ws.shared.utils;

import java.util.Date;
import java.util.Random;

import org.springframework.stereotype.Component;

import com.azubike.ellpisis.app.ws.security.SecurityConstants;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;

@Component
public class Utils {
	private final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
	private final Random RANDOM = new Random();

	public String generateRandomUserId(int length) {
		return generateRandomString(length);
	}

	public String generateRadomAddressId(int length) {
		return generateRandomString(length);
	}

	private String generateRandomString(int length) {
		StringBuilder output = new StringBuilder(length);
		for (int i = 0; i < length; i++) {
			output.append(ALPHABET.charAt(RANDOM.nextInt(ALPHABET.length())));
		}
		return output.toString();
	}

	public boolean hasTokenExpired(String token) {
		boolean returnedValue = true;
		try {
			Claims claims = Jwts.parser().setSigningKey(SecurityConstants.getTokenSecret()).parseClaimsJws(token)
					.getBody();
			Date tokenExpirationDate = claims.getExpiration();
			Date todaysDate = new Date();
			returnedValue = tokenExpirationDate.before(todaysDate);
		} catch (ExpiredJwtException | SignatureException ex) {
			returnedValue = true;
		}
		return returnedValue;

	}

	public String generateEmailVerificationToken(String userId) {
		String token = Jwts.builder().setSubject(userId)
				.setExpiration(new Date(System.currentTimeMillis() + SecurityConstants.EXPIRATION_TIME))
				.signWith(SignatureAlgorithm.HS512, SecurityConstants.TOKEN_SECRET).compact();
		return token;
	}

	public String generatePasswordResetToken(String userId) {
		String token = Jwts.builder().setSubject(userId)
				.setExpiration(new Date(System.currentTimeMillis() + SecurityConstants.PASSWORD_RESET_EXPIRATION_TIME))
				.signWith(SignatureAlgorithm.HS512, SecurityConstants.TOKEN_SECRET).compact();
		return token;
	}

}
