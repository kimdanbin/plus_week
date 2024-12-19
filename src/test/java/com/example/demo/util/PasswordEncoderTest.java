package com.example.demo.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PasswordEncoderTest {

    // 8. 비밀번호 암호화 테스트코드
    @Test
    void testEncode() {
        // given
        String password = "1234";

        // when
        String encodedPassword = PasswordEncoder.encode(password);

        //then 암호화된 비밀번호가 null이 아니고 원래 비밀번호와 달라야 한다
        assertNotNull(encodedPassword, "암호화된 비밀번호가 null이 아님");
        assertNotEquals(password, encodedPassword, "암호화된 비밀번호가 원본과 다름");
    }

    // 해싱된 비밀번호가 원래 비밀번호와 일치하는지 테스트코드
    @Test
    void testMatches() {
        // given
        String password = "1234";
        String encodedPassword = PasswordEncoder.encode(password);

        // when
        boolean matches = PasswordEncoder.matches(password, encodedPassword);

        // then
        assertTrue(matches);
    }

}