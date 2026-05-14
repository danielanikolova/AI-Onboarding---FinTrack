package com.daniela.AI_Onboarding___FinTrack.controller;

import com.daniela.AI_Onboarding___FinTrack.domain.dto.AccountDTO;
import com.daniela.AI_Onboarding___FinTrack.domain.dto.AccountRequest;
import com.daniela.AI_Onboarding___FinTrack.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {

  private final AccountService accountService;

  @GetMapping
  public List<AccountDTO> getAll() {
    return accountService.findAllForCurrentUser();
  }

  @GetMapping("/{id}")
  public AccountDTO getById(@PathVariable UUID id) {
    return accountService.findById(id);
  }

  @PostMapping
  public ResponseEntity<AccountDTO> create(@Valid @RequestBody AccountRequest request) {
    AccountDTO created = accountService.create(request);
    URI location = ServletUriComponentsBuilder.fromCurrentRequest()
        .path("/{id}").buildAndExpand(created.id()).toUri();
    return ResponseEntity.created(location).body(created);
  }

  @PutMapping("/{id}")
  public AccountDTO update(@PathVariable UUID id, @Valid @RequestBody AccountRequest request) {
    return accountService.update(id, request);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deactivate(@PathVariable UUID id) {
    accountService.deactivate(id);
    return ResponseEntity.noContent().build();
  }
}
