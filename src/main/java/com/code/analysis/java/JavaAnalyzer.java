package com.code.analysis.java;

import com.code.analysis.core.CodeAnalyzer;
import com.code.analysis.core.model.CodeUnit;
import com.code.analysis.core.model.Definition;
import com.code.analysis.core.model.Documentation;
import com.code.analysis.java.converter.JavaConverter;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class JavaAnalyzer implements CodeAnalyzer {

  private final JavaParser parser;
  private final JavaConverter converter;

  public JavaAnalyzer(Path sourceRoot) {
    var typeSolver = new CombinedTypeSolver(
      new ReflectionTypeSolver(),
      new JavaParserTypeSolver(sourceRoot)
    );
    var symbolSolver = new JavaSymbolSolver(typeSolver);
    var config = new ParserConfiguration()
      .setSymbolResolver(symbolSolver)
      .setLanguageLevel(ParserConfiguration.LanguageLevel.JAVA_17);

    this.parser = new JavaParser(config);
    this.converter = new JavaConverter();
  }

  public JavaAnalyzer() {
    var config = new ParserConfiguration()
      .setSymbolResolver(new JavaSymbolSolver(new ReflectionTypeSolver()))
      .setLanguageLevel(ParserConfiguration.LanguageLevel.JAVA_17);

    this.parser = new JavaParser(config);
    this.converter = new JavaConverter();
  }

  @Override
  public CodeUnit parseFile(Path path) throws IOException {
    var parseResult = parser.parse(path);
    if (!parseResult.isSuccessful()) {
      throw new IOException("Failed to parse Java file: " + parseResult.getProblems());
    }

    var compilationUnit = parseResult
      .getResult()
      .orElseThrow(() -> new IOException("Failed to get compilation unit"));

    return converter.convert(compilationUnit);
  }

  @Override
  public List<Definition> extractDefinitions(CodeUnit codeUnit) {
    return new ArrayList<>(codeUnit.definitions());
  }

  @Override
  public List<Documentation> extractDocumentation(CodeUnit codeUnit) {
    return codeUnit.documentation() != null ? List.of(codeUnit.documentation()) : List.of();
  }
}
