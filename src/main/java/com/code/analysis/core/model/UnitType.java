package com.code.analysis.core.model;

/**
 * Types of code organization units.
 * This enum represents different ways code can be organized across various
 * programming languages.
 */
public enum UnitType {
    /** Source code file */
    FILE,

    /** Module (e.g., Python module, Node.js module) */
    MODULE,

    /** Namespace (e.g., Java package, C# namespace) */
    NAMESPACE,

    /** Package (e.g., Java package, NPM package) */
    PACKAGE,

    /** Library or framework */
    LIBRARY,

    /** Other organization unit types */
    OTHER
}
