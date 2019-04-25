package aau.distributedsystems.shared;

import java.io.Serializable;

public class MatrixBlock implements Serializable {
    private int[][] block;
    private int rowStart;
    private int rowEnd;
    private int colStart;
    private int colEnd;

    public MatrixBlock(int[][] matrix, int rowStart, int rowEnd, int colStart, int colEnd) {

        this.block = split(matrix, rowStart, rowEnd, colStart, colEnd);
        this.rowStart = rowStart;
        this.rowEnd = rowEnd;
        this.colStart = colStart;
        this.colEnd = colEnd;
    }



    private int[][] split(int[][] matrix, int rowStart, int rowEnd, int colStart, int colEnd) {
        int rows = rowEnd-rowStart;
        int cols = colEnd-colStart;
        int[][] block = new int[cols][rows];
        int row, col = 0;
        for(int i = colStart; i < colEnd; i++) {
            row = 0;
            for(int j = rowStart; j < rowEnd; j++) {
                block[col][row] = matrix[i][j];
                row++;
            }
            col++;
        }
        return block;
    }

    public int[][] getBlock() {
        return block;
    }

    public int getRowStart() {
        return rowStart;
    }

    public int getRowEnd() {
        return rowEnd;
    }

    public int getColStart() {
        return colStart;
    }

    public int getColEnd() {
        return colEnd;
    }
}
