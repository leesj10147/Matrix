import org.jetbrains.annotations.NotNull;

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
    private Matrix(int[][] value, int row_s, int row_e, int column_s, int column_e)//just for inner method
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
        return value[i][j];
    }
    public static Matrix add(Matrix a, Matrix b)
    {
        int[][] result = new int[a.row_size][b.column_size];
        for(int i = b.row_s; i <= a.row_e; ++i)
            for(int j = a.column_s; j <= b.column_e; ++j)
                result[i - a.row_s][j - a.column_s] = a.value[i][j] + b.value[i][j];
        return new Matrix(result, 0, a.row_size - 1, 0, a.column_size - 1);
    }
    public static Matrix subtract(Matrix a, Matrix b)
    {
        int[][] result = new int[a.row_size][b.column_size];
        for(int i = b.row_s; i <= a.row_e; ++i)
            for(int j = a.column_s; j <= b.column_e; ++j)
                result[i - a.row_s][j - a.column_s] = a.value[i][j] - b.value[i][j];
        return new Matrix(result, 0, a.row_size - 1, 0, a.column_size - 1);
    }
}
