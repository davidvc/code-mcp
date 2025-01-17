package com.code.analysis.java.converter;

import com.code.analysis.core.model.Definition;
import com.code.analysis.core.model.DefinitionKind;
import com.code.analysis.core.model.Reference;
import com.code.analysis.core.model.ReferenceKind;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JavaClassConverter {

  public Definition convert(ClassOrInterfaceDeclaration classDecl) {
    Map<String, Object> metadata = new HashMap<>();
    metadata.put("visibility", getVisibility(classDecl));
    metadata.put("isAbstract", classDecl.isAbstract());
    metadata.put("isInterface", classDecl.isInterface());

    Definition classDef = new Definition(
        classDecl.getNameAsString(),
        DefinitionKind.TYPE,
        metadata
    );

    // Handle superclass
    if (classDecl.getExtendedTypes().isNonEmpty()) {
      String superClassName = classDecl.getExtendedTypes().get(0).getNameAsString();
      classDef.addReference(new Reference(
          ReferenceKind.EXTEND,
          superClassName
      ));
    }

    // Handle implemented interfaces
    classDecl.getImplementedTypes().forEach(impl -> {
      classDef.addReference(new Reference(
          ReferenceKind.IMPLEMENT,
          impl.getNameAsString()
      ));
    });

    return classDef;
  }

  private String getVisibility(ClassOrInterfaceDeclaration classDecl) {
    if (classDecl.isPublic()) {
      return "public";
    } else if (classDecl.isProtected()) {
      return "protected";
    } else if (classDecl.isPrivate()) {
      return "private";
    }
    return "package-private";
  }
}
