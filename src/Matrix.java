import org.omg.CORBA.MARSHAL;

public final class Matrix {
    private final int[][] value;// value must be not change
    private final int row_size;
    private final int column_size;
    private final int row_s;
    private final int row_e;
    private final int column_s;
    private final int column_e;
    private Matrix(int[][] value, int row_size, int column_size, int row_s, int row_e, int column_s, int column_e)
    {
        this.value = new int[value.length][value[0].length];
        for (int i = row_s; i <= row_e; ++i)
            System.arraycopy(value[i], column_s, this.value[i], column_s, column_size);
        this.row_e = row_e;
        this.row_s = row_s;
        this.row_size = row_size;
        this.column_e = column_e;
        this.column_s = column_s;
        this.column_size = column_size;
    }
    public Matrix(int[][] value)//for outer
    {
        this(value, value.length, value[0].length, 0, value.length - 1, 0, value[0].length - 1);
    }
    public Matrix(int[][] value, int row_size, int column_size)//for outer
    {
        this(value, row_size, column_size, 0, row_size - 1, 0, column_size - 1);
    }
    private Matrix(int[][] value, int row_s, int row_e, int column_s, int column_e)//just for inner method, start index may not be zero
    {
        this.value = value;
        this.column_size = column_e - column_s + 1;
        this.column_s = column_s;
        this.column_e = column_e;
        this.row_size = row_e - row_s + 1;
        this.row_s = row_s;
        this.row_e = row_e;
    }
    public int getRow_size()
    {
        return row_size;
    }

    public int getColumn_size()
    {
        return column_size;
    }

    public int getValue(int i, int j)
    {
        return value[i + this.row_s][j + this.column_s];
    }
    public static Matrix add(Matrix a, Matrix b)
    {
        int[][] result = new int[a.row_size][a.column_size];
        for(int i = 0; i < a.row_size; ++i)
            for(int j = 0; j < a.column_size; ++j)
                result[i][j] = a.getValue(i, j) + b.getValue(i, j);
        return new Matrix(result, 0, a.row_size - 1, 0, a.column_size - 1);
    }
    public static Matrix subtract(Matrix a, Matrix b)
    {
        int[][] result = new int[a.row_size][a.column_size];
        for(int i = 0; i < a.row_size; ++i)
            for(int j = 0; j < a.column_size; ++j)
                result[i][j] = a.getValue(i, j) - b.getValue(i, j);
        return new Matrix(result, 0, a.row_size - 1, 0, a.column_size - 1);
    }
    public static Matrix multiply(Matrix a, Matrix b)
    {
        int[][] result = new int[a.row_size][b.column_size];
        for(int i = 0; i < a.row_size; ++i)
            for(int j = 0; j < b.column_size; ++j)
                for(int k = 0; k < a.column_size; ++k)
                    result[i][j] += a.value[i + a.row_s][k + a.column_s] * b.value[k + b.row_s][j + b.column_s];
        return new Matrix(result, 0, a.row_size - 1, 0, b.column_size - 1);
    }
    public static Matrix strassen(Matrix a, Matrix b)
    {
        int[][] result = new int[a.row_size][a.column_size];
        if(a.row_size == 1)
        {
            result[0][0] = a.getValue(0, 0) * b.getValue(0, 0);
            return new Matrix(result,0,result.length - 1,0, result[0].length - 1);
        }

        int n = a.row_e;
        int nd2 = (a.row_e + a.row_s) / 2;
        int one = a.row_s;
        Matrix A11 = new Matrix(a.value, one, nd2, one, nd2);
        Matrix A12 = new Matrix(a.value, one, nd2, nd2 + 1, n);
        Matrix A21 = new Matrix(a.value, nd2 + 1, n, one, nd2);
        Matrix A22 = new Matrix(a.value, nd2 + 1, n, nd2 + 1,n);
        n = b.row_e;
        nd2 = (b.row_e + b.row_s) / 2;
        one = b.row_s;
        Matrix B11 = new Matrix(b.value, one, nd2, one, nd2);
        Matrix B12 = new Matrix(b.value, one, nd2, nd2 + 1, n);
        Matrix B21 = new Matrix(b.value, nd2 + 1, n, one, nd2);
        Matrix B22 = new Matrix(b.value, nd2 + 1, n, nd2 + 1,n);
        Matrix S1 = Matrix.subtract(B12, B22);
        Matrix S2 = Matrix.add(A11, A12);
        Matrix S3 = Matrix.add(A21, A22);
        Matrix S4 = Matrix.subtract(B21, B11);
        Matrix S5 = Matrix.add(A11, A22);
        Matrix S6 = Matrix.add(B11, B22);
        Matrix S7 = Matrix.subtract(A12, A22);
        Matrix S8 = Matrix.add(B21, B22);
        Matrix S9 = Matrix.subtract(A11, A21);
        Matrix S10 = Matrix.add(B11, B12);
        Matrix P1 = Matrix.strassen(A11, S1);
        Matrix P2 = Matrix.strassen(S2, B22);
        Matrix P3 = Matrix.strassen(S3, B11);
        Matrix P4 = Matrix.strassen(A22, S4);
        Matrix P5 = Matrix.strassen(S5, S6);
        Matrix P6 = Matrix.strassen(S7, S8);
        Matrix P7 = Matrix.strassen(S9, S10);
        one = 0;
        nd2 = (result.length - 1) / 2;
        n = result.length - 1;
        for(int i = one; i <= nd2; ++i)
            for (int j = one; j <= nd2; ++j)
                result[i][j] = P5.getValue(i - one, j - one) + P4.getValue(i - one, j - one) -
                        P2.getValue(i - one, j - one) + P6.getValue(i - one, j - one);
        for(int i = one; i <= nd2; ++i)
            for (int j = nd2 + 1; j <=n; ++j)
                result[i][j] = P1.getValue(i - one, j - nd2 - 1) + P2.getValue(i - one, j - nd2 - 1);

        for(int i = nd2 + 1; i <= n; ++i)
            for(int j = one; j <= nd2; ++j)
                result[i][j] = P3.getValue(i - nd2 - 1, j - one) + P4.getValue(i - nd2 - 1, j - one);
        for(int i = nd2 + 1; i <= n; ++i)
            for(int j = nd2 + 1; j <= n; ++j)
                result[i][j] = P5.getValue(i - (nd2 + 1), j - (nd2 + 1)) + P1.getValue(i - (nd2 + 1), j - (nd2 + 1))
                        - P3.getValue(i - (nd2 + 1), j - (nd2 + 1)) - P7.getValue(i - (nd2 + 1), j - (nd2 + 1));

        return new Matrix(result,0,result.length - 1,0, result[0].length - 1);
    }
    @Override
    public boolean equals(Object o)
    {
        Matrix m =(Matrix) o;
        if(m.row_size != row_size || m.column_size != column_size)
            return false;
        for(int i = 0; i < m.row_size; ++i)
            for(int j = 0; j < m.column_size; ++j)
                if(getValue(i, j) != m.getValue(i, j))
                    return false;
        return true;
    }

