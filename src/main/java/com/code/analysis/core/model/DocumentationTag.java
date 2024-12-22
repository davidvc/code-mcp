package com.code.analysis.core.model;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import lombok.Builder;

/**
 * Represents a documentation tag, such as @param or @return.
 * This class captures structured documentation elements.
 */
@Builder
public record DocumentationTag(String id, String name, String value, Map<String, Object> metadata) {
  public DocumentationTag {
    metadata = Collections.unmodifiableMap(
      new HashMap<>(metadata != null ? metadata : Collections.emptyMap())
    );
  }
}
