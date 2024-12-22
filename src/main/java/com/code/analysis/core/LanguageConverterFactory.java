package com.code.analysis.core;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Factory for creating language-specific code converters.
 * This factory manages the creation of converters for different programming
 * languages,
 * allowing easy extension to support new languages.
 */
public class LanguageConverterFactory {

  private final Map<String, ConverterSupplier> converterSuppliers;

  public LanguageConverterFactory() {
    this.converterSuppliers = new HashMap<>();
    registerDefaultConverters();
  }

  /**
   * Gets a converter for the specified file based on its extension.
   *
   * @param path The path to the file to analyze
   * @return An Optional containing the appropriate converter, or empty if no
   *         converter exists
   */
  public Optional<CodeAnalyzer> getConverter(Path path) {
    String extension = getFileExtension(path);
    return Optional.ofNullable(converterSuppliers.get(extension)).map(supplier ->
      supplier.create(path)
    );
  }

  /**
   * Registers a new converter for a specific file extension.
   *
   * @param extension The file extension (without the dot)
   * @param supplier  A supplier that creates a new converter instance
   */
  public void registerConverter(String extension, ConverterSupplier supplier) {
    converterSuppliers.put(extension.toLowerCase(), supplier);
  }

  private void registerDefaultConverters() {
    // Register Java converter by default
    registerConverter("java", path -> new com.code.analysis.java.JavaAnalyzer(path));
  }

  private String getFileExtension(Path path) {
    String fileName = path.getFileName().toString();
    int lastDotIndex = fileName.lastIndexOf('.');
    return lastDotIndex > 0 ? fileName.substring(lastDotIndex + 1).toLowerCase() : "";
  }

  /**
   * Functional interface for creating converter instances.
   * This allows different converters to have different constructor parameters.
   */
  @FunctionalInterface
  public interface ConverterSupplier {
    CodeAnalyzer create(Path sourceRoot);
  }
}
