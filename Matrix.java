package csce6231.bored.util;

import java.io.Serializable;

public abstract class Matrix implements Serializable, Cloneable {
	private static final long serialVersionUID = 1L;

	public static Matrix create(int rows, int columns) {
		return create(rows, columns, 0.0);
	}
	
	public static Matrix create(int rows, int columns, double defaultValue) {
		return create(rows, columns, new ValueProvider() {
			@Override
			public double getValue() {
				return 0.0;
			}
		});
	}
	
	public static Matrix create(int rows, int columns, ValueProvider valueSource) {
		validateRowCount(rows);
		validateColCount(columns);
		
		Matrix m;
		
		if (columns == 1) {
			m = new MatrixColumn(rows);
			
		} else if (rows == 1) {
			m = new MatrixRow(columns);
			
		} else {
			m = new Matrix2D(rows, columns);
		}
		
		for (int r = 0; r < rows; r++) {
			for (int c = 0; c < columns; c++) {
				m.set(r, c, valueSource.getValue());
			}
		}
		
		return m;
	}
	
	public static Matrix create(double[] values) {
		return create(values, false);
	}
	
	public static Matrix create(double[] values, boolean transposed) {
		if (transposed) {
			validateColCount(values.length);
			return new MatrixRow(values);
			
		} else {
			validateRowCount(values.length);
			return new MatrixColumn(values);
		}
	}
	
	public static Matrix create(double[][] values) {
		validateRowCount(values.length);
		validateColCount(values[0].length);
		return new Matrix2D(values);
	}
	
	public abstract int getRows();
	public abstract int getColumns();
	
	public abstract double get(int row, int col);
	public abstract void set(int row, int col, double value);
	
	public abstract double[] getRow(int row);
	public abstract double[] getColumn(int col);
	
	protected abstract Matrix createSimilar();
	protected abstract Matrix createTransposed();
	
