import neo4j, { Driver, Session } from 'neo4j-driver';

export class Neo4jService {
    private driver: Driver;

    constructor(uri: string, username: string, password: string) {
        this.driver = neo4j.driver(uri, neo4j.auth.basic(username, password));
    }

    async verifyConnection(): Promise<boolean> {
        const session = this.driver.session();
        try {
            await session.run('RETURN 1');
            return true;
        } catch (error) {
            console.error('Neo4j connection error:', error);
            return false;
        } finally {
            await session.close();
        }
    }

    async getSession(): Promise<Session> {
        return this.driver.session();
    }

    async close(): Promise<void> {
        await this.driver.close();
    }

    // Code Analysis Queries
    async getCodeSummary(): Promise<any> {
        const session = await this.getSession();
        try {
            const result = await session.run(`
                MATCH (c:Component)
                OPTIONAL MATCH (c)-[:CONTAINS]->(f:File)
                OPTIONAL MATCH (f)-[:CONTAINS]->(cls:Class)
                OPTIONAL MATCH (cls)-[:CONTAINS]->(m:Method)
                RETURN 
                    count(DISTINCT c) as components,
                    count(DISTINCT f) as files,
                    count(DISTINCT cls) as classes,
                    count(DISTINCT m) as methods
            `);
            return result.records[0].toObject();
        } finally {
            await session.close();
        }
    }

    async getComponentDetails(): Promise<any[]> {
        const session = await this.getSession();
        try {
            const result = await session.run(`
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
            `);
            return result.records.map(record => record.get('component'));
        } finally {
            await session.close();
        }
    }

    async getComplexityMetrics(): Promise<any[]> {
        const session = await this.getSession();
        try {
            const result = await session.run(`
                MATCH (m:Method)
                WHERE m.complexity > 0
                RETURN m.fullSignature as method, m.complexity as complexity
                ORDER BY m.complexity DESC
                LIMIT 10
            `);
            return result.records.map(record => record.toObject());
        } finally {
            await session.close();
        }
    }
}
