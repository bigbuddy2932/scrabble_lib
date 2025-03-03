package net.benjaneer.scrabble;

import net.benjaneer.scrabble.words.CrosswordBoard;
import net.benjaneer.scrabble.words.CrosswordSquareSolver;
import net.benjaneer.scrabble.words.WordList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class Main {

    private static final Logger LOGGER = LogManager.getLogger(Main.class);
    private static final String FILE_PATTERN = "output/WORD_SQUARE_SIZE_%d_%d.txt";

    public static void main(String[] args) {
        CrosswordInput input = new CrosswordInput(args);

        if(!input.doWrite) {
            LOGGER.warn("Will not write to file");
        }

        Set<CrosswordBoard> crossword = CrosswordSquareSolver.findLargestPossibleCrosswordSquare(WordList.scrabble2019List(), input.lengthOrder);

        if(crossword == null || crossword.isEmpty()) {
            LOGGER.info("No crossword found");
            return;
        }

        int length = crossword.stream().findFirst().get().crossword.length;
        LOGGER.info("Found {} crosswords of size {}.", crossword.size(), length);

        if(input.doWrite) {
            String fileName = String.format(FILE_PATTERN, length, System.currentTimeMillis());
            LOGGER.info("Writing file {}", fileName);
            try (FileWriter writer = new FileWriter(fileName)) {
                for(CrosswordBoard crosswordBoard : crossword) {
                    writer.write(crosswordBoard.toString());
                    writer.write("\n");
                }
            } catch (IOException e) {
                LOGGER.error("Issue writing file", e);
                throw new RuntimeException(e);
            }
        }
    }

    private static final class CrosswordInput {

        public final boolean doWrite;
        public final List<Integer> lengthOrder;

        public CrosswordInput(String[] args) {
            List<String> argSet = Arrays.stream(args).toList();

            doWrite = !argSet.contains("-noWrite");

            lengthOrder = argSet.stream().map(x -> {
                        try {
                            return Integer.parseInt(x);
                        } catch (Exception e) {
                            return null;
                        }
                    }
            ).filter(Objects::nonNull).toList();
        }
    }
}