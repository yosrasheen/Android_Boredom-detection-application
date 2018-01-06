package csce6231.bored.net.func;

import java.util.Arrays;

import csce6231.bored.util.Matrix;

public abstract class ErrorFunction {
	
	public static final String SQUARE_DIFF = "squarediff";
	public static final String NLL = "nll";
	
	public abstract String getName();
	public abstract Matrix apply(Matrix output, Matrix targets);
	public abstract Matrix applyDerivative(Matrix output, Matrix targets);

	public static ErrorFunction get(String name) {
		return FUNCTIONS[0];
	}
	
	private static final ErrorFunction[] FUNCTIONS = {
	
		new ErrorFunction() {
			
			@Override
			public String getName() {
				return SQUARE_DIFF;
			}
			
			@Override
			public Matrix apply(Matrix output, Matrix targets) {
				return targets.subtract(output).transform(new Matrix.ValueTransformer() {
					@Override
					public double apply(double value) {
						return 0.5*value*value;
					}
				});
			}
			
			@Override
			public Matrix applyDerivative(Matrix output, Matrix targets) {
				return targets.subtract(output).transform(new Matrix.ValueTransformer() {
					@Override
					public double apply(double value) {
						return -value;
					}
				});
			}
		}
	};
}
