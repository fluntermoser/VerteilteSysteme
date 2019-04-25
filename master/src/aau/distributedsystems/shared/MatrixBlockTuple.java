package aau.distributedsystems.shared;

import java.io.Serializable;

public class MatrixBlockTuple implements Serializable{
    private MatrixBlock blockA;
    private MatrixBlock blockB;

    public MatrixBlockTuple(MatrixBlock blockA, MatrixBlock blockB) {
        this.blockA = blockA;
        this.blockB = blockB;
    }

    public MatrixBlock getBlockA() {
        return blockA;
    }

    public MatrixBlock getBlockB() {
        return blockB;
    }
}
