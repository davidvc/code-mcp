package com.code.analysis.core.model;

/**
 * Common kinds of references across programming languages.
 * This enum represents different ways one piece of code can reference another,
 * providing a language-agnostic way to classify relationships between code
 * elements.
 */
public enum ReferenceKind {
  /** Direct usage/call of a definition */
  USE,

  /** Modification of a definition */
  MODIFY,

  /** Extension/inheritance of a definition */
  EXTEND,

  /** Implementation of a definition */
  IMPLEMENT,

  /** Import/include of a definition */
  IMPORT,

  /** Other kinds of references */
  OTHER,
}