    public static Matrix winograd(Matrix a, Matrix b)
    {
        int[][] result = new int[a.row_size][a.column_size];
        if(a.row_size == 1)
        {
           result[0][0] = a.getValue(0, 0) * b.getValue(0, 0);
            return new Matrix(result,0,result.length - 1,0, result[0].length - 1);
        }

        int n = a.row_e;
        int nd2 = (a.row_e + a.row_s) / 2;
        int one = a.row_s;

        Matrix A11 = new Matrix(a.value, one, nd2, one, nd2);
        Matrix A12 = new Matrix(a.value, one, nd2, nd2 + 1, n);
        Matrix A21 = new Matrix(a.value, nd2 + 1, n, one, nd2);
        Matrix A22 = new Matrix(a.value, nd2 + 1, n, nd2 + 1,n);

        n = b.row_e;
        nd2 = (b.row_e + b.row_s) / 2;
        one = b.row_s;

        Matrix B11 = new Matrix(b.value, one, nd2, one, nd2);
        Matrix B12 = new Matrix(b.value, one, nd2, nd2 + 1, n);
        Matrix B21 = new Matrix(b.value, nd2 + 1, n, one, nd2);
        Matrix B22 = new Matrix(b.value, nd2 + 1, n, nd2 + 1,n);

        Matrix S1 = Matrix.add(A21, A22);
        Matrix S2 = Matrix.subtract(S1, A11);
        Matrix S3 = Matrix.subtract(A11, A21);
        Matrix S4 = Matrix.subtract(A12, S2);
        Matrix S5 = Matrix.subtract(B12, B11);
        Matrix S6 = Matrix.subtract(B22, S5);
        Matrix S7 = Matrix.subtract(B22, B12);
        Matrix S8 = Matrix.subtract(S6, B21);

        Matrix P1 = Matrix.multiply(S2, S6);
        Matrix P2 = Matrix.multiply(A11, B11);
        Matrix P3 = Matrix.multiply(A12, B21);
        Matrix P4 = Matrix.multiply(S3, S7);
        Matrix P5 = Matrix.multiply(S1, S5);
        Matrix P6 = Matrix.multiply(S4, B22);
        Matrix P7 = Matrix.multiply(A22, S8);

        Matrix T1 = Matrix.add(P1, P2);
        Matrix T2 = Matrix.add(T1, P4);


        one = 0;
        nd2 = (result.length - 1) / 2;
        n = result.length - 1;
        for(int i = one; i <= nd2; ++i)
            for (int j = one; j <= nd2; ++j)
                result[i][j] = P2.getValue(i - one, j - one) + P3.getValue(i - one, j - one);

        for(int i = one; i <= nd2; ++i)
            for (int j = nd2 + 1; j <=n; ++j)
                result[i][j] = T1.getValue(i - one, j - nd2 - 1) + P5.getValue(i - one, j - nd2 - 1)
                        + P6.getValue(i - one, j - nd2 - 1);


        for(int i = nd2 + 1; i <= n; ++i)
            for(int j = one; j <= nd2; ++j)
                result[i][j] = T2.getValue(i - nd2 - 1, j - one) - P7.getValue(i - nd2 - 1, j - one);


        for(int i = nd2 + 1; i <= n; ++i)
            for(int j = nd2 + 1; j <= n; ++j)
                result[i][j] = T2.getValue(i - nd2 - 1, j - nd2 - 1) + P5.getValue(i - nd2 - 1, j - nd2 - 1);

        return new Matrix(result,0,result.length - 1,0, result[0].length - 1);
    }
    @Override
    public Matrix clone()
    {
        int result[][] = new int[this.getRow_size()][this.getColumn_size()];
        for (int i = row_s; i <= row_e; ++i)
            System.arraycopy(value[i], column_s, result[i - row_s], 0, column_size);
        return new Matrix(result,0,result.length - 1,0, result[0].length - 1);
    }
    public void setValue(int i, int j, int value)
    {
        this.value[i + this.row_s][j + this.column_s] = value;
    }
}
