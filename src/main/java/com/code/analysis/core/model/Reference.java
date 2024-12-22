package com.code.analysis.core.model;

import java.util.Map;

/**
 * Represents a reference to a definition in code.
 * This is a language-agnostic abstraction that can represent different
 * kinds of references across various programming languages.
 */
public interface Reference {
    /**
     * Gets the definition being referenced.
     * 
     * @return Referenced definition
     */
    Definition getTarget();

    /**
     * Gets the kind of reference.
     * 
     * @return Reference kind
     */
    ReferenceKind getKind();

    /**
     * Gets the position where this reference occurs.
     * 
     * @return Reference position
     */
    Position getPosition();

    /**
     * Gets the scope containing this reference.
     * 
     * @return Containing scope
     */
    Scope getScope();

    /**
     * Gets language-specific metadata about this reference.
     * 
     * @return Map of metadata key-value pairs
     */
    Map<String, Object> getMetadata();
}
