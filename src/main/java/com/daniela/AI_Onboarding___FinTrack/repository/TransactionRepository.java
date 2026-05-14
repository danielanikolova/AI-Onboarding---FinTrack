package com.daniela.AI_Onboarding___FinTrack.repository;

import com.daniela.AI_Onboarding___FinTrack.domain.enums.CategoryType;
import com.daniela.AI_Onboarding___FinTrack.domain.model.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface TransactionRepository extends JpaRepository<Transaction, UUID> {

  @Query("""
      SELECT t FROM Transaction t
      WHERE t.accountId IN :accountIds
        AND (:fromDate IS NULL OR t.transactionDate >= :fromDate)
        AND (:toDate IS NULL OR t.transactionDate <= :toDate)
        AND (:categoryId IS NULL OR t.category.id = :categoryId)
        AND (:type IS NULL OR t.category.type = :type)
      """)
  Page<Transaction> findByFilters(
      @Param("accountIds") List<UUID> accountIds,
      @Param("fromDate") LocalDate fromDate,
      @Param("toDate") LocalDate toDate,
      @Param("categoryId") UUID categoryId,
      @Param("type") CategoryType type,
      Pageable pageable
  );

  @Query("""
      SELECT COALESCE(SUM(CASE WHEN t.category.type = 'INCOME' THEN t.amount ELSE -t.amount END), 0)
      FROM Transaction t WHERE t.accountId = :accountId
      """)
  BigDecimal calculateBalanceDelta(@Param("accountId") UUID accountId);
}
