package com.daniela.AI_Onboarding___FinTrack.controller;

import com.daniela.AI_Onboarding___FinTrack.domain.dto.CategoryDTO;
import com.daniela.AI_Onboarding___FinTrack.domain.dto.CategoryRequest;
import com.daniela.AI_Onboarding___FinTrack.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

  private final CategoryService categoryService;

  @GetMapping
  public List<CategoryDTO> getAll() {
    return categoryService.findAll();
  }

  @GetMapping("/{id}")
  public CategoryDTO getById(@PathVariable UUID id) {
    return categoryService.findById(id);
  }

  @PostMapping
  public ResponseEntity<CategoryDTO> create(@Valid @RequestBody CategoryRequest request) {
    CategoryDTO created = categoryService.create(request);
    URI location = ServletUriComponentsBuilder.fromCurrentRequest()
        .path("/{id}").buildAndExpand(created.id()).toUri();
    return ResponseEntity.created(location).body(created);
  }

  @PutMapping("/{id}")
  public CategoryDTO update(@PathVariable UUID id, @Valid @RequestBody CategoryRequest request) {
    return categoryService.update(id, request);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable UUID id) {
    categoryService.delete(id);
    return ResponseEntity.noContent().build();
  }
}
