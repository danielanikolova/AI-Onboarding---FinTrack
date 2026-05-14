package com.daniela.AI_Onboarding___FinTrack.controller;

import com.daniela.AI_Onboarding___FinTrack.domain.dto.TransactionDTO;
import com.daniela.AI_Onboarding___FinTrack.domain.dto.TransactionRequest;
import com.daniela.AI_Onboarding___FinTrack.domain.enums.CategoryType;
import com.daniela.AI_Onboarding___FinTrack.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

  private final TransactionService transactionService;

  @GetMapping
  public Page<TransactionDTO> getAll(
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
      @RequestParam(required = false) UUID categoryId,
      @RequestParam(required = false) CategoryType type,
      @PageableDefault(size = 20, sort = "transactionDate", direction = Sort.Direction.DESC) Pageable pageable) {
    return transactionService.findAll(fromDate, toDate, categoryId, type, pageable);
  }

  @GetMapping("/{id}")
  public TransactionDTO getById(@PathVariable UUID id) {
    return transactionService.findById(id);
  }

  @PostMapping
  public ResponseEntity<TransactionDTO> create(@Valid @RequestBody TransactionRequest request) {
    TransactionDTO created = transactionService.create(request);
    URI location = ServletUriComponentsBuilder.fromCurrentRequest()
        .path("/{id}").buildAndExpand(created.id()).toUri();
    return ResponseEntity.created(location).body(created);
  }

  @PutMapping("/{id}")
  public TransactionDTO update(@PathVariable UUID id, @Valid @RequestBody TransactionRequest request) {
    return transactionService.update(id, request);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable UUID id) {
    transactionService.delete(id);
    return ResponseEntity.noContent().build();
  }
}
