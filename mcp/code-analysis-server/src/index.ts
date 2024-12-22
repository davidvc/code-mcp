#!/usr/bin/env node
import { Server } from '@modelcontextprotocol/sdk/server/index.js';
import { StdioServerTransport } from '@modelcontextprotocol/sdk/server/stdio.js';
import {
    CallToolRequestSchema,
    ErrorCode,
    ListResourcesRequestSchema,
    ListToolsRequestSchema,
    McpError,
} from '@modelcontextprotocol/sdk/types.js';
import { Neo4jService } from './services/neo4j.service.js';

class CodeAnalysisServer {
    private server: Server;
    private neo4j: Neo4jService;

    constructor() {
        // Initialize Neo4j service
        const uri = process.env.NEO4J_URI || 'neo4j://localhost:7687';
        const username = process.env.NEO4J_USER || 'neo4j';
        const password = process.env.NEO4J_PASSWORD;
        
        if (!password) {
            throw new Error('NEO4J_PASSWORD environment variable is required');
        }

        this.neo4j = new Neo4jService(uri, username, password);
        
        // Initialize MCP server
        this.server = new Server(
            {
                name: 'code-analysis-server',
                version: '0.1.0',
            },
            {
                capabilities: {
                    resources: {},
                    tools: {},
                },
            }
        );

        this.setupToolHandlers();
        this.setupErrorHandler();
    }

    private setupToolHandlers() {
        // List available tools
        this.server.setRequestHandler(ListToolsRequestSchema, async () => ({
            tools: [
                {
                    name: 'get_code_summary',
                    description: 'Get a summary of the codebase structure',
                    inputSchema: {
                        type: 'object',
                        properties: {},
                        required: [],
                    },
                },
                {
                    name: 'get_component_details',
                    description: 'Get detailed information about components',
                    inputSchema: {
                        type: 'object',
                        properties: {},
                        required: [],
                    },
                },
                {
                    name: 'get_complexity_metrics',
                    description: 'Get complexity metrics for methods',
                    inputSchema: {
                        type: 'object',
                        properties: {},
                        required: [],
                    },
                },
            ],
        }));

        // Handle tool calls
        this.server.setRequestHandler(CallToolRequestSchema, async (request) => {
            try {
                switch (request.params.name) {
                    case 'get_code_summary': {
                        const summary = await this.neo4j.getCodeSummary();
                        return {
                            content: [
                                {
                                    type: 'text',
                                    text: JSON.stringify(summary, null, 2),
                                },
                            ],
                        };
                    }

                    case 'get_component_details': {
                        const details = await this.neo4j.getComponentDetails();
                        return {
                            content: [
                                {
                                    type: 'text',
                                    text: JSON.stringify(details, null, 2),
                                },
                            ],
                        };
                    }

                    case 'get_complexity_metrics': {
                        const metrics = await this.neo4j.getComplexityMetrics();
                        return {
                            content: [
                                {
                                    type: 'text',
                                    text: JSON.stringify(metrics, null, 2),
                                },
                            ],
                        };
                    }

                    default:
                        throw new McpError(
                            ErrorCode.MethodNotFound,
                            `Unknown tool: ${request.params.name}`
                        );
                }
            } catch (error) {
                console.error('Tool execution error:', error);
                throw new McpError(
                    ErrorCode.InternalError,
                    'Failed to execute tool'
                );
            }
        });
    }

    private setupErrorHandler() {
        this.server.onerror = (error) => {
            console.error('[MCP Error]', error);
        };

        process.on('SIGINT', async () => {
            await this.close();
            process.exit(0);
        });
    }

    async run() {
        // Verify Neo4j connection
        const connected = await this.neo4j.verifyConnection();
        if (!connected) {
            throw new Error('Failed to connect to Neo4j');
        }

        // Start server
        const transport = new StdioServerTransport();
        await this.server.connect(transport);
        console.error('Code Analysis MCP server running on stdio');
    }

    async close() {
        await this.neo4j.close();
        await this.server.close();
    }
}

// Start server
const server = new CodeAnalysisServer();
server.run().catch(console.error);
