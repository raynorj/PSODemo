import java.util.Random;

public class Particle {
	public Perceptron current, best;
	public double bestScore; //personal best
	public double[] velocity;

	public Particle(int weightCount, double threshold) {
		Random randy = new Random();

		current = new Perceptron(weightCount, threshold); //generate current
		best = new Perceptron(current.getWeights(), current.getThreshold()); //store current into best
		bestScore = 0;

		velocity = new double[current.getWeights().length];
		for (int i = 0; i < velocity.length; i++) {
			velocity[i] = randy.nextDouble();
			if (randy.nextDouble() > 0.5)
				velocity[i] *= -1;
		}
	}
}