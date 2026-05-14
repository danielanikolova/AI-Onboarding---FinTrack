package com.daniela.AI_Onboarding___FinTrack.domain.dto;

import com.daniela.AI_Onboarding___FinTrack.domain.enums.CategoryType;

import java.time.OffsetDateTime;
import java.util.UUID;

public record CategoryDTO(
    UUID id,
    String name,
    CategoryType type,
    String description,
    OffsetDateTime createdAt,
    OffsetDateTime updatedAt
) {}
