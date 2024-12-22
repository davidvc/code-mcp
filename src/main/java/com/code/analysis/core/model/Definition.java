package com.code.analysis.core.model;

import java.util.List;
import java.util.Map;

/**
 * Represents a code definition (function, type, variable, etc.).
 * This is a language-agnostic abstraction that can represent different
 * kinds of definitions across various programming languages.
 */
public interface Definition {
    /**
     * Gets the unique identifier for this definition.
     * 
     * @return Unique identifier (e.g., fully qualified name)
     */
    String getId();

    /**
     * Gets the simple name of this definition.
     * 
     * @return Simple name
     */
    String getName();

    /**
     * Gets the kind of definition.
     * 
     * @return Definition kind
     */
    DefinitionKind getKind();

    /**
     * Gets the scope of this definition.
     * 
     * @return Scope information
     */
    Scope getScope();

    /**
     * Gets all references to this definition.
     * 
     * @return List of references
     */
    List<Reference> getReferences();

    /**
     * Gets documentation associated with this definition.
     * 
     * @return Documentation or empty if none
     */
    Documentation getDocumentation();

    /**
     * Gets the source position of this definition.
     * 
     * @return Source position
     */
    Position getPosition();

    /**
     * Gets language-specific metadata.
     * 
     * @return Map of metadata key-value pairs
     */
    Map<String, Object> getMetadata();
}
