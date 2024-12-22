package com.code.analysis.neo4j;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.neo4j.driver.*;
import org.testcontainers.containers.Neo4jContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
class Neo4jServiceIT {
    @Container
    private static final Neo4jContainer<?> neo4jContainer = new Neo4jContainer<>("neo4j:5.15.0")
            .withoutAuthentication();

    private Neo4jService service;
    private Driver driver;

    @BeforeEach
    void setUp() {
        driver = GraphDatabase.driver(neo4jContainer.getBoltUrl());
        service = new Neo4jService(driver);
        setupTestData();
    }

    private void setupTestData() {
        try (Session session = driver.session()) {
            // Clean existing data
            session.run("MATCH (n) DETACH DELETE n");

            // Create test components with relationships
            session.run(
                    """
                            CREATE (c1:Component {name: 'Core', cohesion: 0.8, coupling: 0.2})
                            CREATE (c2:Component {name: 'UI', cohesion: 0.7, coupling: 0.3})
                            CREATE (f1:File {name: 'CoreService.java'})
                            CREATE (f2:File {name: 'CoreRepository.java'})
                            CREATE (f3:File {name: 'UIComponent.java'})
                            CREATE (cls1:Class {name: 'CoreService'})
                            CREATE (cls2:Class {name: 'CoreRepository'})
                            CREATE (cls3:Class {name: 'UIComponent'})
                            CREATE (m1:Method {name: 'processData', fullSignature: 'com.core.CoreService.processData()', complexity: 8})
                            CREATE (m2:Method {name: 'saveData', fullSignature: 'com.core.CoreRepository.saveData()', complexity: 3})
                            CREATE (m3:Method {name: 'render', fullSignature: 'com.ui.UIComponent.render()', complexity: 5})

                            // Create relationships
                            CREATE (c1)-[:CONTAINS]->(f1)
                            CREATE (c1)-[:CONTAINS]->(f2)
                            CREATE (c2)-[:CONTAINS]->(f3)
                            CREATE (f1)-[:CONTAINS]->(cls1)
                            CREATE (f2)-[:CONTAINS]->(cls2)
                            CREATE (f3)-[:CONTAINS]->(cls3)
                            CREATE (cls1)-[:CONTAINS]->(m1)
                            CREATE (cls2)-[:CONTAINS]->(m2)
                            CREATE (cls3)-[:CONTAINS]->(m3)
                            """);
        }
    }

    @Test
    void shouldVerifyConnection() {
        assertThat(service.verifyConnection()).isTrue();
    }

    @Test
    void shouldReturnCorrectCodeSummary() {
        Map<String, Object> summary = service.getCodeSummary();

        assertThat(summary)
                .containsEntry("components", 2L)
                .containsEntry("files", 3L)
                .containsEntry("classes", 3L)
                .containsEntry("methods", 3L);
    }

    @Test
    void shouldReturnCorrectComponentDetails() {
        List<Map<String, Object>> details = service.getComponentDetails();

        assertThat(details).hasSize(2);

        // Find Core component
        Map<String, Object> core = details.stream()
                .filter(d -> d.get("name").equals("Core"))
                .findFirst()
                .orElseThrow();

        assertThat(core)
                .containsEntry("cohesion", 0.8)
                .containsEntry("coupling", 0.2)
                .containsEntry("fileCount", 2L)
                .containsEntry("classCount", 2L);

        // Find UI component
        Map<String, Object> ui = details.stream()
                .filter(d -> d.get("name").equals("UI"))
                .findFirst()
                .orElseThrow();

        assertThat(ui)
                .containsEntry("cohesion", 0.7)
                .containsEntry("coupling", 0.3)
                .containsEntry("fileCount", 1L)
                .containsEntry("classCount", 1L);
    }

    @Test
    void shouldReturnComplexityMetricsOrderedByComplexity() {
        List<Map<String, Object>> metrics = service.getComplexityMetrics();

        assertThat(metrics).hasSize(3);

        // First should be the most complex method
        assertThat(metrics.get(0))
                .containsEntry("method", "com.core.CoreService.processData()")
                .containsEntry("complexity", 8);

        // Second should be the next most complex
        assertThat(metrics.get(1))
                .containsEntry("method", "com.ui.UIComponent.render()")
                .containsEntry("complexity", 5);

        // Third should be the least complex
        assertThat(metrics.get(2))
                .containsEntry("method", "com.core.CoreRepository.saveData()")
                .containsEntry("complexity", 3);
    }
}
