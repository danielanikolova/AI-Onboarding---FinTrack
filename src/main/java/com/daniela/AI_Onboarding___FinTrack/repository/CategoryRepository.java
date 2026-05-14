package com.daniela.AI_Onboarding___FinTrack.repository;

import com.daniela.AI_Onboarding___FinTrack.domain.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CategoryRepository extends JpaRepository<Category, UUID> {

  @Query("SELECT c FROM Category c WHERE c.deletedAt IS NULL")
  List<Category> findAllActive();

  @Query("SELECT c FROM Category c WHERE c.id = :id AND c.deletedAt IS NULL")
  Optional<Category> findActiveById(UUID id);

  boolean existsByNameAndDeletedAtIsNull(String name);
}
