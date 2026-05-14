package com.daniela.AI_Onboarding___FinTrack.repository;

import com.daniela.AI_Onboarding___FinTrack.domain.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AccountRepository extends JpaRepository<Account, UUID> {

  List<Account> findByUserIdAndIsActiveTrue(UUID userId);

  Optional<Account> findByIdAndUserIdAndIsActiveTrue(UUID id, UUID userId);

  boolean existsByNameAndUserIdAndIsActiveTrue(String name, UUID userId);
}
