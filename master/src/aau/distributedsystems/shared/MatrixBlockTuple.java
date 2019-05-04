package aau.distributedsystems.shared;

import java.io.Serializable;

public class MatrixBlockTuple implements Serializable{
    private MatrixBlock blockA;
    private MatrixBlock blockB;
    private ResultMatrixPart rp;

    public MatrixBlockTuple(MatrixBlock blockA, MatrixBlock blockB, ResultMatrixPart rp) {
        this.blockA = blockA;
        this.blockB = blockB;
        this.rp = rp;
    }

    public MatrixBlock getBlockA() {
        return blockA;
    }

    public MatrixBlock getBlockB() {
        return blockB;
    }

    public ResultMatrixPart getRp() {
        return rp;
    }
}
