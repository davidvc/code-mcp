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
import java.util.List;
import java.util.UUID;

/**
 * Extracts documentation from Java source files.
 * This class handles parsing and converting Javadoc comments into our model.
 */
class JavaDocumentationExtractor {

  /**
   * Extracts all documentation from a compilation unit.
   *
   * @param cu The compilation unit to process
   * @return A list of Documentation objects
   */
  List<Documentation> extract(CompilationUnit cu) {
    var docs = new ArrayList<Documentation>();

    cu
      .getAllContainedComments()
      .forEach(comment -> {
        if (comment instanceof JavadocComment javadocComment) {
          var javadoc = javadocComment.parse();
          docs.add(convertJavadoc(javadoc, javadocComment));
        }
      });

    return docs;
  }

  private Documentation convertJavadoc(Javadoc javadoc, JavadocComment comment) {
    var begin = comment.getBegin().orElseThrow();

    var builder = Documentation.builder()
      .id(UUID.randomUUID().toString())
      .description(javadoc.getDescription().toText())
      .format(DocumentationFormat.JAVADOC)
      .position(Position.builder().line(begin.line).column(begin.column).build());

    javadoc.getBlockTags().forEach(tag -> builder.addTag(convertBlockTag(tag)));

    return builder.build();
  }

  private DocumentationTag convertBlockTag(JavadocBlockTag tag) {
    var builder = DocumentationTag.builder()
      .id(UUID.randomUUID().toString())
      .name(tag.getTagName())
      .value(tag.getContent().toText());

    tag.getName().ifPresent(name -> builder.addMetadata("name", name));

    return builder.build();
  }
}
