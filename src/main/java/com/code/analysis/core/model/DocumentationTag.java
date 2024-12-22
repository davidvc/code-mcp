package com.code.analysis.core.model;

import java.util.Map;

/**
 * Represents a documentation tag/annotation.
 * This record provides a language-agnostic way to represent documentation tags
 * like @param, @return, etc. across various programming languages.
 */
public record DocumentationTag(
        /** Tag name (e.g., @param, @return) */
        String name,

        /** Tag value */
        String value,

        /** Additional tag attributes */
        Map<String, String> attributes) {
    /**
     * Creates a new DocumentationTag instance.
     * 
     * @param name       Tag name (e.g., @param, @return)
     * @param value      Tag value
     * @param attributes Additional tag attributes
     */
    public DocumentationTag {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Tag name cannot be null or blank");
        }
        if (value == null) {
            value = "";
        }
        if (attributes == null) {
            throw new IllegalArgumentException("Attributes map cannot be null");
        }
    }

    /**
     * Creates a simple tag with just a name and value.
     * 
     * @param name  Tag name
     * @param value Tag value
     * @return New DocumentationTag instance
     */
    public static DocumentationTag simple(String name, String value) {
        return new DocumentationTag(name, value, Map.of());
    }

    /**
     * Creates a tag with a name but no value.
     * 
     * @param name Tag name
     * @return New DocumentationTag instance
     */
    public static DocumentationTag marker(String name) {
        return new DocumentationTag(name, "", Map.of());
    }
}
