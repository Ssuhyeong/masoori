package com.fintech.masoori.domain.user.service;

import java.util.Optional;

import com.fintech.masoori.domain.user.dto.*;
import com.fintech.masoori.domain.user.entity.User;
import com.fintech.masoori.global.config.jwt.TokenInfo;

import antlr.Token;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface UserService {
	boolean checkEmail(String email) throws Exception;

	boolean checkOAuthAccount(String email);

	void signUp(SignUpReq signUpReq);

	TokenInfo login(LoginReq loginReq);

	void logout(HttpServletRequest request, HttpServletResponse response);

	// 사용자 이메일로 조회
	Optional<User> findByEmail(String email);

	void updateInfoAndSendSms(SendSmsReq sendSmsReq, User loginUser);

	void sendSignupEmailCode(String email);

	void sendPasswordEmailCode(String email);

	void verifyEmailCode(EmailCheckReq emailCheckReq);

	void verifySmsCode(SmsCheckReq smsCheckReq);

	void updateSmsAlarm(User loginUser);

	void updateCardGeneration(User loginUser);

	void updateIntegration(User loginUser);
}
