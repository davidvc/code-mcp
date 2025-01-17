package com.code.analysis.core.model;

import lombok.Data;
import lombok.NonNull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class Definition {
    private final @NonNull String name;
    private final @NonNull DefinitionKind kind;
    private final Map<String, Object> metadata;
    private final List<Reference> references;

    public Definition(@NonNull String name, @NonNull DefinitionKind kind, Map<String, Object> metadata) {
        this.name = name;
        this.kind = kind;
        this.metadata = new HashMap<>(metadata);
        this.references = new ArrayList<>();
    }

    public Map<String, Object> metadata() {
        return Collections.unmodifiableMap(metadata);
    }

    public List<Reference> references() {
        return Collections.unmodifiableList(references);
    }

    public void addReference(@NonNull Reference reference) {
        references.add(reference);
    }
}
