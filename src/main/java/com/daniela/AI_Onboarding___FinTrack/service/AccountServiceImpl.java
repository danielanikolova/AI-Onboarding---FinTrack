package com.daniela.AI_Onboarding___FinTrack.service;

import com.daniela.AI_Onboarding___FinTrack.domain.dto.AccountDTO;
import com.daniela.AI_Onboarding___FinTrack.domain.dto.AccountRequest;
import com.daniela.AI_Onboarding___FinTrack.domain.model.Account;
import com.daniela.AI_Onboarding___FinTrack.exception.DuplicateResourceException;
import com.daniela.AI_Onboarding___FinTrack.exception.ResourceNotFoundException;
import com.daniela.AI_Onboarding___FinTrack.repository.AccountRepository;
import com.daniela.AI_Onboarding___FinTrack.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class AccountServiceImpl implements AccountService {

  private final AccountRepository accountRepository;
  private final TransactionRepository transactionRepository;

  // Placeholder — replace with Spring Security context once auth is implemented
  private UUID currentUserId() {
    return UUID.fromString("00000000-0000-0000-0000-000000000001");
  }

  @Override
  public List<AccountDTO> findAllForCurrentUser() {
    UUID userId = currentUserId();
    return accountRepository.findByUserIdAndIsActiveTrue(userId).stream()
        .map(this::toDTO)
        .toList();
  }

  @Override
  public AccountDTO findById(UUID id) {
    return toDTO(findActiveForCurrentUserOrThrow(id));
  }

  @Override
  @Transactional
  public AccountDTO create(AccountRequest request) {
    UUID userId = currentUserId();
    if (accountRepository.existsByNameAndUserIdAndIsActiveTrue(request.name(), userId)) {
      throw new DuplicateResourceException("Account", "name", request.name());
    }
    Account account = Account.builder()
        .userId(userId)
        .name(request.name())
        .type(request.type())
        .currency(request.currency() != null ? request.currency() : "BGN")
        .initialBalance(request.initialBalance() != null ? request.initialBalance() : BigDecimal.ZERO)
        .build();
    Account saved = accountRepository.save(account);
    log.info("Created account id={} for userId={}", saved.getId(), userId);
    return toDTO(saved);
  }

  @Override
  @Transactional
  public AccountDTO update(UUID id, AccountRequest request) {
    UUID userId = currentUserId();
    Account account = findActiveForCurrentUserOrThrow(id);
    if (!account.getName().equals(request.name())
        && accountRepository.existsByNameAndUserIdAndIsActiveTrue(request.name(), userId)) {
      throw new DuplicateResourceException("Account", "name", request.name());
    }
    account.setName(request.name());
    account.setType(request.type());
    account.setCurrency(request.currency());
    account.setInitialBalance(request.initialBalance());
    log.info("Updated account id={}", id);
    return toDTO(accountRepository.save(account));
  }

  @Override
  @Transactional
  public void deactivate(UUID id) {
    Account account = findActiveForCurrentUserOrThrow(id);
    account.setIsActive(false);
    accountRepository.save(account);
    log.info("Deactivated account id={}", id);
  }

  private Account findActiveForCurrentUserOrThrow(UUID id) {
    return accountRepository.findByIdAndUserIdAndIsActiveTrue(id, currentUserId())
        .orElseThrow(() -> new ResourceNotFoundException("Account", id));
  }

  private AccountDTO toDTO(Account a) {
    BigDecimal delta = transactionRepository.calculateBalanceDelta(a.getId());
    BigDecimal currentBalance = a.getInitialBalance().add(delta != null ? delta : BigDecimal.ZERO);
    return new AccountDTO(a.getId(), a.getName(), a.getType(), a.getCurrency(),
        a.getInitialBalance(), currentBalance, a.getIsActive(), a.getCreatedAt(), a.getUpdatedAt());
  }
}
