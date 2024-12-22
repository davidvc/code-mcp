package com.code.analysis.core.model;

public enum DefinitionKind {
  TYPE, // Class, struct, etc.
  INTERFACE, // Interface, protocol, etc.
  ENUM, // Enumeration type
  FUNCTION, // Method, function, procedure
  VARIABLE, // Field, variable, constant
  MODULE, // Package, module, namespace
  PROPERTY, // Property, getter/setter
  PARAMETER, // Function/method parameter
  OTHER, // Other definition types
}
