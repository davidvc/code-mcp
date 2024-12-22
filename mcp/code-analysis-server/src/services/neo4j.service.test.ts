import { Neo4jService } from './neo4j.service';
import neo4j from 'neo4j-driver';
import { Session } from 'neo4j-driver';

// Mock neo4j driver
jest.mock('neo4j-driver', () => {
    const mockSession = {
        run: jest.fn(),
        close: jest.fn(),
    };
    const mockDriver = {
        session: jest.fn(() => mockSession),
        close: jest.fn(),
    };
    return {
        driver: jest.fn(() => mockDriver),
        auth: {
            basic: jest.fn(),
        },
    };
});

describe('Neo4jService', () => {
    let service: Neo4jService;
    let mockDriver: any;
    let mockSession: any;

    beforeEach(() => {
        jest.clearAllMocks();
        service = new Neo4jService('bolt://localhost:7687', 'neo4j', 'password');
        mockDriver = (neo4j.driver as jest.Mock)();
        mockSession = mockDriver.session();
    });

    describe('verifyConnection', () => {
        it('should return true when connection is successful', async () => {
            mockSession.run.mockResolvedValueOnce({ records: [] });
            const result = await service.verifyConnection();
            expect(result).toBe(true);
            expect(mockSession.run).toHaveBeenCalledWith('RETURN 1');
        });

        it('should return false when connection fails', async () => {
            mockSession.run.mockRejectedValueOnce(new Error('Connection failed'));
            const result = await service.verifyConnection();
            expect(result).toBe(false);
        });
    });

    describe('getCodeSummary', () => {
        it('should return code summary', async () => {
            const mockSummary = {
                components: 1,
                files: 2,
                classes: 3,
                methods: 4,
            };
            mockSession.run.mockResolvedValueOnce({
                records: [{
                    toObject: () => mockSummary,
                }],
            });

            const result = await service.getCodeSummary();
            expect(result).toEqual(mockSummary);
        });
    });

    describe('getComponentDetails', () => {
        it('should return component details', async () => {
            const mockComponents = [{
                name: 'TestComponent',
                cohesion: 0.8,
                coupling: 0.2,
                fileCount: 1,
                classCount: 1,
            }];
            mockSession.run.mockResolvedValueOnce({
                records: mockComponents.map(comp => ({
                    get: () => comp,
                })),
            });

            const result = await service.getComponentDetails();
            expect(result).toEqual(mockComponents);
        });
    });

    describe('getComplexityMetrics', () => {
        it('should return complexity metrics', async () => {
            const mockMetrics = [{
                method: 'test()',
                complexity: 5,
            }];
            mockSession.run.mockResolvedValueOnce({
                records: mockMetrics.map(metric => ({
                    toObject: () => metric,
                })),
            });

            const result = await service.getComplexityMetrics();
            expect(result).toEqual(mockMetrics);
        });
    });
});
