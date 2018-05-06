package utils;

public class MathUtil {
	public static final double D_MIN = 0d;
	public static final double D_MAX = 1d;
	public static final float F_MIN = 0f;
	public static final float F_MAX = 1f;
	
	public static double normalize(double value, double min, double max) {
		if(value >= D_MIN && value <= D_MAX) return value;
		return ((value - D_MIN) / (D_MAX - D_MIN));
	}
	
	public static float normalize(float value) {
		if(value >= F_MIN && value <= F_MAX) return value;
		value = (F_MAX / (F_MAX + value));
	    return value;
	}
}
