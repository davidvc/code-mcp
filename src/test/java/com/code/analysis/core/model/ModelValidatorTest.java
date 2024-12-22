package com.code.analysis.core.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class ModelValidatorTest {

  @Test
  void shouldValidateIdentifiers() {
    assertThat(ModelValidator.isValidIdentifier("validName")).isTrue();
    assertThat(ModelValidator.isValidIdentifier("valid_name")).isTrue();
    assertThat(ModelValidator.isValidIdentifier("_validName")).isTrue();
    assertThat(ModelValidator.isValidIdentifier("ValidName123")).isTrue();

    assertThat(ModelValidator.isValidIdentifier("")).isFalse();
    assertThat(ModelValidator.isValidIdentifier(null)).isFalse();
    assertThat(ModelValidator.isValidIdentifier("123invalid")).isFalse();
    assertThat(ModelValidator.isValidIdentifier("invalid-name")).isFalse();
    assertThat(ModelValidator.isValidIdentifier("invalid name")).isFalse();
  }

  @Test
  void shouldValidateNotEmpty() {
    assertThatThrownBy(() -> ModelValidator.validateNotEmpty(null, "test"))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessageContaining("test cannot be null or empty");

    assertThatThrownBy(() -> ModelValidator.validateNotEmpty("", "test"))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessageContaining("test cannot be null or empty");

    assertThatThrownBy(() -> ModelValidator.validateNotEmpty("  ", "test"))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessageContaining("test cannot be null or empty");
  }

  @Test
  void shouldValidateNotNull() {
    assertThatThrownBy(() -> ModelValidator.validateNotNull(null, "test"))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessageContaining("test cannot be null");
  }
}
