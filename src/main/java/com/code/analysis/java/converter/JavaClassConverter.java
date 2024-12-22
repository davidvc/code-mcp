package com.code.analysis.java.converter;

import com.code.analysis.core.model.Definition;
import com.code.analysis.core.model.DefinitionKind;
import com.code.analysis.core.model.ModelValidator;
import com.code.analysis.core.model.Position;
import com.code.analysis.core.model.Scope;
import com.code.analysis.core.model.ScopeLevel;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.EnumDeclaration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Converts Java class and interface declarations into language-agnostic definitions.
 */
public class JavaClassConverter {

  /**
   * Creates a scope from a JavaParser node.
   */
  private static Scope createScopeFromNode(Node node, boolean isPublic) {
    var begin = node.getBegin().orElseThrow();
    var end = node.getEnd().orElseThrow();

    return Scope.builder()
      .level(isPublic ? ScopeLevel.GLOBAL : ScopeLevel.PACKAGE)
      .start(Position.builder().line(begin.line).column(begin.column).build())
      .end(Position.builder().line(end.line).column(end.column).build())
      .build();
  }

  /**
   * Creates a position from a JavaParser node.
   */
  private static Position createPositionFromNode(Node node) {
    var begin = node.getBegin().orElseThrow();
    return Position.builder().line(begin.line).column(begin.column).build();
  }

  public Definition convertClass(ClassOrInterfaceDeclaration declaration) {
    ModelValidator.validateNotNull(declaration, "Class declaration");
    var scope = createScopeFromNode(declaration, declaration.isPublic());

    Map<String, Object> metadata = new HashMap<>();
    metadata.put("isAbstract", declaration.isAbstract());
    metadata.put(
      "superclass",
      declaration
        .getExtendedTypes()
        .stream()
        .map(type -> type.getNameAsString())
        .findFirst()
        .orElse(null)
    );
    metadata.put(
      "interfaces",
      declaration
        .getImplementedTypes()
        .stream()
        .map(type -> type.getNameAsString())
        .collect(Collectors.toList())
    );

    return Definition.builder()
      .id(UUID.randomUUID().toString())
      .name(declaration.getNameAsString())
      .kind(DefinitionKind.TYPE)
      .scope(scope)
      .position(createPositionFromNode(declaration))
      .metadata(metadata)
      .build();
  }

  public Definition convertInterface(ClassOrInterfaceDeclaration declaration) {
    ModelValidator.validateNotNull(declaration, "Interface declaration");
    var scope = createScopeFromNode(declaration, declaration.isPublic());

    Map<String, Object> metadata = new HashMap<>();
    metadata.put(
      "superInterfaces",
      declaration
        .getExtendedTypes()
        .stream()
        .map(type -> type.getNameAsString())
        .collect(Collectors.toList())
    );

    return Definition.builder()
      .id(UUID.randomUUID().toString())
      .name(declaration.getNameAsString())
      .kind(DefinitionKind.INTERFACE)
      .scope(scope)
      .position(createPositionFromNode(declaration))
      .metadata(metadata)
      .build();
  }

  public Definition convertEnum(EnumDeclaration declaration) {
    ModelValidator.validateNotNull(declaration, "Enum declaration");
    var scope = createScopeFromNode(declaration, declaration.isPublic());

    Map<String, Object> metadata = new HashMap<>();
    metadata.put(
      "constants",
      declaration
        .getEntries()
        .stream()
        .map(entry -> entry.getNameAsString())
        .collect(Collectors.toList())
    );

    return Definition.builder()
      .id(UUID.randomUUID().toString())
      .name(declaration.getNameAsString())
      .kind(DefinitionKind.ENUM)
      .scope(scope)
      .position(createPositionFromNode(declaration))
      .metadata(metadata)
      .build();
  }
}
