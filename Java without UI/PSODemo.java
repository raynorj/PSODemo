import java.io.*;
import java.util.Random;
import java.util.Arrays;
import java.util.Scanner;

public class PSODemo {
	public static void main(String[] args) throws FileNotFoundException, Exception {
		//prep particle swarm
		Particle[] swarm = new Particle[30];
		for (int i = 0; i < swarm.length; i++)
			swarm[i] = new Particle(2, 0.5);

		Perceptron globalBest = new Perceptron(swarm[0].current.getWeights(), swarm[0].current.getThreshold());
		double globalBestScore = 0;
		double desiredScore = 0.9;

		double[][] data = GetData("file.txt");
		double[][] inputs = GetInputs(data);
		double[] outputs = GetOutputs(data);

		while (globalBestScore < desiredScore) {
			for (int i = 0; i < swarm.length; i++) {
				double currentScore = 1 - evaluatePerceptron(swarm[i].current, inputs, outputs);

				if (currentScore > swarm[i].bestScore) {
					swarm[i].bestScore = currentScore;
					swarm[i].best = new Perceptron(swarm[i].current.getWeights(), swarm[i].current.getThreshold());
				}

				if (currentScore > globalBestScore) {
					globalBestScore = currentScore;
					globalBest = new Perceptron(swarm[i].current.getWeights(), swarm[i].current.getThreshold());
				}
			}
			
			Random randy = new Random();

			for (int i = 0; i < swarm.length; i++) {
				double[] newWeights = Arrays.copyOf(swarm[i].current.getWeights(), swarm[i].current.getWeights().length);

				for (int j = 0; j < swarm[i].velocity.length; j++) {
					swarm[i].velocity[j] += 0.1 * randy.nextDouble() * (globalBest.getWeights()[j] - swarm[i].current.getWeights()[j])
										  + 0.1 * randy.nextDouble() * (swarm[i].best.getWeights()[j] - swarm[i].current.getWeights()[j]);

					newWeights[j] += swarm[i].velocity[j];
				}

				swarm[i].current = new Perceptron(newWeights, swarm[i].current.getThreshold());
			}
		}

		for (int i = 0; i < globalBest.getWeights().length; i++) 
			System.out.print(globalBest.getWeights()[i] + ", ");
	}

	public static double evaluatePerceptron(Perceptron per, double[][] inputs, double[] outputs) throws Exception {
		if (inputs.length != outputs.length) throw new Exception();
		double total = inputs.length;
		double incorrect = 0;

		for (int i = 0; i < inputs.length; i++)
			incorrect += Math.abs(outputs[i] - per.run(inputs[i]));

		return incorrect / total;
	}

	public static double[][] GetData(String filepath) throws FileNotFoundException {
		int y = 0, x = 0;
		boolean XFound = false;

		Scanner preScanner = new Scanner(new File(filepath));
		while (preScanner.hasNextLine()) {
			Scanner lineScanner = new Scanner(preScanner.nextLine());
			lineScanner.useDelimiter(",");

			if (!XFound) {
				while (lineScanner.hasNext()) {
					x++;
					lineScanner.next();
				}
				XFound = true;
			}

			y++;
		}
		preScanner.close();

		double[][] newData = new double[y][x];
		int i = 0, j = 0;

		Scanner fileScanner = new Scanner(new File(filepath));
		while (fileScanner.hasNextLine()) {
			Scanner lineScanner = new Scanner(fileScanner.nextLine());
			lineScanner.useDelimiter(",");

			while (lineScanner.hasNext()) {
				newData[i][j] = Double.parseDouble(lineScanner.next());
				j++;
			}

			j = 0;
			i++;
		}
		fileScanner.close();

		return newData;
	}

	public static double[][] GetInputs(double[][] data) {
		double[][] inputs = new double[data.length][];

		for (int i = 0; i < data.length; i++) {
			inputs[i] = new double[data[i].length-1];
			for (int j = 0; j < inputs[i].length; j++)
				inputs[i][j] = data[i][j];
		}

		return inputs;
	}

	public static double[] GetOutputs(double[][] data) {
		double[] outputs = new double[data.length];
		for (int i = 0; i < data.length; i++)
			outputs[i] = data[i][data[i].length-1];
		return outputs;
	}
}