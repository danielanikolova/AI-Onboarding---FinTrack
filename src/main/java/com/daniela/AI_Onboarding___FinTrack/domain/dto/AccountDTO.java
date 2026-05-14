package com.daniela.AI_Onboarding___FinTrack.domain.dto;

import com.daniela.AI_Onboarding___FinTrack.domain.enums.AccountType;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public record AccountDTO(
    UUID id,
    String name,
    AccountType type,
    String currency,
    BigDecimal initialBalance,
    BigDecimal currentBalance,
    Boolean isActive,
    OffsetDateTime createdAt,
    OffsetDateTime updatedAt
) {}
