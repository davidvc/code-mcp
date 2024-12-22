package com.code.analysis.core;

import java.util.Optional;
import java.util.function.Supplier;

/**
 * Provides utilities for safely executing operations with error handling.
 */
public final class OperationExecutor {

  private OperationExecutor() {
    // Prevent instantiation
  }

  /**
   * Safely executes an operation with error handling.
   *
   * @param operation    The operation to execute
   * @param errorMessage Message to include in exception if operation fails
   * @param <T>          The type of result
   * @return The result of the operation
   * @throws IllegalStateException if the operation fails
   */
  public static <T> T safeExecute(Supplier<Optional<T>> operation, String errorMessage) {
    try {
      return operation.get().orElseThrow(() -> new IllegalStateException(errorMessage));
    } catch (Exception e) {
      throw new IllegalStateException(errorMessage + ": " + e.getMessage(), e);
    }
  }
}
