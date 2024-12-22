package com.code.analysis.java.converter;

import com.code.analysis.core.model.CodeUnit;
import com.github.javaparser.ast.CompilationUnit;

/**
 * Interface for converting Java compilation units into language-agnostic code units.
 */
public interface JavaConverter {
  /**
   * Converts a Java compilation unit into a language-agnostic code unit.
   *
   * @param compilationUnit The compilation unit to convert
   * @return A CodeUnit representing the file and its contents
   * @throws IllegalArgumentException if compilationUnit is null
   */
  CodeUnit convert(CompilationUnit compilationUnit);
}
