package com.code.analysis.core;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Optional;
import org.junit.jupiter.api.Test;

class OperationExecutorTest {

  @Test
  void shouldSafelyExecuteOperation() {
    var result = OperationExecutor.safeExecute(() -> Optional.of("success"), "Failed to execute");
    assertThat(result).isEqualTo("success");

    assertThatThrownBy(() ->
      OperationExecutor.safeExecute(() -> Optional.empty(), "Failed to execute")
    )
      .isInstanceOf(IllegalStateException.class)
      .hasMessageContaining("Failed to execute");

    assertThatThrownBy(() ->
      OperationExecutor.safeExecute(
        () -> {
          throw new RuntimeException("error");
        },
        "Failed to execute"
      )
    )
      .isInstanceOf(IllegalStateException.class)
      .hasMessageContaining("Failed to execute: error");
  }
}
