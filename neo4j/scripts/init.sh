#!/bin/bash

# Exit on error
set -e

# Check if password is provided
if [ -z "$1" ]; then
    echo "Usage: $0 <neo4j-password>"
    exit 1
fi

PASSWORD=$1
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ROOT_DIR="$(dirname "$SCRIPT_DIR")"

echo "Setting up Neo4j schema..."
JAVA_HOME=/usr/local/opt/openjdk@21 cypher-shell -u neo4j -p "$PASSWORD" < "$SCRIPT_DIR/schema.cypher"

echo "Creating test data..."
JAVA_HOME=/usr/local/opt/openjdk@21 cypher-shell -u neo4j -p "$PASSWORD" < "$ROOT_DIR/data/test_data.cypher"

echo "Neo4j initialization complete!"
