package com.daniela.AI_Onboarding___FinTrack.service;

import com.daniela.AI_Onboarding___FinTrack.domain.dto.AccountDTO;
import com.daniela.AI_Onboarding___FinTrack.domain.dto.AccountRequest;

import java.util.List;
import java.util.UUID;

public interface AccountService {

  List<AccountDTO> findAllForCurrentUser();

  AccountDTO findById(UUID id);

  AccountDTO create(AccountRequest request);

  AccountDTO update(UUID id, AccountRequest request);

  void deactivate(UUID id);
}
