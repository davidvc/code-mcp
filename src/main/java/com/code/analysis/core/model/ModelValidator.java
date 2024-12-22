package com.code.analysis.core.model;

/**
 * Provides validation methods for model classes.
 */
public final class ModelValidator {

  private ModelValidator() {
    // Prevent instantiation
  }

  /**
   * Validates that a string is not null or empty.
   *
   * @param value     The string to check
   * @param fieldName Name of the field being validated
   * @throws IllegalArgumentException if the string is null or empty
   */
  public static void validateNotEmpty(String value, String fieldName) {
    if (value == null || value.trim().isEmpty()) {
      throw new IllegalArgumentException(fieldName + " cannot be null or empty");
    }
  }

  /**
   * Validates that an object is not null.
   *
   * @param value     The object to check
   * @param fieldName Name of the field being validated
   * @throws IllegalArgumentException if the object is null
   */
  public static void validateNotNull(Object value, String fieldName) {
    if (value == null) {
      throw new IllegalArgumentException(fieldName + " cannot be null");
    }
  }

  /**
   * Determines if a string represents a valid identifier.
   * This is useful for validating names across different languages.
   *
   * @param name The string to check
   * @return true if the string is a valid identifier
   */
  public static boolean isValidIdentifier(String name) {
    if (name == null || name.isEmpty()) {
      return false;
    }

    // First character must be a letter or underscore
    if (!Character.isLetter(name.charAt(0)) && name.charAt(0) != '_') {
      return false;
    }

    // Remaining characters must be letters, digits, or underscores
    for (int i = 1; i < name.length(); i++) {
      char c = name.charAt(i);
      if (!Character.isLetterOrDigit(c) && c != '_') {
        return false;
      }
    }

    return true;
  }
}
