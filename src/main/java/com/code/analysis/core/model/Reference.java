package com.code.analysis.core.model;

import lombok.NonNull;

public record Reference(
    @NonNull ReferenceKind kind,
    @NonNull String targetName
) {
    // Record automatically provides:
    // - Constructor
    // - Getters (kind(), targetName())
    // - equals(), hashCode(), toString()
}
