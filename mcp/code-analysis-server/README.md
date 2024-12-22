# Code Analysis MCP Server

An MCP server that provides code analysis tools through Neo4j graph database integration.

## Prerequisites

- Node.js and npm
- Neo4j database running with initialized schema (see [../../scripts/neo4j](../../scripts/neo4j))

## Installation

1. Install dependencies:

   ```bash
   npm install
   ```

2. Build the server:
   ```bash
   npm run build
   ```

## Configuration

Set the following environment variables:

- `NEO4J_URI` - Neo4j connection URI (default: neo4j://localhost:7687)
- `NEO4J_USER` - Neo4j username (default: neo4j)
- `NEO4J_PASSWORD` - Neo4j password (required)

## Available Tools

### get_code_summary

Get a summary of the codebase structure, including counts of:

- Components
- Files
- Classes
- Methods

### get_component_details

Get detailed information about components, including:

- Name
- Cohesion metrics
- Coupling metrics
- File count
- Class count

### get_complexity_metrics

Get complexity metrics for methods:

- Method signature
- Complexity score
- Sorted by complexity (descending)
- Limited to top 10 most complex methods

## Usage

1. Start Neo4j database:

   ```bash
   brew services start neo4j
   ```

2. Initialize Neo4j schema and test data:

   ```bash
   cd ../../code-mcp/scripts/neo4j
   ./init.sh <neo4j-password>
   ```

3. Set environment variables:

   ```bash
   export NEO4J_PASSWORD=<your-password>
   ```

4. Run the server:
   ```bash
   npm start
   ```

## Development

- `npm run build` - Build the server
- `npm run watch` - Build and watch for changes
- `npm start` - Start the server

## Integration with Claude

The server is automatically registered with Claude.app during installation. You can use the tools by asking Claude to analyze code using the code analysis tools.
