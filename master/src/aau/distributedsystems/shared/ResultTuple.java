package aau.distributedsystems.shared;

import java.io.Serializable;

public class ResultTuple implements Serializable {

    private int[][] matrix;
    private ResultMatrixPart part;

    public ResultTuple(int[][] matrix, ResultMatrixPart part) {
        this.matrix = matrix;
        this.part = part;
    }

    public int[][] getMatrix() {
        return matrix;
    }

    public ResultMatrixPart getPart() {
        return part;
    }
}
