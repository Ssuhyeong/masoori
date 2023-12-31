package com.fintech.masoori.domain.credit.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.cglib.core.Local;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.fintech.masoori.domain.credit.entity.CreditCardUser;

public interface CreditCardUserRepository extends JpaRepository<CreditCardUser, Long> {

	@Query("SELECT c FROM CreditCardUser c WHERE c.user.id = :userId AND c.createdDate >= :startDate AND c.createdDate <= :endDate")
	List<CreditCardUser> findCreditCardsByUserId(@Param("userId") Long userId,
		@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

	@Modifying
	@Query("UPDATE CreditCardUser c SET c.createdDate = :date WHERE c.user.id = :userId AND c.year = :year AND c.month = :month")
	void updateCreatedDate(@Param("date")LocalDateTime date, @Param("userId") long userId, @Param("year") long year, @Param("month") long month);

}
