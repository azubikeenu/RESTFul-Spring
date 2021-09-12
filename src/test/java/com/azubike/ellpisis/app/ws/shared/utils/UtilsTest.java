package com.azubike.ellpisis.app.ws.shared.utils;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class UtilsTest {
	@Autowired
	Utils utils;

	@BeforeEach
	void setUp() throws Exception {
	}

	@Test
	void testGenerateRandomUserId() {
		String userId = utils.generateRandomUserId(30);
		String userIdTwo = utils.generateRandomUserId(30);

		assertNotNull(userId);
		assertNotNull(userIdTwo);

		assertTrue(userId.length() == 30);
		assertTrue(!userId.equalsIgnoreCase(userIdTwo));
	}

	@Test
	void testHasTokenNotExpired() {
		String token = utils.generateEmailVerificationToken("dummy_id");
		assertNotNull(token);
		assertFalse(utils.hasTokenExpired(token));
	}

	@Test
	void testHasTokenExpired() {
		String expiredToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6IjVjOGEyM2M4MmY4ZmI4MTRiNTZmYTE4ZCIsImlhdCI6MTYzMTE4ODA2NSwiZXhwIjoxNjM4OTY0MDY1fQ.aU1erscxttnd3c4EfU-QUtYzXXpaBJnGbVntj9DMPSY";
		assertTrue(utils.hasTokenExpired(expiredToken));

	}

}
