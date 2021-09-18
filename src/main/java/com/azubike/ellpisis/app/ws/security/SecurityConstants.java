package com.azubike.ellpisis.app.ws.security;

import com.azubike.ellpisis.app.ws.SpringApplicationContext;

public class SecurityConstants {
	public static final long EXPIRATION_TIME = 864000000; // 10 days
	public static final String TOKEN_PREFIX = "Bearer ";
	public static final String HEADER_STRING = "Authorization";
	public static final String SIGN_UP_URL = "/users";
	public static final String TOKEN_SECRET = "j6xwyvChZm0j41";
	public static final String EMAIL_VERIFICATION_URL = "/users/email-verification";
	public static final String PASSWORD_RESET_REQUEST_URL = "/users/password-reset-request";
	public static final String PASSWORD_RESET_URL = "/users/password-reset";
	public static final long PASSWORD_RESET_EXPIRATION_TIME = 1000 * 60 * 60; // 1hr
	public static final String H2_CONSOLE = "/h2-console/**";

	public static String getTokenSecret() {
		AppProperties appProperties = (AppProperties) SpringApplicationContext.getBean("appProperties");
		return appProperties.getTokenSecret();
	}

}
