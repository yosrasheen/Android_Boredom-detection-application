package csce6231.bored.net.func;

import java.util.Arrays;
import java.util.function.DoubleUnaryOperator;

import csce6231.bored.util.Matrix;

public abstract class ActivationFunction extends Function {

	public static final String TANH = "tanh";

	public static ActivationFunction get(String name) {
		return FUNCTIONS[0];
	}
	
	private static final ActivationFunction[] FUNCTIONS = {
		
		new ActivationFunction() {
			
			@Override
			public String getName() {
				return TANH;
			}
			
			@Override
			public Matrix apply(Matrix x) {
				return x.transform(new Matrix.ValueTransformer() {
					@Override
					public double apply(double operand) {
						return Math.tanh(operand);
					}
				});
			}
			
			@Override
			public Matrix applyDerivative(Matrix x) {
				return apply(x).transform(new Matrix.ValueTransformer() {
					@Override
					public double apply(double value) {
						return 1 - value*value;
					}
				});
			}
		}
	};
}
