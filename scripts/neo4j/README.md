# Neo4j Setup Scripts

These scripts set up the Neo4j database for the code analysis MCP plugin.

## Prerequisites

- Neo4j Community Edition installed (`brew install neo4j`)
- OpenJDK 21 installed (installed automatically with Neo4j)
- Neo4j service running (`brew services start neo4j`)

## Scripts

- `schema.cypher`: Creates constraints and indexes for the graph database
- `test_data.cypher`: Creates test data and verifies the structure
- `init.sh`: Main initialization script that runs both Cypher scripts

## Usage

1. Start Neo4j service if not running:

   ```bash
   brew services start neo4j
   ```

2. Run the initialization script with your Neo4j password:
   ```bash
   ./init.sh <neo4j-password>
   ```

## Schema Structure

### Node Types

- Component: High-level code components
- File: Source code files
- Class: Java classes
- Method: Class methods

### Relationships

- CONTAINS: Hierarchical relationship between nodes

### Indexes

- File language
- Class name
- Method name
- Various metric indexes for performance

## Test Data

The test data creates a simple structure:

```
Component (TestComponent)
└── File (/test/Main.java)
    └── Class (com.test.Main)
        └── Method (main)
```

This includes metrics and properties to verify the schema works correctly.
