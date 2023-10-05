package com.fintech.masoori.global.rabbitMQ.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class ChallengeRequestMessage {
	private Long cardId;
	private String verse;
}
