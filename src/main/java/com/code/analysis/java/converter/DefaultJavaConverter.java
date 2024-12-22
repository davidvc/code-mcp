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
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Default implementation of JavaConverter that orchestrates the conversion of Java source code
 * into language-agnostic model classes using specialized converters for each type of declaration.
 */
public class DefaultJavaConverter implements JavaConverter {

  private final JavaClassConverter classConverter;
  private final JavaMethodConverter methodConverter;
  private final JavaDocumentationConverter documentationConverter;

  public DefaultJavaConverter() {
    this.classConverter = new JavaClassConverter();
    this.methodConverter = new JavaMethodConverter();
    this.documentationConverter = new JavaDocumentationConverter();
  }

  @Override
  public CodeUnit convert(CompilationUnit compilationUnit) {
    ModelValidator.validateNotNull(compilationUnit, "CompilationUnit");

    try {
      var definitions = new ArrayList<Definition>();

      // Convert classes and interfaces
      compilationUnit
        .findAll(ClassOrInterfaceDeclaration.class)
        .forEach(declaration -> {
          if (declaration.isInterface()) {
            definitions.add(classConverter.convertInterface(declaration));
          } else {
            definitions.add(classConverter.convertClass(declaration));
            // Add methods and constructors
            declaration
              .getMethods()
              .forEach(method -> definitions.add(methodConverter.convertMethod(method)));
            declaration
              .getConstructors()
              .forEach(constructor ->
                definitions.add(methodConverter.convertConstructor(constructor))
              );
          }
        });

      // Get file-level documentation if any
      var documentation = compilationUnit
        .getAllContainedComments()
        .stream()
        .filter(comment -> comment instanceof JavadocComment)
        .map(comment -> documentationConverter.convertJavadoc((JavadocComment) comment))
        .findFirst()
        .orElse(null);

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
    } catch (Exception e) {
      throw new IllegalStateException(
        "Failed to convert compilation unit: " + e.getMessage(),
        e
      );
    }
  }
}
