package com.code.analysis.core.model;

import java.util.List;
import java.util.Map;

/**
 * Represents documentation for a code element.
 * This is a language-agnostic abstraction that can represent different
 * kinds of documentation across various programming languages.
 */
public interface Documentation {
    /**
     * Gets the main description text.
     * 
     * @return Description text
     */
    String getDescription();

    /**
     * Gets the documentation format.
     * 
     * @return Documentation format
     */
    DocumentationFormat getFormat();

    /**
     * Gets the position where this documentation appears.
     * 
     * @return Documentation position
     */
    Position getPosition();

    /**
     * Gets all tags/annotations in this documentation.
     * 
     * @return List of documentation tags
     */
    List<DocumentationTag> getTags();

    /**
     * Gets language-specific metadata.
     * 
     * @return Map of metadata key-value pairs
     */
    Map<String, Object> getMetadata();
}
