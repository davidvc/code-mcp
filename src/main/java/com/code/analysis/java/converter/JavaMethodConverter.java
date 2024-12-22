package com.code.analysis.java.converter;

import com.code.analysis.core.model.Definition;
import com.code.analysis.core.model.DefinitionKind;
import com.code.analysis.core.model.ModelValidator;
import com.code.analysis.core.model.Position;
import com.code.analysis.core.model.Scope;
import com.code.analysis.core.model.ScopeLevel;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Converts Java method and constructor declarations into language-agnostic definitions.
 */
public class JavaMethodConverter {

  /**
   * Creates a scope from a JavaParser node.
   */
  private static Scope createScopeFromNode(Node node, boolean isPublic, boolean isPrivate) {
    var begin = node.getBegin().orElseThrow();
    var end = node.getEnd().orElseThrow();

    return Scope.builder()
      .level(
        isPublic ? ScopeLevel.GLOBAL : isPrivate ? ScopeLevel.TYPE : ScopeLevel.PACKAGE
      )
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

  public Definition convertMethod(MethodDeclaration declaration) {
    ModelValidator.validateNotNull(declaration, "Method declaration");
    var scope = createScopeFromNode(
      declaration,
      declaration.isPublic(),
      declaration.isPrivate()
    );

    Map<String, Object> metadata = new HashMap<>();
    metadata.put("returnType", declaration.getType().asString());
    metadata.put(
      "parameters",
      declaration.getParameters().stream().map(p -> p.getNameAsString()).collect(Collectors.toList())
    );
    metadata.put("isStatic", declaration.isStatic());

    return Definition.builder()
      .id(UUID.randomUUID().toString())
      .name(declaration.getNameAsString())
      .kind(DefinitionKind.FUNCTION)
      .scope(scope)
      .position(createPositionFromNode(declaration))
      .metadata(metadata)
      .build();
  }

  public Definition convertConstructor(ConstructorDeclaration declaration) {
    ModelValidator.validateNotNull(declaration, "Constructor declaration");
    var scope = createScopeFromNode(
      declaration,
      declaration.isPublic(),
      declaration.isPrivate()
    );

    Map<String, Object> metadata = new HashMap<>();
    metadata.put("isConstructor", true);
    metadata.put(
      "parameters",
      declaration.getParameters().stream().map(p -> p.getNameAsString()).collect(Collectors.toList())
    );

    return Definition.builder()
      .id(UUID.randomUUID().toString())
      .name(declaration.getNameAsString())
      .kind(DefinitionKind.FUNCTION)
      .scope(scope)
      .position(createPositionFromNode(declaration))
      .metadata(metadata)
      .build();
  }
}
