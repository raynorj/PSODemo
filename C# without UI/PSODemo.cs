//This version lacks display functionality

using System;
using System.IO;

public class PSODemo {
	public static void Main(string[] args) {
		//prep particle swarm
		Particle[] swarm = new Particle[30];
		for (int i = 0; i < swarm.Length; i++)
			swarm[i] = new Particle(2, 0.5);

		Perceptron globalBest = new Perceptron(swarm[0].current.Weights, swarm[0].current.Threshold);
		double globalBestScore = 0;
		double desiredScore = 0.9;

		double[][] data = GetData("file.txt");
		double[][] inputs = GetInputs(data);
		double[] outputs = GetOutputs(data);

		while (globalBestScore < desiredScore) {
			for (int i = 0; i < swarm.Length; i++) {
				double currentScore = 1 - evaluatePerceptron(swarm[i].current, inputs, outputs);

				if (currentScore > swarm[i].bestScore) {
					swarm[i].bestScore = currentScore;
					swarm[i].best = new Perceptron(swarm[i].current.Weights, swarm[i].current.Threshold);
				}

				if (currentScore > globalBestScore) {
					globalBestScore = currentScore;
					globalBest = new Perceptron(swarm[i].current.Weights, swarm[i].current.Threshold);
				}
			}
			
			Random randy = new Random();

			for (int i = 0; i < swarm.Length; i++) {
				double[] newWeights = new double[swarm[i].current.Weights.Length];
				Array.Copy(swarm[i].current.Weights, newWeights, swarm[i].current.Weights.Length);

				for (int j = 0; j < swarm[i].velocity.Length; j++) {
					swarm[i].velocity[j] += 0.1 * randy.NextDouble() * (globalBest.Weights[j] - swarm[i].current.Weights[j])
							      + 0.1 * randy.NextDouble() * (swarm[i].best.Weights[j] - swarm[i].current.Weights[j]);

					newWeights[j] += swarm[i].velocity[j];
				}

				swarm[i].current = new Perceptron(newWeights, swarm[i].current.Threshold);
			}
		}

		for (int i = 0; i < globalBest.Weights.Length; i++) 
			Console.Write(globalBest.Weights[i] + ", ");
	}

	public static double evaluatePerceptron(Perceptron per, double[][] inputs, double[] outputs) {
		if (inputs.Length != outputs.Length) throw new Exception();
		double total = inputs.Length;
		double incorrect = 0;

		for (int i = 0; i < inputs.Length; i++)
			incorrect += Math.Abs(outputs[i] - per.run(inputs[i]));

		return incorrect / total;
	}

	public static double[][] GetData(string filepath) {
		//extract training data
		string[] datalines = File.ReadAllLines(filepath);
		string[][] stringData = new string[datalines.Length][];
		for (int i = 0; i < stringData.Length; i++)
			stringData[i] = datalines[i].Split(',');

		//parse training data
		double[][] data = new double[stringData.Length][];
		for (int i = 0; i < data.Length; i++) {
			data[i] = new double[stringData[i].Length];
			for (int j = 0; j < data[i].Length; j++)
				data[i][j] = Double.Parse(stringData[i][j]);
		}

		return data;
	}

	public static double[][] GetInputs(double[][] data) {
		double[][] inputs = new double[data.Length][];

		for (int i = 0; i < data.Length; i++) {
			inputs[i] = new double[data[i].Length-1];
			for (int j = 0; j < inputs[i].Length; j++)
				inputs[i][j] = data[i][j];
		}

		return inputs;
	}

	public static double[] GetOutputs(double[][] data) {
		double[] outputs = new double[data.Length];
		for (int i = 0; i < data.Length; i++)
			outputs[i] = data[i][data[i].Length-1];
		return outputs;
	}
}

public class Particle {
	public Perceptron current, best;
	public double bestScore; //personal best
	public double[] velocity;

	public Particle(int weightCount, double threshold) {
		Random randy = new Random();

		current = new Perceptron(weightCount, threshold); //generate current
		best = new Perceptron(current.Weights, current.Threshold); //store current into best
		bestScore = 0;

		velocity = new double[current.Weights.Length];
		for (int i = 0; i < velocity.Length; i++) {
			velocity[i] = randy.NextDouble();
			if (randy.NextDouble() > 0.5)
				velocity[i] *= -1;
		}
	}
}

public class Perceptron {
	private double[] weight;
	public double[] Weights {
		get {
			return weight;
		}
	}
	private double threshold;
	public double Threshold {
		get {
			return threshold;
		}
	}

	public Perceptron(int weightCount, double threshold) {
		Random randy = new Random();

		weight = new double[weightCount];
		for (int i = 0; i < weight.Length; i++)
			weight[i] = randy.NextDouble();
		this.threshold = threshold;
	}

	public Perceptron(double[] weights, double threshold) {
		weight = new double[weights.Length];
		Array.Copy(weights, weight, weights.Length);
		this.threshold = threshold;
	}

	public double run(double[] input) {
		if (input.Length != weight.Length) throw new Exception();

		double sum = 0;
		for (int i = 0; i < weight.Length; i++)
			sum += weight[i] * input[i];

		if (sum > threshold) return 1;
		else return 0;
	}
}