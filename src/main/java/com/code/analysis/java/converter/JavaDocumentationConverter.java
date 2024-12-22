package com.code.analysis.java.converter;

import com.code.analysis.core.model.Documentation;
import com.code.analysis.core.model.DocumentationFormat;
import com.code.analysis.core.model.DocumentationTag;
import com.code.analysis.core.model.ModelValidator;
import com.code.analysis.core.model.Position;
import com.github.javaparser.ast.comments.JavadocComment;
import com.github.javaparser.javadoc.JavadocBlockTag;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Converts Javadoc comments into language-agnostic documentation.
 */
public class JavaDocumentationConverter {

  /**
   * Creates a position from a JavaParser node.
   */
  private static Position createPositionFromNode(JavadocComment node) {
    var begin = node.getBegin().orElseThrow();
    return Position.builder().line(begin.line).column(begin.column).build();
  }

  public Documentation convertJavadoc(JavadocComment comment) {
    ModelValidator.validateNotNull(comment, "Javadoc comment");
    var javadoc = comment.parse();
    var tags = javadoc.getBlockTags().stream().map(this::convertBlockTag).collect(Collectors.toList());

    return Documentation.builder()
      .id(UUID.randomUUID().toString())
      .description(javadoc.getDescription().toText())
      .format(DocumentationFormat.JAVADOC)
      .position(createPositionFromNode(comment))
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
