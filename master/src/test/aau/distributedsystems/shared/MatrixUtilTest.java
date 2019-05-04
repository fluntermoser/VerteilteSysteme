package test.aau.distributedsystems.shared;

import aau.distributedsystems.shared.MatrixBlock;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import aau.distributedsystems.shared.MatrixUtil;

import java.util.List;

public class MatrixUtilTest {


    /*
        just a test without assertion to see if the print method prints correctly to the console
     */
    @Test
    public void TestPrintMatrix() {
        int[][] matrix = MatrixUtil.generateMatrix(4, 8);
        MatrixUtil.printMatrix(matrix);
    }


    @Test
    public void TestMultiplyResultSize() {
        int[][] a = MatrixUtil.generateMatrix(4, 25);
        int[][] b = MatrixUtil.generateMatrix(25, 4);
        int[][] resultMatrix = MatrixUtil.multiply(a, b);
        Assertions.assertEquals(25, resultMatrix.length);
        Assertions.assertEquals(25, resultMatrix[0].length);
    }

    @Test
    public void TestMultiplyFunctionallity() {
        int[][] a = new int[2][4];
        int[][] b = new int[3][2];

        a[0][0] = 2;
        a[0][1] = 5;
        a[0][2] = 7;
        a[0][3] = 0;

        a[1][0] = 1;
        a[1][1] = 3;
        a[1][2] = 4;
        a[1][3] = 2;

        b[0][0] = 8;
        b[0][1] = 3;

        b[1][0] = 9;
        b[1][1] = 6;

        b[2][0] = 2;
        b[2][1] = 1;


        int[][] exR = new int[3][4];

        exR[0][0] = 19;
        exR[0][1] = 49;
        exR[0][2] = 68;
        exR[0][3] = 6;

        exR[1][0] = 24;
        exR[1][1] = 63;
        exR[1][2] = 87;
        exR[1][3] = 12;

        exR[2][0] = 5;
        exR[2][1] = 13;
        exR[2][2] = 18;
        exR[2][3] = 2;

        int [][] calculatedResult = MatrixUtil.multiply(a, b);

        MatrixUtil.printMatrix(exR);
        MatrixUtil.printMatrix(calculatedResult);

        Assertions.assertArrayEquals(exR, calculatedResult);
    }

    @Test
    public void TestMultiplicationNotPossible() {
        int[][] a = new int[4][7];
        int[][] b = new int[9][3];

        Assertions.assertThrows(ArithmeticException.class,() -> MatrixUtil.multiply(a, b));
    }

    @Test
    public void TestSplitMatrix() {
        int[][] a = MatrixUtil.generateMatrix(4, 8);

        MatrixUtil.printMatrix(a);

        List<MatrixBlock> blocks = MatrixUtil.splitInBlocks(a);
        for(MatrixBlock block: blocks) {
            MatrixUtil.printMatrix(block.getBlock());
        }
    }

    @Test
    public void TestMultiplicationSplit() {
        int[][] a = MatrixUtil.generateMatrix(4, 4);
        int[][] b = MatrixUtil.generateMatrix(4, 4);

        int[][] resultMatrixLinear = MatrixUtil.multiply(a, b);

        List<MatrixBlock> blocksA = MatrixUtil.splitInBlocks(a); //4 blocks (2x2)
        List<MatrixBlock> blocksB = MatrixUtil.splitInBlocks(b);

        MatrixBlock a11 = blocksA.get(0);
        MatrixBlock a12 = blocksA.get(1);
        MatrixBlock a21 = blocksA.get(2);
        MatrixBlock a22 = blocksA.get(3);

        MatrixBlock b11 = blocksB.get(0);
        MatrixBlock b12 = blocksB.get(1);
        MatrixBlock b21 = blocksB.get(2);
        MatrixBlock b22 = blocksB.get(3);

        int[][] c11 = MatrixUtil.add(MatrixUtil.multiply(a11.getBlock(), b11.getBlock()), MatrixUtil.multiply(a12.getBlock(), b21.getBlock()));
        int[][] c12 = MatrixUtil.add(MatrixUtil.multiply(a11.getBlock(), b12.getBlock()), MatrixUtil.multiply(a12.getBlock(), b22.getBlock()));
        int[][] c21 = MatrixUtil.add(MatrixUtil.multiply(a21.getBlock(), b11.getBlock()), MatrixUtil.multiply(a22.getBlock(), b21.getBlock()));
        int[][] c22 = MatrixUtil.add(MatrixUtil.multiply(a21.getBlock(), b12.getBlock()), MatrixUtil.multiply(a22.getBlock(), b22.getBlock()));

        MatrixUtil.printMatrix(c11);
        MatrixUtil.printMatrix(c12);
        MatrixUtil.printMatrix(c21);
        MatrixUtil.printMatrix(c22);

        MatrixUtil.printMatrix(resultMatrixLinear);


    }

}
