import java.util.Random;
import java.util.Arrays;

public class Perceptron {
	private double[] weight;
	private double threshold;

	public Perceptron(int weightCount, double threshold) {
		Random randy = new Random();

		weight = new double[weightCount];
		for (int i = 0; i < weight.length; i++)
			weight[i] = randy.nextDouble();
		this.threshold = threshold;
	}

	public Perceptron(double[] weights, double threshold) {
		weight = Arrays.copyOf(weights, weights.length);
		this.threshold = threshold;
	}

	public double run(double[] input) throws Exception {
		if (input.length != weight.length) throw new Exception();

		double sum = 0;
		for (int i = 0; i < weight.length; i++)
			sum += weight[i] * input[i];

		if (sum > threshold) return 1;
		else return 0;
	}
	
	public double[] getWeights() {
		return weight;
	}
	
	public double getThreshold() {
		return threshold;
	}
}