	public Matrix subMatrix(int startRow, int rows, int startCol, int columns) {
		return new SubMatrix(this, startRow, rows, startCol, columns);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Matrix) {
			Matrix m = (Matrix) obj;
			if (this.getRows() == m.getRows() && this.getColumns() == m.getColumns()) {
				for (int r = 0; r < this.getRows(); r++) {
					for (int c = 0; c < this.getColumns(); c++) {
						if (this.get(r, c) != m.get(r, c)) { return false; }
					}
				}
				return true;
				
			} else {
				return false;
			}
			
		} else {
			return false;
		}
	}
	
	@Override
	public int hashCode() {
		int hash = 0;
		for (int r = 0; r < getRows(); r++) {
			for (int c = 0; c < getColumns(); c++) {
				hash += new Double(get(r, c)).hashCode();
			}
		}
		return hash;
	}
	
	@Override
	public String toString() {
		StringBuilder s = new StringBuilder();
		
		s.append("[");
		String rowComma = "";
		
		for (int r = 0; r < getRows(); r++) {
			s.append(rowComma);
			rowComma = ",";
			
			s.append("[");
			String colComma = "";

			for (int c = 0; c < getColumns(); c++) {
				s.append(colComma);
				colComma = ",";
				
				s.append(get(r, c));
			}
			s.append("]");
		}
		
		s.append("]");
		
		return s.toString();
	}
	
	@Override
	public Matrix clone() {
		return this.transform(new ValueTransformer() {
			@Override
			public double apply(double value) {
				return value;
			}
		});
	}
	
	public Matrix transpose() {
		Matrix m = createTransposed();
		
		for (int r = 0; r < getRows(); r++) {
			for (int c = 0; c < getColumns(); c++) {
				m.set(c, r, get(r, c));
			}
		}
		
		return m;
	}
	
	public Matrix diag() {
		Matrix m;
		
		if (getRows() == 1) {
			m = Matrix.create(getColumns(), getColumns(), 0.0);
			for (int c = 0; c < m.getRows(); c++) {
				m.set(c, c, get(0, c));
			}
			
		} else {
			m = Matrix.create(getRows(), getRows(), 0.0);
			for (int r = 0; r < m.getRows(); r++) {
				m.set(r, r, get(r, 0));
			}
		}
		
		return m;
	}
	
	public Matrix add(Matrix m) {
		validateSize(m, "addition");
		
		Matrix result = createSimilar();
		
		for (int r = 0; r < this.getRows(); r++) {
			for (int c = 0; c < this.getColumns(); c++) {
				result.set(r, c, this.get(r, c) + m.get(r, c));
			}
		}
		
		return result;
	}
	
	public Matrix subtract(Matrix m) {
		validateSize(m, "subtraction");
		
		Matrix result = createSimilar();
		
		for (int r = 0; r < this.getRows(); r++) {
			for (int c = 0; c < this.getColumns(); c++) {
				result.set(r, c, this.get(r, c) - m.get(r, c));
			}
		}
		
		return result;
	}
	
	public Matrix multiplyElements(Matrix m) {
		validateSize(m, "multiplication");
		
		Matrix result = createSimilar();
		
		for (int r = 0; r < this.getRows(); r++) {
			for (int c = 0; c < this.getColumns(); c++) {
				result.set(r, c, this.get(r, c) * m.get(r, c));
			}
		}
		
		return result;
	}
	
	public Matrix divideElements(Matrix m) {
		validateSize(m, "division");
		
		Matrix result = createSimilar();
		
		for (int r = 0; r < this.getRows(); r++) {
			for (int c = 0; c < this.getColumns(); c++) {
				result.set(r, c, this.get(r, c) / m.get(r, c));
			}
		}
		
		return result;
	}
	
	public Matrix transform(ValueTransformer func) {
		Matrix result = createSimilar();
		for (int r = 0; r < getRows(); r++) {
			for (int c = 0; c < getColumns(); c++) {
				result.set(r, c, func.apply(get(r, c)));
			}
		}
		return result;
	}
	
	public Matrix multiply(Matrix m) {
		if (this.getColumns() != m.getRows()) {
			throw new IllegalArgumentException("Cannot perfom matrix multiplication on " + this + " and " + m);
		}
		
		Matrix result = create(this.getRows(), m.getColumns());
		
		for (int r = 0; r < result.getRows(); r++) {
			for (int c = 0; c < result.getColumns(); c++) {
				double value = 0;
				for (int rc = 0; rc < this.getColumns(); rc++) {
					value += get(r, rc) * m.get(rc, c);
				}
				result.set(r, c, value);
			}
		}
		
		return result;
	}
	
	private static void validateRowCount(int rows) {
		if (rows <= 0) {
			throw new IllegalArgumentException("Matrix row count cannot be zero or negative");
		}
	}
	
	private static void validateColCount(int cols) {
		if (cols <= 0) {
			throw new IllegalArgumentException("Matrix column count cannot be zero or negative");
		}
	}
	
	private void validateSize(Matrix m, String operation) {
		if (m.getRows() != this.getRows() || m.getColumns() != this.getColumns()) {
			throw new IllegalArgumentException("Cannot perform " + operation + " on " + this + " and " + m);
		}
	}
	
	private static class MatrixColumn extends Matrix {
		private static final long serialVersionUID = 1L;

		private double[] values;
		
		public MatrixColumn(int rows) {
			this.values = new double[rows];
		}
		
		public MatrixColumn(double[] values) {
			this.values = values;
		}

		@Override
		public int getRows() {
			return values.length;
		}

		@Override
		public int getColumns() {
			return 1;
		}

		@Override
		public double get(int row, int col) {
			if (col != 0) { throw new IllegalArgumentException("Invalid column: " + col); }
			return values[row];
		}

		@Override
		public void set(int row, int col, double value) {
			if (col != 0) { throw new IllegalArgumentException("Invalid column: " + col); }
			values[row] = value;
		}
		
		@Override
		public double[] getRow(int row) {
			return new double[] { values[row] };
		}
		
		@Override
		public double[] getColumn(int col) {
			if (col != 0) { throw new IllegalArgumentException("Invalid column: " + col); }
			return values;
		}

		@Override
		protected Matrix createSimilar() {
			return new MatrixColumn(values.length);
		}

		@Override
		protected Matrix createTransposed() {
			return new MatrixRow(values.length);
		}
	}
	
	private static class MatrixRow extends Matrix {
		private static final long serialVersionUID = 1L;

		private double[] values;
		
		public MatrixRow(int columns) {
			this.values = new double[columns];
		}
		
		public MatrixRow(double[] values) {
			this.values = values;
		}

		@Override
		public int getRows() {
			return 1;
		}

		@Override
		public int getColumns() {
			return values.length;
		}

		@Override
		public double get(int row, int col) {
			if (row != 0) { throw new IllegalArgumentException("Invalid row: " + row); }
			return values[col];
		}

		@Override
		public void set(int row, int col, double value) {
			if (row != 0) { throw new IllegalArgumentException("Invalid row: " + row); }
			values[col] = value;
		}
		
		@Override
		public double[] getRow(int row) {
			if (row != 0) { throw new IllegalArgumentException("Invalid row: " + row); }
			return values;
		}
		
		@Override
		public double[] getColumn(int col) {
			return new double[] { values[col] };
		}

		@Override
		protected Matrix createSimilar() {
			return new MatrixRow(values.length);
		}

		@Override
		protected Matrix createTransposed() {
			return new MatrixColumn(values.length);
		}
	}
	
	private static class Matrix2D extends Matrix {
		private static final long serialVersionUID = 1L;

		private double[][] values;
		
		public Matrix2D(int rows, int columns) {
			this.values = new double[rows][columns];
		}
		
		public Matrix2D(double[][] values) {
			this.values = values;
		}

		@Override
		public int getRows() {
			return values.length;
		}

		@Override
		public int getColumns() {
			return values[0].length;
		}

		@Override
		public double get(int row, int col) {
			return values[row][col];
		}

		@Override
		public void set(int row, int col, double value) {
			values[row][col] = value;
		}
		
		@Override
		public double[] getRow(int row) {
			return values[row];
		}
		
		@Override
		public double[] getColumn(int col) {
			double[] column = new double[values.length];
			for (int r = 0; r < values.length; r++) {
				column[r] = values[r][col];
			}
			return column;
		}

		@Override
		protected Matrix createSimilar() {
			return new Matrix2D(getRows(), getColumns());
		}

		@Override
		protected Matrix createTransposed() {
			return new Matrix2D(getColumns(), getRows());
		}
	}
	
	private static class SubMatrix extends Matrix {
		private static final long serialVersionUID = 1L;

		private final Matrix original;
		private final int startRow;
		private final int rows;
		private final int startCol;
		private final int columns;
		
		public SubMatrix(Matrix original, int startRow, int rows, int startCol, int columns) {
			this.original = original;
			this.startRow = startRow;
			this.rows = rows;
			this.startCol = startCol;
			this.columns = columns;
		}

		@Override
		public int getRows() {
			return rows;
		}

		@Override
		public int getColumns() {
			return columns;
		}

		@Override
		public double get(int row, int col) {
			return original.get(startRow + row, startCol + col);
		}

		@Override
		public void set(int row, int col, double value) {
			original.set(startRow + row, startCol + col, value);
		}

		@Override
		public double[] getRow(int row) {
			if (columns == original.getColumns()) { return original.getRow(startRow + row); }
			else {
				double[] values = new double[columns];
				for (int c = 0; c < columns; c++) { values[c] = get(row, c); }
				return values;
			}
		}

		@Override
		public double[] getColumn(int col) {
			if (rows == original.getRows()) { return original.getColumn(startCol + col); }
			else {
				double[] values = new double[rows];
				for (int r = 0; r < rows; r++) { values[r] = get(r, col); }
				return values;
			}
		}

		@Override
		protected Matrix createSimilar() {
			return Matrix.create(rows, columns);
		}

		@Override
		protected Matrix createTransposed() {
			return Matrix.create(columns, rows);
		}
	}

	public static interface ValueProvider {
		public double getValue();
	}

	public static interface ValueTransformer {
		public double apply(double value);
	}
}
