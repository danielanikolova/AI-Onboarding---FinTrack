package com.daniela.AI_Onboarding___FinTrack.domain.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record TransactionRequest(
    @NotNull UUID accountId,
    @NotNull UUID categoryId,
    @NotNull @Positive BigDecimal amount,
    String description,
    @NotNull LocalDate transactionDate
) {}
