package monopolySimulation;

import java.io.FileWriter;
import java.io.IOException;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import Monopoly.GameSpaces.GameSpace;
import edu.princeton.cs.algs4.RedBlackBST;

/**
 * Runs simulations of Monopoly and writes the results to a .csv file.
 * 
 * @author Cristian Morales
 * @author Josh Martin
 *
 */
public class GameSimulationClient {

	private static RedBlackBST<Integer, GameSpace[]> strategyAResults = new RedBlackBST<>();
	private static RedBlackBST<Integer, GameSpace[]> strategyBResults = new RedBlackBST<>();

	public static void main(String[] args) {
		
		final int MAX_TURNS = 1_000_000;

		for (int currentTurns = 1000; currentTurns <= MAX_TURNS; currentTurns *= 10) {

			for (Strategy type : Strategy.values()) {

				for (int trial = 1; trial < 11; trial++) {

					simulateGame(currentTurns, type, trial);
				}

				writeResults(currentTurns, type);
			}
		}
	}

	/**
	 * Simulates a game of Monopoly with {@code maxTurns} turns and using
	 * {@code strategy} while in jail.
	 *
	 * @param maxTurns The maximum number of turns in the game.
	 * @param strategy The strategy the player uses while in jail.
	 * @param trial    The trial number for this simulation.
	 */
	private static void simulateGame(int maxTurns, Strategy strategy, int trial) {

		Game game = new Game(maxTurns, strategy);

		for (int rollCounter = 0; rollCounter < maxTurns; rollCounter++) {
			game.roll();
		}

		if (strategy == Strategy.A) {
			strategyAResults.put(trial, game.getGameBoard());
		} else {
			strategyBResults.put(trial, game.getGameBoard());
		}
	}

	/**
	 * Writes simulation results to a .csv file.
	 * 
	 * @param n        The number of turns taken in each simulation.
	 * @param strategy The strategy whose results are to be printed.
	 */
	private static void writeResults(int n, Strategy strategy) {

		String file = "Results/SimulationResults (Strategy " + strategy.toString() + ", n = " + n + ").csv";
		RedBlackBST<Integer, GameSpace[]> strategyResults = null;

    	try (CSVPrinter csvPrinter = new CSVPrinter(new FileWriter(file), CSVFormat.DEFAULT)) {
    		
    		switch (strategy) {
			case A:
				strategyResults = strategyAResults;
				break;
			case B:
				strategyResults = strategyBResults;
				break;
			}

			csvPrinter.print("Strategy " + strategy);
			csvPrinter.print("n = " + n);
			csvPrinter.println();
			csvPrinter.println();
			
			writeTrials(csvPrinter, strategyResults);
			
        } catch (IOException e) {
            e.printStackTrace();
        }
	}

	/**
	 * Writes the results of all trials in {@code results} with {@code writer}.
	 * 
	 * @param csvPrinter  The object that will write {@code results} to file.
	 * @param results The simulation results that should be printed.
	 * @throws IOException
	 */
	private static void writeTrials(CSVPrinter csvPrinter, RedBlackBST<Integer, GameSpace[]> results) throws IOException {

		for (Integer trial : results.keys()) {

			GameSpace[] currentGameBoard = results.get(trial);
			int totalTimesLanded = 0;

			for (GameSpace el : currentGameBoard) {
				totalTimesLanded += el.getTimesLandedOn();
			}
			
			csvPrinter.printRecord("Trial " + trial);
			csvPrinter.println();
			
			csvPrinter.print("Space Name");
			csvPrinter.print("Times Landed On");
			csvPrinter.print("Percent Landed On (%)");
			csvPrinter.println();
			
			for (GameSpace space : currentGameBoard) {
				
				int timesLandedOn = space.getTimesLandedOn();
				double percentLandedOn = ((double) timesLandedOn) / totalTimesLanded;
				
				csvPrinter.print(space.getName());
				csvPrinter.print(timesLandedOn);
				csvPrinter.print(100*percentLandedOn);
				csvPrinter.println();
			}

			csvPrinter.println();
			csvPrinter.println();
		}

	}

	/**
	 * Test method for writeResults and writeTrials methods.
	 */
	private static void testWriteResults() {
		
		GameSpace[] results = new GameSpace[3];

		SpaceNames[] allSpaceNames = SpaceNames.values();

		for (int i = 0; i < results.length; i++) {
			results[i] = new GameSpace(allSpaceNames[i], 1000);
		}

		strategyAResults.put(1, results);
		strategyAResults.put(2, results);

		writeResults(1000, Strategy.A);
		writeResults(1000, Strategy.B);
	}
}