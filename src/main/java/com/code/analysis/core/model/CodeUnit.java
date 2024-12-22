package com.code.analysis.core.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Builder;

/**
 * Represents a unit of code, such as a file or module.
 * This is the top-level model class that contains definitions and dependencies.
 */
@Builder
public record CodeUnit(
  String id,
  String name,
  UnitType type,
  List<Definition> definitions,
  List<CodeUnit> dependencies,
  Documentation documentation,
  Map<String, Object> metadata
) {
  public CodeUnit {
    definitions = Collections.unmodifiableList(
      new ArrayList<>(definitions != null ? definitions : Collections.emptyList())
    );
    dependencies = Collections.unmodifiableList(
      new ArrayList<>(dependencies != null ? dependencies : Collections.emptyList())
    );
    metadata = Collections.unmodifiableMap(
      new HashMap<>(metadata != null ? metadata : Collections.emptyMap())
    );
  }

  public static class CodeUnitBuilder {

    private List<Definition> definitions = new ArrayList<>();
    private List<CodeUnit> dependencies = new ArrayList<>();
    private Map<String, Object> metadata = new HashMap<>();

    public CodeUnitBuilder addDefinition(Definition definition) {
      this.definitions.add(definition);
      return this;
    }

    public CodeUnitBuilder addDefinitions(List<Definition> definitions) {
      this.definitions.addAll(definitions);
      return this;
    }

    public CodeUnitBuilder addDependency(CodeUnit dependency) {
      this.dependencies.add(dependency);
      return this;
    }

    public CodeUnitBuilder addDependencies(List<CodeUnit> dependencies) {
      this.dependencies.addAll(dependencies);
      return this;
    }

    public CodeUnitBuilder addMetadata(String key, Object value) {
      this.metadata.put(key, value);
      return this;
    }
  }
}
