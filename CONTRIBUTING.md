# Contributing to Code Analysis MCP Plugin

This guide will help you set up your development environment and understand the contribution process.

## System Requirements

### Required Software

- **Java 21 or higher**

  - Required for modern language features:
    - Enhanced pattern matching
    - Record patterns
    - String templates
    - Virtual threads
    - Structured concurrency
  - Recommended: Install via Homebrew on macOS:
    ```bash
    brew install openjdk@21
    ```

- **Neo4j 5.18.0 or higher**

  - Required for graph database functionality
  - Install via Homebrew on macOS:
    ```bash
    brew install neo4j
    ```

- **Maven 3.9 or higher**
  - Required for build management
  - Install via Homebrew on macOS:
    ```bash
    brew install maven
    ```

### Environment Setup

1. **Configure Java 21**

   ```bash
   # Add to your shell profile (.zshrc, .bashrc, etc.):
   export JAVA_HOME=/usr/local/opt/openjdk@21
   export PATH="$JAVA_HOME/bin:$PATH"
   ```

2. **Configure Neo4j**

   ```bash
   # Start Neo4j service
   brew services start neo4j

   # Set initial password (first time only)
   neo4j-admin set-initial-password your-password
   ```

3. **Clone and Build**

   ```bash
   # Clone repository
   git clone https://github.com/your-username/code-mcp.git
   cd code-mcp

   # Build project
   mvn clean install
   ```

## Development Workflow

### Building and Testing

1. **Run Unit Tests**

   ```bash
   mvn test
   ```

2. **Run Integration Tests**

   ```bash
   mvn verify
   ```

3. **Build Project**
   ```bash
   mvn clean package
   ```

### Neo4j Development

The project uses Neo4j in two ways:

1. Embedded database for integration tests
2. Standalone server for development and production

#### Local Development

1. Start Neo4j server:

   ```bash
   brew services start neo4j
   ```

2. Initialize schema and test data:
   ```bash
   cd neo4j/scripts
   ./init.sh your-neo4j-password
   ```

## Code Style and Guidelines

1. Coding principles

   - Follow clean code principles
   - Apply SOLID principles
   - Maximum method complexity: 5
   - Maximum method length: 25 lines
   - Use meaningful variable and method names

2. **Code Style and Formatting**

   - Code is automatically formatted using Prettier

   To format code:

   ```bash
   # Format all files
   mvn initialize  # First time only, to set up node/npm
   npm run format

   # Check formatting (runs automatically during mvn verify)
   npm run format:check
   ```

3. **Testing**

   - Follow TDD approach
   - Focus on testing at the public contract level, rather than detailed unit tests
   - Maintain test coverage above 90%

4. **Git Workflow**
   - Create feature branches from main
   - Use meaningful commit messages
   - Include tests with all changes
   - Submit pull requests for review

## Documentation

1. **Code Documentation**

   - Add useful class-level and method-level comments where it helps to explain intent
   - Include example usage where appropriate
   - Document complex algorithms and decisions

2. **Project Documentation**
   - Update README.md for user-facing changes
   - Update CONTRIBUTING.md for development changes
   - Keep our high-level technical design document current
   - If you are using an AI to help you code, refer to this document and .clinerules for general context

## Getting Help

- Create an issue for bugs or feature requests
- Refer to the technical design document for architecture details

## License

By contributing to this project, you agree that your contributions will be licensed under the MIT License.
