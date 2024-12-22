package com.code.analysis.core.model;

/**
 * Kinds of code definitions.
 * This enum represents different types of definitions that can exist across
 * various programming languages.
 */
public enum DefinitionKind {
    /** Function or method */
    FUNCTION,

    /** Type definition (class, interface, enum, etc.) */
    TYPE,

    /** Variable or constant */
    VARIABLE,

    /** Module or namespace */
    MODULE,

    /** Property or field */
    PROPERTY,

    /** Parameter */
    PARAMETER,

    /** Other definition kinds */
    OTHER
}
