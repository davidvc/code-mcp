package com.code.analysis.core.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Builder;

/**
 * Represents documentation associated with code elements.
 * This class captures documentation content and metadata.
 */
@Builder
public record Documentation(
  String id,
  String description,
  DocumentationFormat format,
  Position position,
  List<DocumentationTag> tags,
  Map<String, Object> metadata
) {
  public Documentation {
    tags = Collections.unmodifiableList(
      new ArrayList<>(tags != null ? tags : Collections.emptyList())
    );
    metadata = Collections.unmodifiableMap(
      new HashMap<>(metadata != null ? metadata : Collections.emptyMap())
    );
  }

  public static class DocumentationBuilder {

    private List<DocumentationTag> tags = new ArrayList<>();
    private Map<String, Object> metadata = new HashMap<>();

    public DocumentationBuilder addTag(DocumentationTag tag) {
      this.tags.add(tag);
      return this;
    }

    public DocumentationBuilder addTags(List<DocumentationTag> tags) {
      this.tags.addAll(tags);
      return this;
    }

    public DocumentationBuilder addMetadata(String key, Object value) {
      this.metadata.put(key, value);
      return this;
    }
  }
}
