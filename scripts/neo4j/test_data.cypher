// Clean up existing test data
MATCH (n) DETACH DELETE n;

// Create test component with metrics
CREATE (c:Component {
    name: 'TestComponent',
    cohesion: 0.8,
    coupling: 0.2
});

// Create test file
CREATE (f:File {
    path: '/test/Main.java',
    language: 'Java',
    lastModified: datetime()
});

// Create test class
CREATE (cls:Class {
    fullName: 'com.test.Main',
    name: 'Main',
    complexity: 5
});

// Create test method
CREATE (m:Method {
    fullSignature: 'com.test.Main.main(String[])',
    name: 'main',
    complexity: 2,
    lineCount: 10
});

// Create relationships
MATCH (c:Component {name: 'TestComponent'})
MATCH (f:File {path: '/test/Main.java'})
MATCH (cls:Class {fullName: 'com.test.Main'})
MATCH (m:Method {fullSignature: 'com.test.Main.main(String[])'})
CREATE (c)-[:CONTAINS]->(f)
CREATE (f)-[:CONTAINS]->(cls)
CREATE (cls)-[:CONTAINS]->(m);

// Verify the structure
MATCH (c:Component)-[:CONTAINS]->(f:File)-[:CONTAINS]->(cls:Class)-[:CONTAINS]->(m:Method)
RETURN c.name as component, 
       c.cohesion as cohesion,
       f.path as file,
       cls.fullName as class,
       m.fullSignature as method,
       m.complexity as methodComplexity;
