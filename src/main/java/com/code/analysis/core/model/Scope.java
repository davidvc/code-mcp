package com.code.analysis.core.model;

import java.util.List;
import java.util.Map;

/**
 * Represents the scope and accessibility of a definition.
 * This is a language-agnostic abstraction that can represent different
 * scoping rules across various programming languages.
 */
public interface Scope {
    /**
     * Gets the scope level.
     * 
     * @return Scope level
     */
    ScopeLevel getLevel();

    /**
     * Gets the containing scope, if any.
     * 
     * @return Parent scope or empty if this is a top-level scope
     */
    Scope getParent();

    /**
     * Gets child scopes contained within this scope.
     * 
     * @return List of child scopes
     */
    List<Scope> getChildren();

    /**
     * Gets the start position of this scope in the source code.
     * 
     * @return Start position
     */
    Position getStart();

    /**
     * Gets the end position of this scope in the source code.
     * 
     * @return End position
     */
    Position getEnd();

    /**
     * Gets language-specific metadata about this scope.
     * 
     * @return Map of metadata key-value pairs
     */
    Map<String, Object> getMetadata();
}
