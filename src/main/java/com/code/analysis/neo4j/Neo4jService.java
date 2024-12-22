package com.code.analysis.neo4j;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.neo4j.driver.*;

/**
 * Service class for interacting with a Neo4j graph database to analyze code
 * structure and metrics.
 *
 * This service provides functionality to:
 * - Query code structure information (components, files, classes, methods)
 * - Retrieve code quality metrics (complexity, coupling, cohesion)
 * - Analyze relationships between code elements
 *
 * The service uses the Neo4j Java driver to execute Cypher queries and process
 * results.
 * All database operations are performed within a session scope to ensure proper
 * resource
 * management and transaction handling.
 *
 * Example usage:
 *
 * <pre>
 * try (Neo4jService service = new Neo4jService(driver)) {
 *     if (service.verifyConnection()) {
 *         Map<String, Object> summary = service.getCodeSummary();
 *         List<Map<String, Object>> metrics = service.getComplexityMetrics();
 *     }
 * }
 * </pre>
 */
public class Neo4jService implements AutoCloseable {

  private final Driver driver;

  public Neo4jService(Driver driver) {
    this.driver = driver;
  }

  /**
   * Verifies the connection to the Neo4j database by executing a simple query.
   *
   * @return true if the connection is successful, false otherwise
   */
  public boolean verifyConnection() {
    try (Session session = driver.session()) {
      session.run("RETURN 1");
      return true;
    } catch (Exception e) {
      return false;
    }
  }

  /**
   * Retrieves a summary of the codebase structure including counts of components,
   * files, classes, and methods.
   *
   * @return A map containing counts of different code elements:
   *         - components: number of distinct components
   *         - files: number of source files
   *         - classes: number of classes
   *         - methods: number of methods
   */
  public Map<String, Object> getCodeSummary() {
    try (Session session = driver.session()) {
      Result result = session.run(
        """
        MATCH (c:Component)
        OPTIONAL MATCH (c)-[:CONTAINS]->(f:File)
        OPTIONAL MATCH (f)-[:CONTAINS]->(cls:Class)
        OPTIONAL MATCH (cls)-[:CONTAINS]->(m:Method)
        RETURN
            count(DISTINCT c) as components,
            count(DISTINCT f) as files,
            count(DISTINCT cls) as classes,
            count(DISTINCT m) as methods
        """
      );
      return result.list().get(0).asMap();
    }
  }

  /**
   * Retrieves detailed information about all components in the codebase.
   * For each component, includes:
   * - Name
   * - Cohesion and coupling metrics
   * - Count of contained files and classes
   *
   * @return List of component details as maps
   */
  public List<Map<String, Object>> getComponentDetails() {
    try (Session session = driver.session()) {
      Result result = session.run(
        """
        MATCH (c:Component)
        OPTIONAL MATCH (c)-[:CONTAINS]->(f:File)
        OPTIONAL MATCH (f)-[:CONTAINS]->(cls:Class)
        WITH c, collect(DISTINCT f) as files, collect(DISTINCT cls) as classes
        RETURN {
            name: c.name,
            cohesion: c.cohesion,
            coupling: c.coupling,
            fileCount: size(files),
            classCount: size(classes)
        } as component
        """
      );
      return result
        .list()
        .stream()
        .map(record -> record.get("component").asMap())
        .collect(Collectors.toList());
    }
  }

  /**
   * Retrieves complexity metrics for methods in the codebase.
   * Returns the top 10 most complex methods, ordered by complexity.
   *
   * @return List of method complexity details, including method signature and
   *         complexity score
   */
  public List<Map<String, Object>> getComplexityMetrics() {
    try (Session session = driver.session()) {
      Result result = session.run(
        """
        MATCH (m:Method)
        WHERE m.complexity > 0
        RETURN {
            method: m.fullSignature,
            complexity: m.complexity
        } as metrics
        ORDER BY m.complexity DESC
        LIMIT 10
        """
      );
      return result
        .list()
        .stream()
        .map(record -> record.get("metrics").asMap())
        .collect(Collectors.toList());
    }
  }

  @Override
  public void close() {
    driver.close();
  }
}
