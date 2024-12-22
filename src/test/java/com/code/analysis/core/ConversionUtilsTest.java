package com.code.analysis.core;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.code.analysis.core.model.Position;
import com.code.analysis.core.model.Scope;
import com.code.analysis.core.model.ScopeLevel;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class ConversionUtilsTest {

  @Test
  void shouldCreatePosition() {
    var position = ConversionUtils.createPosition(1, 2);
    assertThat(position.line()).isEqualTo(1);
    assertThat(position.column()).isEqualTo(2);
    assertThat(position.offset()).isEqualTo(0); // Default value
  }

  @Test
  void shouldCreateScope() {
    var scope = ConversionUtils.createScope(ScopeLevel.GLOBAL, 1, 2, 3, 4);
    assertThat(scope.level()).isEqualTo(ScopeLevel.GLOBAL);
    assertThat(scope.start().line()).isEqualTo(1);
    assertThat(scope.start().column()).isEqualTo(2);
    assertThat(scope.end().line()).isEqualTo(3);
    assertThat(scope.end().column()).isEqualTo(4);
  }

  @Test
  void shouldValidateIdentifiers() {
    assertThat(ConversionUtils.isValidIdentifier("validName")).isTrue();
    assertThat(ConversionUtils.isValidIdentifier("valid_name")).isTrue();
    assertThat(ConversionUtils.isValidIdentifier("_validName")).isTrue();
    assertThat(ConversionUtils.isValidIdentifier("ValidName123")).isTrue();

    assertThat(ConversionUtils.isValidIdentifier("")).isFalse();
    assertThat(ConversionUtils.isValidIdentifier(null)).isFalse();
    assertThat(ConversionUtils.isValidIdentifier("123invalid")).isFalse();
    assertThat(ConversionUtils.isValidIdentifier("invalid-name")).isFalse();
    assertThat(ConversionUtils.isValidIdentifier("invalid name")).isFalse();
  }

  @Test
  void shouldValidateNotEmpty() {
    assertThatThrownBy(() -> ConversionUtils.validateNotEmpty(null, "test"))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessageContaining("test cannot be null or empty");

    assertThatThrownBy(() -> ConversionUtils.validateNotEmpty("", "test"))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessageContaining("test cannot be null or empty");

    assertThatThrownBy(() -> ConversionUtils.validateNotEmpty("  ", "test"))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessageContaining("test cannot be null or empty");
  }

  @Test
  void shouldValidateNotNull() {
    assertThatThrownBy(() -> ConversionUtils.validateNotNull(null, "test"))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessageContaining("test cannot be null");
  }

  @Test
  void shouldSafelyExecuteOperation() {
    var result = ConversionUtils.safeExecute(() -> Optional.of("success"), "Failed to execute");
    assertThat(result).isEqualTo("success");

    assertThatThrownBy(() ->
      ConversionUtils.safeExecute(() -> Optional.empty(), "Failed to execute")
    )
      .isInstanceOf(IllegalStateException.class)
      .hasMessageContaining("Failed to execute");

    assertThatThrownBy(() ->
      ConversionUtils.safeExecute(
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
