package com.daniela.AI_Onboarding___FinTrack.domain.dto;

import com.daniela.AI_Onboarding___FinTrack.domain.enums.CategoryType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CategoryRequest(
    @NotBlank String name,
    @NotNull CategoryType type,
    String description
) {}
