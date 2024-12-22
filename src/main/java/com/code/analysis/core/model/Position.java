package com.code.analysis.core.model;

import lombok.Builder;

/**
 * Represents a position in source code.
 * This class captures line, column, and offset information.
 */
@Builder
public record Position(int line, int column, int offset) {
  public Position {
    offset = Math.max(0, offset); // Default to 0 if negative
  }
}
