package com.code.analysis.core.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Builder;

/**
 * Represents a definition in code, such as a class, method, or variable.
 * This class captures the essential properties of any named construct in code.
 */
@Builder
public record Definition(
  String id,
  String name,
  DefinitionKind kind,
  Scope scope,
  Position position,
  List<Reference> references,
  Map<String, Object> metadata
) {
  public Definition {
    references = Collections.unmodifiableList(
      new ArrayList<>(references != null ? references : Collections.emptyList())
    );
    metadata = Collections.unmodifiableMap(
      new HashMap<>(metadata != null ? metadata : Collections.emptyMap())
    );
  }
}
