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

  public static class ScopeBuilder {

    private List<Scope> children = new ArrayList<>();
    private Map<String, Object> metadata = new HashMap<>();

    public ScopeBuilder addChild(Scope child) {
      this.children.add(child);
      return this;
    }

    public ScopeBuilder addChildren(List<Scope> children) {
      this.children.addAll(children);
      return this;
    }

    public ScopeBuilder addMetadata(String key, Object value) {
      this.metadata.put(key, value);
      return this;
    }
  }
}
