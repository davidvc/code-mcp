package com.code.analysis.core.model;

/**
 * Represents a position in source code.
 * This record provides a language-agnostic way to represent locations in source
 * code,
 * using 1-based line and column numbers along with a 0-based character offset.
 */
public record Position(
        /** Line number (1-based) */
        int line,

        /** Column number (1-based) */
        int column,

        /** Character offset from start of file (0-based) */
        int offset) {
    /**
     * Creates a new Position instance.
     * 
     * @param line   Line number (1-based)
     * @param column Column number (1-based)
     * @param offset Character offset from start of file (0-based)
     */
    public Position {
        if (line < 1) {
            throw new IllegalArgumentException("Line number must be positive");
        }
        if (column < 1) {
            throw new IllegalArgumentException("Column number must be positive");
        }
        if (offset < 0) {
            throw new IllegalArgumentException("Offset must be non-negative");
        }
    }

    /**
     * Creates a Position from a 0-based line and column.
     * 
     * @param zeroBasedLine   Line number (0-based)
     * @param zeroBasedColumn Column number (0-based)
     * @param offset          Character offset from start of file
     * @return New Position instance
     */
    public static Position fromZeroBased(int zeroBasedLine, int zeroBasedColumn, int offset) {
        return new Position(zeroBasedLine + 1, zeroBasedColumn + 1, offset);
    }

    /**
     * Converts this position to 0-based line and column numbers.
     * 
     * @return Array containing [line, column] in 0-based form
     */
    public int[] toZeroBased() {
        return new int[] { line - 1, column - 1 };
    }

    /**
     * Checks if this position comes before another position.
     * 
     * @param other Position to compare with
     * @return true if this position is before other
     */
    public boolean isBefore(Position other) {
        if (this.line < other.line)
            return true;
        if (this.line > other.line)
            return false;
        return this.column < other.column;
    }

    /**
     * Checks if this position comes after another position.
     * 
     * @param other Position to compare with
     * @return true if this position is after other
     */
    public boolean isAfter(Position other) {
        if (this.line > other.line)
            return true;
        if (this.line < other.line)
            return false;
        return this.column > other.column;
    }
}
