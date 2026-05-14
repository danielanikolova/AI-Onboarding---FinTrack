package com.daniela.AI_Onboarding___FinTrack.service;

import com.daniela.AI_Onboarding___FinTrack.domain.dto.CategoryDTO;
import com.daniela.AI_Onboarding___FinTrack.domain.dto.CategoryRequest;
import com.daniela.AI_Onboarding___FinTrack.domain.model.Category;
import com.daniela.AI_Onboarding___FinTrack.exception.DuplicateResourceException;
import com.daniela.AI_Onboarding___FinTrack.exception.ResourceNotFoundException;
import com.daniela.AI_Onboarding___FinTrack.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class CategoryServiceImpl implements CategoryService {

  private final CategoryRepository categoryRepository;

  @Override
  public List<CategoryDTO> findAll() {
    return categoryRepository.findAllActive().stream()
        .map(this::toDTO)
        .toList();
  }

  @Override
  public CategoryDTO findById(UUID id) {
    return toDTO(findActiveOrThrow(id));
  }

  @Override
  @Transactional
  public CategoryDTO create(CategoryRequest request) {
    if (categoryRepository.existsByNameAndDeletedAtIsNull(request.name())) {
      throw new DuplicateResourceException("Category", "name", request.name());
    }
    Category category = Category.builder()
        .name(request.name())
        .type(request.type())
        .description(request.description())
        .build();
    Category saved = categoryRepository.save(category);
    log.info("Created category id={} name={}", saved.getId(), saved.getName());
    return toDTO(saved);
  }

  @Override
  @Transactional
  public CategoryDTO update(UUID id, CategoryRequest request) {
    Category category = findActiveOrThrow(id);
    if (!category.getName().equals(request.name())
        && categoryRepository.existsByNameAndDeletedAtIsNull(request.name())) {
      throw new DuplicateResourceException("Category", "name", request.name());
    }
    category.setName(request.name());
    category.setType(request.type());
    category.setDescription(request.description());
    log.info("Updated category id={}", id);
    return toDTO(categoryRepository.save(category));
  }

  @Override
  @Transactional
  public void delete(UUID id) {
    Category category = findActiveOrThrow(id);
    category.setDeletedAt(OffsetDateTime.now());
    categoryRepository.save(category);
    log.info("Soft-deleted category id={}", id);
  }

  private Category findActiveOrThrow(UUID id) {
    return categoryRepository.findActiveById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Category", id));
  }

  private CategoryDTO toDTO(Category c) {
    return new CategoryDTO(c.getId(), c.getName(), c.getType(),
        c.getDescription(), c.getCreatedAt(), c.getUpdatedAt());
  }
}
