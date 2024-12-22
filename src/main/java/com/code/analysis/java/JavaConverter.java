package com.code.analysis.java;

import com.code.analysis.core.ConversionUtils;
import com.code.analysis.core.model.CodeUnit;
import com.code.analysis.core.model.Definition;
import com.code.analysis.core.model.DefinitionKind;
import com.code.analysis.core.model.Documentation;
import com.code.analysis.core.model.DocumentationFormat;
import com.code.analysis.core.model.Position;
import com.code.analysis.core.model.Scope;
import com.code.analysis.core.model.ScopeLevel;
import com.code.analysis.core.model.UnitType;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.EnumDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.comments.JavadocComment;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Converts Java source code into language-agnostic model classes.
 * This converter uses JavaParser to parse Java source files and converts the
 * AST
 * into our generic code analysis model.
 *
 * <p>
 * The converter handles:
 * <ul>
 * <li>Classes and interfaces
 * <li>Methods and constructors
 * <li>Enums
 * <li>Documentation comments
 * </ul>
 *
 * <p>
 * Thread Safety: This class is thread-safe and can be shared between threads.
 */
class JavaConverter {

  private final ClassConverter classConverter;
  private final MethodConverter methodConverter;
  private final DocumentationConverter documentationConverter;

  JavaConverter() {
    this.classConverter = new ClassConverter();
    this.methodConverter = new MethodConverter();
    this.documentationConverter = new DocumentationConverter();
  }

  /**
   * Converts a JavaParser CompilationUnit to our model.
   *
   * @param cu The compilation unit to convert
   * @return A CodeUnit representing the file and its contents
   * @throws IllegalArgumentException if cu is null
   * @throws IllegalStateException    if conversion fails
   */
  CodeUnit convert(CompilationUnit cu) {
    ConversionUtils.validateNotNull(cu, "CompilationUnit");

    try {
      var definitions = new ArrayList<Definition>();

      // Convert classes and interfaces
      cu
        .findAll(ClassOrInterfaceDeclaration.class)
        .forEach(decl -> {
          if (decl.isInterface()) {
            definitions.add(classConverter.convertInterface(decl));
          } else {
            definitions.add(classConverter.convertClass(decl));
            // Add methods and constructors
            decl
              .getMethods()
              .forEach(method -> definitions.add(methodConverter.convertMethod(method)));
            decl
              .getConstructors()
              .forEach(ctor -> definitions.add(methodConverter.convertConstructor(ctor)));
          }
        });

      // Convert enums
      cu
        .findAll(EnumDeclaration.class)
        .forEach(decl -> definitions.add(classConverter.convertEnum(decl)));

      // Get file-level documentation if any
      var documentation = cu
        .getAllContainedComments()
        .stream()
        .filter(comment -> comment instanceof JavadocComment)
        .map(comment -> documentationConverter.convertJavadoc((JavadocComment) comment))
        .findFirst()
        .orElse(null);

      return CodeUnit.builder()
        .id(UUID.randomUUID().toString())
        .name(cu.getStorage().map(storage -> storage.getFileName()).orElse("unknown"))
        .type(UnitType.FILE)
        .addMetadata(
          "packageName",
          cu.getPackageDeclaration().map(pkg -> pkg.getNameAsString()).orElse("")
        )
        .addMetadata(
          "imports",
          cu.getImports().stream().map(imp -> imp.getNameAsString()).collect(Collectors.toList())
        )
        .addDefinitions(definitions)
        .documentation(documentation)
        .build();
    } catch (Exception e) {
      throw new IllegalStateException("Failed to convert compilation unit: " + e.getMessage(), e);
    }
  }

  /**
   * Creates a scope from a JavaParser node.
   */
  private static Scope createScopeFromNode(Node node, boolean isPublic) {
    var begin = node.getBegin().orElseThrow();
    var end = node.getEnd().orElseThrow();

    return ConversionUtils.createScope(
      isPublic ? ScopeLevel.GLOBAL : ScopeLevel.PACKAGE,
      begin.line,
      begin.column,
      end.line,
      end.column
    );
  }

  /**
   * Creates a position from a JavaParser node.
   */
  private static Position createPositionFromNode(Node node) {
    var begin = node.getBegin().orElseThrow();
    return ConversionUtils.createPosition(begin.line, begin.column);
  }

