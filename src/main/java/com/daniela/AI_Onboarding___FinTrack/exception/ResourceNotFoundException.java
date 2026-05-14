package com.daniela.AI_Onboarding___FinTrack.exception;

public class ResourceNotFoundException extends RuntimeException {

  public ResourceNotFoundException(String resource, Object id) {
    super(resource + " not found with id: " + id);
  }

  public ResourceNotFoundException(String message) {
    super(message);
  }
}
