package csce6231.bored.net.func;

import csce6231.bored.util.Matrix;

public abstract class Function {
	
	public abstract String getName();
	public abstract Matrix apply(Matrix x);
	public abstract Matrix applyDerivative(Matrix x);
}
