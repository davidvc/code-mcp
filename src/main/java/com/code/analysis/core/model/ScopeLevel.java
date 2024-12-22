package com.code.analysis.core.model;

/**
 * Common scope levels across programming languages.
 * This enum represents different levels of scope that can exist in various
 * programming languages,
 * from global scope down to block-level scope.
 */
public enum ScopeLevel {
    /** Global/module level scope */
    GLOBAL,

    /** Package/namespace level scope */
    PACKAGE,

    /** Type (class/interface) level scope */
    TYPE,

    /** Function/method level scope */
    FUNCTION,

    /** Block level scope */
    BLOCK,

    /** Other scope levels */
    OTHER
}
