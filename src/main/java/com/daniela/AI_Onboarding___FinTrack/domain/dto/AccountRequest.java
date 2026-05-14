package com.daniela.AI_Onboarding___FinTrack.domain.dto;

import com.daniela.AI_Onboarding___FinTrack.domain.enums.AccountType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record AccountRequest(
    @NotBlank String name,
    @NotNull AccountType type,
    @NotBlank @Size(min = 3, max = 3) String currency,
    @PositiveOrZero BigDecimal initialBalance
) {}
