package com.code.analysis.neo4j;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.neo4j.driver.Value;

@ExtendWith(MockitoExtension.class)
class Neo4jServiceTest {

  @Mock
  private Driver mockDriver;

  @Mock
  private Session mockSession;

  private Neo4jService service;

  @BeforeEach
  void setUp() {
    service = new Neo4jService(mockDriver);
  }

  @Test
  void shouldReturnTrueWhenConnectionIsSuccessful() {
    // Given
    when(mockDriver.session()).thenReturn(mockSession);
    Result mockResult = mock(Result.class);
    when(mockSession.run("RETURN 1")).thenReturn(mockResult);

    // When
    boolean result = service.verifyConnection();

    // Then
    assertThat(result).isTrue();
    verify(mockSession).run("RETURN 1");
  }

  @Test
  void shouldReturnFalseWhenConnectionFails() {
    // Given
    when(mockDriver.session()).thenReturn(mockSession);
    when(mockSession.run("RETURN 1")).thenThrow(new RuntimeException("Connection failed"));

    // When
    boolean result = service.verifyConnection();

    // Then
    assertThat(result).isFalse();
    verify(mockSession).run("RETURN 1");
  }

  @Test
  void shouldCloseDriverWhenServiceIsClosed() throws Exception {
    // When
    service.close();

    // Then
    verify(mockDriver).close();
  }

  @Test
  void shouldReturnCodeSummary() {
    // Given
    when(mockDriver.session()).thenReturn(mockSession);
    Result mockResult = mock(Result.class);
    Record mockRecord = mock(Record.class);
    Map<String, Object> expectedSummary = Map.of(
      "components",
      1L,
      "files",
      2L,
      "classes",
      3L,
      "methods",
      4L
    );
    when(mockResult.list()).thenReturn(List.of(mockRecord));
    when(mockRecord.asMap()).thenReturn(expectedSummary);
    when(mockSession.run(anyString())).thenReturn(mockResult);

    // When
    Map<String, Object> summary = service.getCodeSummary();

    // Then
    assertThat(summary)
      .containsEntry("components", 1L)
      .containsEntry("files", 2L)
      .containsEntry("classes", 3L)
      .containsEntry("methods", 4L);
    verify(mockSession).run(contains("MATCH (c:Component)"));
  }

  @Test
  void shouldReturnComponentDetails() {
    // Given
    when(mockDriver.session()).thenReturn(mockSession);
    Result mockResult = mock(Result.class);
    Record mockRecord = mock(Record.class);
    Value mockValue = mock(Value.class);
    Map<String, Object> componentDetails = Map.of(
      "name",
      "TestComponent",
      "cohesion",
      0.8,
      "coupling",
      0.2,
      "fileCount",
      2L,
      "classCount",
      3L
    );
    when(mockResult.list()).thenReturn(List.of(mockRecord));
    when(mockRecord.get("component")).thenReturn(mockValue);
    when(mockValue.asMap()).thenReturn(componentDetails);
    when(mockSession.run(anyString())).thenReturn(mockResult);

    // When
    List<Map<String, Object>> details = service.getComponentDetails();

    // Then
    assertThat(details).hasSize(1);
    assertThat(details.get(0))
      .containsEntry("name", "TestComponent")
      .containsEntry("cohesion", 0.8)
      .containsEntry("coupling", 0.2)
      .containsEntry("fileCount", 2L)
      .containsEntry("classCount", 3L);
    verify(mockSession).run(contains("MATCH (c:Component)"));
  }

  @Test
  void shouldReturnComplexityMetrics() {
    // Given
    when(mockDriver.session()).thenReturn(mockSession);
    Result mockResult = mock(Result.class);
    Record mockRecord = mock(Record.class);
    Value mockValue = mock(Value.class);
    Map<String, Object> methodMetrics = Map.of(
      "method",
      "com.test.Main.complexMethod()",
      "complexity",
      10
    );
    when(mockResult.list()).thenReturn(List.of(mockRecord));
    when(mockRecord.get("metrics")).thenReturn(mockValue);
    when(mockValue.asMap()).thenReturn(methodMetrics);
    when(mockSession.run(anyString())).thenReturn(mockResult);

    // When
    List<Map<String, Object>> metrics = service.getComplexityMetrics();

    // Then
    assertThat(metrics).hasSize(1);
    assertThat(metrics.get(0))
      .containsEntry("method", "com.test.Main.complexMethod()")
      .containsEntry("complexity", 10);
    verify(mockSession).run(contains("MATCH (m:Method)"));
  }
}
