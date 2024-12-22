package com.code.analysis.core.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Builder;

/**
 * Represents a scope in code, such as a block, method, or class scope.
 * This class captures the level and position information of a scope.
 */
@Builder
public record Scope(
  ScopeLevel level,
  Position start,
  Position end,
  List<Scope> children,
  Map<String, Object> metadata
) {
  public Scope {
    children = Collections.unmodifiableList(
      new ArrayList<>(children != null ? children : Collections.emptyList())
    );
    metadata = Collections.unmodifiableMap(
      new HashMap<>(metadata != null ? metadata : Collections.emptyMap())
    );
  }
}
