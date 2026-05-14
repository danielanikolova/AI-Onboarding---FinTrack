package com.daniela.AI_Onboarding___FinTrack.service;

import com.daniela.AI_Onboarding___FinTrack.domain.dto.TransactionDTO;
import com.daniela.AI_Onboarding___FinTrack.domain.dto.TransactionRequest;
import com.daniela.AI_Onboarding___FinTrack.domain.enums.CategoryType;
import com.daniela.AI_Onboarding___FinTrack.domain.model.Account;
import com.daniela.AI_Onboarding___FinTrack.domain.model.Category;
import com.daniela.AI_Onboarding___FinTrack.domain.model.Transaction;
import com.daniela.AI_Onboarding___FinTrack.exception.ResourceNotFoundException;
import com.daniela.AI_Onboarding___FinTrack.repository.AccountRepository;
import com.daniela.AI_Onboarding___FinTrack.repository.CategoryRepository;
import com.daniela.AI_Onboarding___FinTrack.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class TransactionServiceImpl implements TransactionService {

  private final TransactionRepository transactionRepository;
  private final AccountRepository accountRepository;
  private final CategoryRepository categoryRepository;

  // Placeholder — replace with Spring Security context once auth is implemented
  private UUID currentUserId() {
    return UUID.fromString("00000000-0000-0000-0000-000000000001");
  }

  @Override
  public Page<TransactionDTO> findAll(LocalDate fromDate, LocalDate toDate,
      UUID categoryId, CategoryType type, Pageable pageable) {
    List<UUID> accountIds = accountRepository.findByUserIdAndIsActiveTrue(currentUserId())
        .stream().map(Account::getId).toList();
    return transactionRepository
        .findByFilters(accountIds, fromDate, toDate, categoryId, type, pageable)
        .map(this::toDTO);
  }

  @Override
  public TransactionDTO findById(UUID id) {
    return toDTO(findOwnedOrThrow(id));
  }

  @Override
  @Transactional
  public TransactionDTO create(TransactionRequest request) {
    Account account = accountRepository
        .findByIdAndUserIdAndIsActiveTrue(request.accountId(), currentUserId())
        .orElseThrow(() -> new ResourceNotFoundException("Account", request.accountId()));
    Category category = categoryRepository.findActiveById(request.categoryId())
        .orElseThrow(() -> new ResourceNotFoundException("Category", request.categoryId()));

    Transaction transaction = Transaction.builder()
        .accountId(account.getId())
        .category(category)
        .amount(request.amount())
        .description(request.description())
        .transactionDate(request.transactionDate())
        .build();
    Transaction saved = transactionRepository.save(transaction);
    log.info("Created transaction id={} accountId={}", saved.getId(), account.getId());
    return toDTO(saved);
  }

  @Override
  @Transactional
  public TransactionDTO update(UUID id, TransactionRequest request) {
    Transaction transaction = findOwnedOrThrow(id);
    Account account = accountRepository
        .findByIdAndUserIdAndIsActiveTrue(request.accountId(), currentUserId())
        .orElseThrow(() -> new ResourceNotFoundException("Account", request.accountId()));
    Category category = categoryRepository.findActiveById(request.categoryId())
        .orElseThrow(() -> new ResourceNotFoundException("Category", request.categoryId()));

    transaction.setAccountId(account.getId());
    transaction.setCategory(category);
    transaction.setAmount(request.amount());
    transaction.setDescription(request.description());
    transaction.setTransactionDate(request.transactionDate());
    log.info("Updated transaction id={}", id);
    return toDTO(transactionRepository.save(transaction));
  }

  @Override
  @Transactional
  public void delete(UUID id) {
    Transaction transaction = findOwnedOrThrow(id);
    transactionRepository.delete(transaction);
    log.info("Deleted transaction id={}", id);
  }

  private Transaction findOwnedOrThrow(UUID id) {
    Transaction t = transactionRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Transaction", id));
    boolean owned = accountRepository
        .findByIdAndUserIdAndIsActiveTrue(t.getAccountId(), currentUserId())
        .isPresent();
    if (!owned) throw new ResourceNotFoundException("Transaction", id);
    return t;
  }

  private TransactionDTO toDTO(Transaction t) {
    return new TransactionDTO(
        t.getId(), t.getAccountId(), t.getCategory().getId(),
        t.getCategory().getName(), t.getCategory().getType(),
        t.getAmount(), t.getDescription(), t.getTransactionDate(),
        t.getCreatedAt(), t.getUpdatedAt());
  }
}
