package com.code.analysis.neo4j;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.neo4j.driver.*;
import org.neo4j.harness.Neo4j;
import org.neo4j.harness.Neo4jBuilders;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Arrays;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class Neo4jServiceIT {
        private static Neo4j embeddedDatabaseServer;
        private static Driver driver;
        private Neo4jService service;

        @BeforeAll
        static void startNeo4j() throws IOException {
                // Initialize embedded database
                embeddedDatabaseServer = Neo4jBuilders.newInProcessBuilder()
                                .withDisabledServer()
                                .build();

                driver = GraphDatabase.driver(embeddedDatabaseServer.boltURI());

                // Read and execute schema and test data files
                String schema = Files.readString(Path.of("neo4j/scripts/schema.cypher"));
                String testData = Files.readString(Path.of("neo4j/data/test_data.cypher"));

                // Execute each statement separately
                try (Session session = driver.session()) {
                        // Split statements by semicolon and filter out empty lines
                        Stream.of(schema, testData)
                                        .flatMap(content -> Arrays.stream(content.split(";")))
                                        .map(String::trim)
                                        .filter(stmt -> !stmt.isEmpty())
                                        .forEach(stmt -> session.run(stmt + ";"));
                }
        }

        @AfterAll
        static void stopNeo4j() {
                if (driver != null) {
                        driver.close();
                }
                if (embeddedDatabaseServer != null) {
                        embeddedDatabaseServer.close();
                }
        }

        @BeforeEach
        void setUp() {
                service = new Neo4jService(driver);
        }

        @Test
        void shouldVerifyConnection() {
                assertThat(service.verifyConnection()).isTrue();
        }

        @Test
        void shouldReturnCorrectCodeSummary() {
                Map<String, Object> summary = service.getCodeSummary();

                assertThat(summary)
                                .containsEntry("components", 1L)
                                .containsEntry("files", 1L)
                                .containsEntry("classes", 1L)
                                .containsEntry("methods", 1L);
        }

        @Test
        void shouldReturnCorrectComponentDetails() {
                List<Map<String, Object>> details = service.getComponentDetails();

                assertThat(details).hasSize(1);

                Map<String, Object> component = details.get(0);
                assertThat(component)
                                .containsEntry("name", "TestComponent")
                                .containsEntry("cohesion", 0.8)
                                .containsEntry("coupling", 0.2)
                                .containsEntry("fileCount", 1L)
                                .containsEntry("classCount", 1L);
        }

        @Test
        void shouldReturnComplexityMetrics() {
                List<Map<String, Object>> metrics = service.getComplexityMetrics();

                assertThat(metrics).hasSize(1);
                assertThat(metrics.get(0))
                                .containsEntry("method", "com.test.Main.main(String[])")
                                .containsEntry("complexity", 2L);
        }
}
