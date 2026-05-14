package com.daniela.AI_Onboarding___FinTrack.service;

import com.daniela.AI_Onboarding___FinTrack.domain.dto.TransactionDTO;
import com.daniela.AI_Onboarding___FinTrack.domain.dto.TransactionRequest;
import com.daniela.AI_Onboarding___FinTrack.domain.enums.CategoryType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.UUID;

public interface TransactionService {

  Page<TransactionDTO> findAll(LocalDate fromDate, LocalDate toDate, UUID categoryId,
      CategoryType type, Pageable pageable);

  TransactionDTO findById(UUID id);

  TransactionDTO create(TransactionRequest request);

  TransactionDTO update(UUID id, TransactionRequest request);

  void delete(UUID id);
}
