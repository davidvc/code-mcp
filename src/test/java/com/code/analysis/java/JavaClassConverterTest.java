package com.code.analysis.java;

import static org.assertj.core.api.Assertions.assertThat;

import com.code.analysis.core.model.Definition;
import com.code.analysis.core.model.DefinitionKind;
import com.code.analysis.core.model.Reference;
import com.code.analysis.core.model.ReferenceKind;
import com.code.analysis.java.converter.JavaClassConverter;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import java.util.List;
import org.junit.jupiter.api.Test;

class JavaClassConverterTest {

  private final JavaClassConverter converter = new JavaClassConverter();

  @Test
  void shouldConvertSimpleClass() {
    // Given
    var cu = new CompilationUnit();
    var classDecl = cu.addClass("Example")
      .setPublic(true);

    // When
    Definition classDef = converter.convert(classDecl);

    // Then
    assertThat(classDef.kind()).isEqualTo(DefinitionKind.TYPE);
    assertThat(classDef.name()).isEqualTo("Example");
    assertThat(classDef.metadata())
      .containsEntry("visibility", "public")
      .containsEntry("isAbstract", false);
  }

  @Test
  void shouldConvertClassWithSuperclass() {
    // Given
    var cu = new CompilationUnit();
    var classDecl = cu.addClass("Example")
      .setPublic(true)
      .addExtendedType("BaseClass");

    // When
    Definition classDef = converter.convert(classDecl);

    // Then
    assertThat(classDef.references()).hasSize(1);
    Reference superRef = classDef.references().get(0);
    assertThat(superRef.kind()).isEqualTo(ReferenceKind.EXTEND);
    assertThat(superRef.target().name()).isEqualTo("BaseClass");
  }

  @Test
  void shouldConvertAbstractClass() {
    // Given
    var cu = new CompilationUnit();
    var classDecl = cu.addClass("Example")
      .setPublic(true)
      .setAbstract(true);

    // When
    Definition classDef = converter.convert(classDecl);

    // Then
    assertThat(classDef.metadata())
      .containsEntry("isAbstract", true);
  }
}
