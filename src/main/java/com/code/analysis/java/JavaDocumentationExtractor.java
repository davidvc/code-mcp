package com.code.analysis.java;

import com.code.analysis.core.model.Documentation;
import com.code.analysis.core.model.DocumentationFormat;
import com.code.analysis.core.model.DocumentationTag;
import com.code.analysis.core.model.Position;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.comments.JavadocComment;
import com.github.javaparser.javadoc.Javadoc;
import com.github.javaparser.javadoc.JavadocBlockTag;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Extracts documentation from Java source files.
 * This class handles parsing and converting Javadoc comments into our model.
 */
class JavaDocumentationExtractor {

  /**
   * Extracts all documentation from a compilation unit.
   *
   * @param compilationUnit The compilation unit to process
   * @return A list of Documentation objects
   */
  List<Documentation> extract(CompilationUnit compilationUnit) {
    var documentations = new ArrayList<Documentation>();

    compilationUnit
      .getAllContainedComments()
      .forEach(comment -> {
        if (comment instanceof JavadocComment javadocComment) {
          var javadoc = javadocComment.parse();
          documentations.add(convertJavadoc(javadoc, javadocComment));
        }
      });

    return documentations;
  }

  private Documentation convertJavadoc(Javadoc javadoc, JavadocComment comment) {
    var begin = comment.getBegin().orElseThrow();
    var tags = javadoc.getBlockTags().stream().map(this::convertBlockTag).collect(Collectors.toList());

    return Documentation.builder()
      .id(UUID.randomUUID().toString())
      .description(javadoc.getDescription().toText())
      .format(DocumentationFormat.JAVADOC)
      .position(Position.builder().line(begin.line).column(begin.column).build())
      .tags(tags)
      .build();
  }

  private DocumentationTag convertBlockTag(JavadocBlockTag tag) {
    Map<String, Object> metadata = new HashMap<>();
    tag.getName().ifPresent(name -> metadata.put("name", name));

    return DocumentationTag.builder()
      .id(UUID.randomUUID().toString())
      .name(tag.getTagName())
      .value(tag.getContent().toText())
      .metadata(metadata)
      .build();
  }
}
