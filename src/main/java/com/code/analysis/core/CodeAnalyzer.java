package com.code.analysis.core;

import com.code.analysis.core.model.CodeUnit;
import com.code.analysis.core.model.Definition;
import com.code.analysis.core.model.Documentation;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public interface CodeAnalyzer {
  CodeUnit parseFile(Path path) throws IOException;

  List<Definition> extractDefinitions(CodeUnit unit);

  List<Documentation> extractDocumentation(CodeUnit unit);
}
