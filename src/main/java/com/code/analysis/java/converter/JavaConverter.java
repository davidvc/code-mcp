package com.code.analysis.java.converter;

import com.code.analysis.core.model.CodeUnit;
import com.code.analysis.core.model.Definition;
import com.code.analysis.core.model.Documentation;
import com.code.analysis.core.model.ModelValidator;
import com.code.analysis.core.model.UnitType;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.comments.JavadocComment;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Converts Java source code into language-agnostic model classes using specialized converters
 * for each type of declaration.
 */
public class JavaConverter {

  private final JavaClassConverter classConverter;
  private final JavaMethodConverter methodConverter;
  private final JavaDocumentationConverter documentationConverter;

  public JavaConverter() {
    this.classConverter = new JavaClassConverter();
    this.methodConverter = new JavaMethodConverter();
    this.documentationConverter = new JavaDocumentationConverter();
  }

  /**
   * Converts a Java compilation unit into a language-agnostic code unit model.
   * This method processes the entire compilation unit, including:
   * - Classes and interfaces with their methods and constructors
   * - File-level documentation (Javadoc comments)
   * - Package and import information
   *
   * @param compilationUnit The Java compilation unit to convert
   * @return A CodeUnit containing the converted definitions, documentation, and metadata
   * @throws IllegalStateException if the conversion fails
   * @throws IllegalArgumentException if compilationUnit is null
   */
  public CodeUnit convert(final CompilationUnit compilationUnit) {
    ModelValidator.validateNotNull(compilationUnit, "CompilationUnit");

    try {
      List<Definition> definitions = convertDefinitions(compilationUnit);
      Documentation documentation = extractFileDocumentation(compilationUnit);
      Map<String, Object> metadata = buildFileMetadata(compilationUnit);

      return buildCodeUnit(compilationUnit, definitions, documentation, metadata);
    } catch (Exception e) {
      throw new IllegalStateException(
        "Failed to convert compilation unit: " + e.getMessage(),
        e
      );
    }
  }

  private List<Definition> convertDefinitions(final CompilationUnit compilationUnit) {
    List<Definition> definitions = new ArrayList<>();
    compilationUnit
      .findAll(ClassOrInterfaceDeclaration.class)
      .forEach(declaration -> {
        if (declaration.isInterface()) {
          definitions.add(classConverter.convertInterface(declaration));
        } else {
          definitions.add(classConverter.convertClass(declaration));
          convertClassMembers(declaration, definitions);
        }
      });
    return definitions;
  }

  private void convertClassMembers(
    final ClassOrInterfaceDeclaration declaration,
    final List<Definition> definitions
  ) {
    declaration
      .getMethods()
      .forEach(method -> definitions.add(methodConverter.convertMethod(method)));
    declaration
      .getConstructors()
      .forEach(constructor ->
        definitions.add(methodConverter.convertConstructor(constructor))
      );
  }

  private Documentation extractFileDocumentation(final CompilationUnit compilationUnit) {
    return compilationUnit
      .getAllContainedComments()
      .stream()
      .filter(comment -> comment instanceof JavadocComment)
      .map(comment -> documentationConverter.convertJavadoc((JavadocComment) comment))
      .findFirst()
      .orElse(null);
  }

  private Map<String, Object> buildFileMetadata(final CompilationUnit compilationUnit) {
    Map<String, Object> metadata = new HashMap<>();
    metadata.put(
      "packageName",
      compilationUnit.getPackageDeclaration().map(pkg -> pkg.getNameAsString()).orElse("")
    );
    metadata.put(
      "imports",
      compilationUnit
        .getImports()
        .stream()
        .map(imp -> imp.getNameAsString())
        .collect(Collectors.toList())
    );
    return metadata;
  }

  private CodeUnit buildCodeUnit(
    final CompilationUnit compilationUnit,
    final List<Definition> definitions,
    final Documentation documentation,
    final Map<String, Object> metadata
  ) {
    return CodeUnit.builder()
      .id(UUID.randomUUID().toString())
      .name(
        compilationUnit.getStorage().map(storage -> storage.getFileName()).orElse("unknown")
      )
      .type(UnitType.FILE)
      .metadata(metadata)
      .definitions(definitions)
      .documentation(documentation)
      .build();
  }
}
