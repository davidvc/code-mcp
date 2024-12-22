# Code Analysis MCP Plugin

A Model Context Protocol (MCP) plugin that enables AI assistants like Cline and Claude to perform sophisticated code analysis and answer questions about codebases.

## Overview

This plugin provides AI assistants with direct access to codebase analysis capabilities through a Neo4j graph database, enabling them to:

- Analyze code structure and relationships
- Calculate code quality metrics
- Extract documentation and context
- Answer high-level questions about the codebase

## Features

- **Code Structure Analysis**

  - Component and module relationships
  - Class hierarchies and dependencies
  - Method complexity and relationships
  - File organization and imports

- **Code Quality Metrics**

  - Cyclomatic complexity
  - Coupling and cohesion metrics
  - Code duplication detection
  - Test coverage analysis

- **Documentation Analysis**

  - Markdown file parsing
  - Documentation quality metrics
  - Documentation coverage analysis
  - Automated documentation updates

- **Natural Language Queries**
  - Ask questions about code structure
  - Get high-level architectural overviews
  - Identify potential code issues
  - Find relevant code examples

## Example Queries

The plugin can answer questions like:

- "Please summarize the key features and functionality of this codebase"
- "Write a high level design document for this codebase, using object and sequence diagrams where useful"
- "Write a summary of the key components of this codebase, with a paragraph or two for each component"
- "What are some of the more problematic files, applying SOLID and clean coding principles"

## Architecture

The plugin uses:

- Neo4j graph database for storing code structure and relationships
- Language-specific parsers for code analysis
- MCP interface for AI assistant integration
- Advanced metrics calculation for code quality analysis

## Getting Started

See [CONTRIBUTING.md](CONTRIBUTING.md) for development setup instructions.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
