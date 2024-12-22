package com.code.analysis.java;

import com.code.analysis.core.model.Definition;
import com.code.analysis.core.model.DefinitionKind;
import com.code.analysis.core.model.Position;
import com.code.analysis.core.model.Scope;
import com.code.analysis.core.model.ScopeLevel;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

class JavaDefinitionExtractor {

  List<Definition> extract(CompilationUnit cu) {
    var definitions = new ArrayList<Definition>();

    cu
      .findAll(ClassOrInterfaceDeclaration.class)
      .forEach(classDecl -> {
        definitions.add(extractClassDefinition(classDecl));
        classDecl.getMethods().forEach(method -> definitions.add(extractMethodDefinition(method)));
        classDecl.getFields().forEach(field -> definitions.add(extractFieldDefinition(field)));
      });

    return definitions;
  }

  private Definition extractClassDefinition(ClassOrInterfaceDeclaration classDecl) {
    var begin = classDecl.getBegin().orElseThrow();
    var end = classDecl.getEnd().orElseThrow();

    var scope = Scope.builder()
      .level(classDecl.isPublic() ? ScopeLevel.GLOBAL : ScopeLevel.PACKAGE)
      .start(Position.builder().line(begin.line).column(begin.column).build())
      .end(Position.builder().line(end.line).column(end.column).build())
      .build();

    Map<String, Object> metadata = new HashMap<>();
    metadata.put("isAbstract", classDecl.isAbstract());
    metadata.put(
      "superclass",
      classDecl
        .getExtendedTypes()
        .stream()
        .map(type -> type.getNameAsString())
        .findFirst()
        .orElse(null)
    );

    return Definition.builder()
      .id(UUID.randomUUID().toString())
      .name(classDecl.getNameAsString())
      .kind(classDecl.isInterface() ? DefinitionKind.INTERFACE : DefinitionKind.TYPE)
      .scope(scope)
      .position(Position.builder().line(begin.line).column(begin.column).build())
      .metadata(metadata)
      .build();
  }

  private Definition extractMethodDefinition(MethodDeclaration methodDecl) {
    var begin = methodDecl.getBegin().orElseThrow();
    var end = methodDecl.getEnd().orElseThrow();

    var scope = Scope.builder()
      .level(
        methodDecl.isPublic()
          ? ScopeLevel.GLOBAL
          : methodDecl.isPrivate() ? ScopeLevel.TYPE : ScopeLevel.PACKAGE
      )
      .start(Position.builder().line(begin.line).column(begin.column).build())
      .end(Position.builder().line(end.line).column(end.column).build())
      .build();

    Map<String, Object> metadata = new HashMap<>();
    metadata.put("returnType", methodDecl.getTypeAsString());
    metadata.put("isStatic", methodDecl.isStatic());

    return Definition.builder()
      .id(UUID.randomUUID().toString())
      .name(methodDecl.getNameAsString())
      .kind(DefinitionKind.FUNCTION)
      .scope(scope)
      .position(Position.builder().line(begin.line).column(begin.column).build())
      .metadata(metadata)
      .build();
  }

  private Definition extractFieldDefinition(FieldDeclaration fieldDecl) {
    var begin = fieldDecl.getBegin().orElseThrow();
    var end = fieldDecl.getEnd().orElseThrow();

    var scope = Scope.builder()
      .level(
        fieldDecl.isPublic()
          ? ScopeLevel.GLOBAL
          : fieldDecl.isPrivate() ? ScopeLevel.TYPE : ScopeLevel.PACKAGE
      )
      .start(Position.builder().line(begin.line).column(begin.column).build())
      .end(Position.builder().line(end.line).column(end.column).build())
      .build();

    Map<String, Object> metadata = new HashMap<>();
    metadata.put("type", fieldDecl.getElementType().asString());
    metadata.put("isStatic", fieldDecl.isStatic());
    metadata.put("isFinal", fieldDecl.isFinal());

    return Definition.builder()
      .id(UUID.randomUUID().toString())
      .name(fieldDecl.getVariables().get(0).getNameAsString())
      .kind(DefinitionKind.VARIABLE)
      .scope(scope)
      .position(Position.builder().line(begin.line).column(begin.column).build())
      .metadata(metadata)
      .build();
  }
}
