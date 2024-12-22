package com.code.analysis.core.model;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import lombok.Builder;

/**
 * Represents a reference to another code element.
 * This class captures relationships between different parts of code.
 */
@Builder
public record Reference(
  String id,
  String name,
  ReferenceKind kind,
  Position position,
  Map<String, Object> metadata
) {
  public Reference {
    metadata = Collections.unmodifiableMap(
      new HashMap<>(metadata != null ? metadata : Collections.emptyMap())
    );
  }

  public static class ReferenceBuilder {

    private Map<String, Object> metadata = new HashMap<>();

    public ReferenceBuilder addMetadata(String key, Object value) {
      this.metadata.put(key, value);
      return this;
    }
  }
}