  /**
   * Handles conversion of class and interface declarations.
   */
  private static class ClassConverter {

    Definition convertClass(ClassOrInterfaceDeclaration decl) {
      ConversionUtils.validateNotNull(decl, "Class declaration");
      var scope = createScopeFromNode(decl, decl.isPublic());

      return Definition.builder()
        .id(UUID.randomUUID().toString())
        .name(decl.getNameAsString())
        .kind(DefinitionKind.TYPE)
        .scope(scope)
        .position(createPositionFromNode(decl))
        .addMetadata("isAbstract", decl.isAbstract())
        .addMetadata(
          "superclass",
          decl
            .getExtendedTypes()
            .stream()
            .map(type -> type.getNameAsString())
            .findFirst()
            .orElse(null)
        )
        .addMetadata(
          "interfaces",
          decl
            .getImplementedTypes()
            .stream()
            .map(type -> type.getNameAsString())
            .collect(Collectors.toList())
        )
        .build();
    }

    Definition convertInterface(ClassOrInterfaceDeclaration decl) {
      ConversionUtils.validateNotNull(decl, "Interface declaration");
      var scope = createScopeFromNode(decl, decl.isPublic());

      return Definition.builder()
        .id(UUID.randomUUID().toString())
        .name(decl.getNameAsString())
        .kind(DefinitionKind.INTERFACE)
        .scope(scope)
        .position(createPositionFromNode(decl))
        .addMetadata(
          "superInterfaces",
          decl
            .getExtendedTypes()
            .stream()
            .map(type -> type.getNameAsString())
            .collect(Collectors.toList())
        )
        .build();
    }

    Definition convertEnum(EnumDeclaration decl) {
      ConversionUtils.validateNotNull(decl, "Enum declaration");
      var scope = createScopeFromNode(decl, decl.isPublic());

      return Definition.builder()
        .id(UUID.randomUUID().toString())
        .name(decl.getNameAsString())
        .kind(DefinitionKind.ENUM)
        .scope(scope)
        .position(createPositionFromNode(decl))
        .addMetadata(
          "constants",
          decl
            .getEntries()
            .stream()
            .map(entry -> entry.getNameAsString())
            .collect(Collectors.toList())
        )
        .build();
    }
  }

  /**
   * Handles conversion of methods and constructors.
   */
  private static class MethodConverter {

    Definition convertMethod(MethodDeclaration decl) {
      ConversionUtils.validateNotNull(decl, "Method declaration");
      var scope = createScopeFromNode(decl, decl.isPublic());

      return Definition.builder()
        .id(UUID.randomUUID().toString())
        .name(decl.getNameAsString())
        .kind(DefinitionKind.FUNCTION)
        .scope(scope)
        .position(createPositionFromNode(decl))
        .addMetadata("returnType", decl.getType().asString())
        .addMetadata(
          "parameters",
          decl.getParameters().stream().map(p -> p.getNameAsString()).collect(Collectors.toList())
        )
        .build();
    }

    Definition convertConstructor(ConstructorDeclaration decl) {
      ConversionUtils.validateNotNull(decl, "Constructor declaration");
      var scope = createScopeFromNode(decl, decl.isPublic());

      return Definition.builder()
        .id(UUID.randomUUID().toString())
        .name(decl.getNameAsString())
        .kind(DefinitionKind.FUNCTION)
        .scope(scope)
        .position(createPositionFromNode(decl))
        .addMetadata("isConstructor", true)
        .addMetadata(
          "parameters",
          decl.getParameters().stream().map(p -> p.getNameAsString()).collect(Collectors.toList())
        )
        .build();
    }
  }

  /**
   * Handles conversion of documentation comments.
   */
  private static class DocumentationConverter {

    Documentation convertJavadoc(JavadocComment comment) {
      ConversionUtils.validateNotNull(comment, "Javadoc comment");
      var begin = comment.getBegin().orElseThrow();
      var javadoc = comment.parse();

      return Documentation.builder()
        .id(UUID.randomUUID().toString())
        .description(javadoc.getDescription().toText())
        .format(DocumentationFormat.JAVADOC)
        .position(ConversionUtils.createPosition(begin.line, begin.column))
        .build();
    }
  }
}
