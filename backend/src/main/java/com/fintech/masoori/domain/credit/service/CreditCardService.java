package com.fintech.masoori.domain.credit.service;

import java.util.List;

import com.fintech.masoori.domain.credit.dto.CreditCardRes;
import com.fintech.masoori.domain.credit.entity.CreditCard;

public interface CreditCardService {
	/**
	 * 유저-카드 전체 조회
	 */
	CreditCardRes selectAll(String userEmail);

	CreditCard selectOne(Long id);

	void save (CreditCard creditCard);
}