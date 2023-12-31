package com.fintech.masoori.domain.analytics.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class MonthlySpendingAnalyticsRes {
	private List<MonthlySpendingAnalytics> monthlySpendingAnalyticsList;

	@Data
	public static class MonthlySpendingAnalytics {
		@Schema(description = "카테고리", example = "음식")
		private String category;
		@Schema(description = "카테고리에 총 사용량", example = "230000")
		private Integer cost;

		public MonthlySpendingAnalytics(
			com.fintech.masoori.domain.analytics.entity.MonthlySpendingAnalytics monthlySpendingAnalytics) {
			this.category = monthlySpendingAnalytics.getCategory();
			this.cost = monthlySpendingAnalytics.getCost();
		}
	}
}
