package com.daniela.AI_Onboarding___FinTrack.domain.dto;

import com.daniela.AI_Onboarding___FinTrack.domain.enums.CategoryType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

public record TransactionDTO(
    UUID id,
    UUID accountId,
    UUID categoryId,
    String categoryName,
    CategoryType categoryType,
    BigDecimal amount,
    String description,
    LocalDate transactionDate,
    OffsetDateTime createdAt,
    OffsetDateTime updatedAt
) {}
