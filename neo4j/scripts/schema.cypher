// Create constraints for uniqueness
CREATE CONSTRAINT component_name IF NOT EXISTS FOR (c:Component) REQUIRE c.name IS UNIQUE;
CREATE CONSTRAINT file_path IF NOT EXISTS FOR (f:File) REQUIRE f.path IS UNIQUE;
CREATE CONSTRAINT class_unique IF NOT EXISTS FOR (c:Class) REQUIRE c.fullName IS UNIQUE;
CREATE CONSTRAINT method_unique IF NOT EXISTS FOR (m:Method) REQUIRE m.fullSignature IS UNIQUE;

// Create indexes for performance
CREATE INDEX file_language IF NOT EXISTS FOR (f:File) ON (f.language);
CREATE INDEX class_name IF NOT EXISTS FOR (c:Class) ON (c.name);
CREATE INDEX method_name IF NOT EXISTS FOR (m:Method) ON (m.name);

// Create indexes for metrics
CREATE INDEX component_cohesion IF NOT EXISTS FOR (c:Component) ON (c.cohesion);
CREATE INDEX component_coupling IF NOT EXISTS FOR (c:Component) ON (c.coupling);
CREATE INDEX class_complexity IF NOT EXISTS FOR (c:Class) ON (c.complexity);
CREATE INDEX method_complexity IF NOT EXISTS FOR (m:Method) ON (m.complexity);
