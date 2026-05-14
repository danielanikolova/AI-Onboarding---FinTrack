package com.daniela.AI_Onboarding___FinTrack.service;

import com.daniela.AI_Onboarding___FinTrack.domain.dto.CategoryDTO;
import com.daniela.AI_Onboarding___FinTrack.domain.dto.CategoryRequest;

import java.util.List;
import java.util.UUID;

public interface CategoryService {

  List<CategoryDTO> findAll();

  CategoryDTO findById(UUID id);

  CategoryDTO create(CategoryRequest request);

  CategoryDTO update(UUID id, CategoryRequest request);

  void delete(UUID id);
}
