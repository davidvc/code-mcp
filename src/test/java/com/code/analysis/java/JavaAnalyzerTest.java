package com.code.analysis.java;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.code.analysis.core.model.DefinitionKind;
import com.code.analysis.core.model.UnitType;
import java.io.IOException;
import java.nio.file.Path;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class JavaAnalyzerTest {

  private JavaAnalyzer analyzer;

  @TempDir
  Path tempDir;

  @BeforeEach
  void setUp() {
    analyzer = new JavaAnalyzer();
  }

  @Test
  void shouldParseValidJavaFile() throws IOException {
    // Given
    var javaCode =
      """
      package com.example;

      public class Example {
          private final String name;

          public Example(String name) {
              this.name = name;
          }

          public String getName() {
              return name;
          }
      }
      """;
    var path = tempDir.resolve("Example.java");
    java.nio.file.Files.writeString(path, javaCode);

    // When
    var unit = analyzer.parseFile(path);

    // Then
    assertThat(unit).isNotNull();
    assertThat(unit.type()).isEqualTo(UnitType.FILE);
    assertThat(unit.name()).isEqualTo("Example.java");
    assertThat(unit.metadata()).containsEntry("packageName", "com.example");

    var definitions = unit.definitions();
    assertThat(definitions).hasSize(3); // class, constructor, method

    var classDefinition = definitions
      .stream()
      .filter(d -> d.kind() == DefinitionKind.TYPE)
      .findFirst()
      .orElseThrow();
    assertThat(classDefinition.name()).isEqualTo("Example");
    assertThat(classDefinition.metadata()).containsEntry("isAbstract", false);

    var methodDefinitions = definitions
      .stream()
      .filter(d -> d.kind() == DefinitionKind.FUNCTION)
      .toList();
    assertThat(methodDefinitions).hasSize(2); // constructor and getName
  }

  @Test
  void shouldExtractDocumentation() throws IOException {
    // Given
    var javaCode =
      """
      package com.example;

      /**
       * Example class demonstrating documentation extraction.
       */
      public class Example {
          /** The person's name */
          private final String name;

          /**
           * Creates a new Example instance.
           * @param name the person's name
           */
          public Example(String name) {
              this.name = name;
          }

          /**
           * Gets the person's name.
           * @return the name
           */
          public String getName() {
              return name;
          }
      }
      """;
    var path = tempDir.resolve("Example.java");
    java.nio.file.Files.writeString(path, javaCode);

    // When
    var unit = analyzer.parseFile(path);
    var docs = analyzer.extractDocumentation(unit);

    // Then
    assertThat(docs).isNotEmpty();
    var doc = docs.get(0);
    assertThat(doc.description()).contains("Example class demonstrating documentation extraction");
  }

  @Test
  void shouldHandleInvalidJavaFile() {
    // Given
    var invalidCode = "this is not valid java code";
    var path = tempDir.resolve("Invalid.java");

    // When/Then
    assertThrows(IOException.class, () -> {
      java.nio.file.Files.writeString(path, invalidCode);
      analyzer.parseFile(path);
    });
  }
}
