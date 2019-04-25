package aau.distributedsystems.shared;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class MatrixUtil {
    public static int[][] generateMatrix(int cols, int rows) {
        int minNumber = 0;
        int maxNumber = 9;
        int[][] matrix = new int[cols][rows];
        for(int i = 0; i < cols; i++) {
            for(int j = 0; j < rows; j++) {
                matrix[i][j] = ThreadLocalRandom.current().nextInt(minNumber, maxNumber + 1);
            }
        }
        return matrix;
    }

    public static int[][] multiply(int[][] a, int[][] b) {
        if(a == null || b == null) {
            return null;
        }

        if(a.length != b[0].length)
            throw new ArithmeticException("Number of columns of matrix a must match the number of rows of matrix be for the multiplication to be possible");

        int s = b.length;
        int q = a[0].length;
        int r = a.length;

        int[][] result = new int[s][q];

        for(int i = 0; i < q; i++) {
            for(int j = 0; j < s; j++) {
                for(int z = 0; z < r; z++) {
                     result[j][i] += a[z][i]*b[j][z];
                }
            }
        }

        return result;
    }

    public static int[][] add(int[][] a, int[][] b) {
        if(a == null || b == null) {
            return null;
        }

        if(a.length != b.length || a[0].length != b[0].length)
            throw new ArithmeticException("Number of columns and rows of matrix a must match the number of columns an rows of matrix be for the addition to be possible");

        int[][] result = new int[a.length][a[0].length];

        printMatrix(a);
        printMatrix(b);
        for(int i = 0; i < a.length; i++) {
            for(int j = 0; j < a[0].length; j++) {
                result[i][j] = a[i][j] + b[i][j];
            }
        }

        return result;
    }

    public static void printMatrix(int[][] m) {
        System.out.println("Matrix: ");
        for(int i = 0; i < m[0].length; i++) {
            String line = "";
            for(int j = 0; j < m.length; j ++) {
                line += " " + m[j][i];
            }
            System.out.println(line);
        }
    }

    /*
        For the sake of simplicity we simply split in 4 blocks (a11 ... a22)
     */
    public static List<MatrixBlock> splitInBlocks(int[][] matrix) {
        int rowStart, rowEnd, colStart, colEnd;

        List<MatrixBlock> blocks = new ArrayList<>();

        for (int i = 0; i < 2; i++){
            rowStart = i*matrix[0].length/2;
            rowEnd = rowStart + matrix[0].length/2;
            for (int j = 0; j < 2; j++){
                colStart = j*matrix.length/2;
                colEnd = colStart + matrix.length/2;
                blocks.add(new MatrixBlock(matrix, rowStart, rowEnd, colStart, colEnd));
            }
        }

        return blocks;
    }
}
