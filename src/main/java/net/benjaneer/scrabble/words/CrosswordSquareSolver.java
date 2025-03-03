package net.benjaneer.scrabble.words;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.concurrent.*;

public class CrosswordSquareSolver {

    private static final Logger LOGGER = LogManager.getLogger(CrosswordSquareSolver.class);

    public static Set<CrosswordBoard> findLargestPossibleCrosswordSquare(WordList wordList) {
        Map<Integer, WordList> lengthMap = wordList.lengthMap();

        return findLargestPossibleCrosswordSquare(lengthMap, lengthMap.keySet().stream().sorted().toList().reversed());
    }

    public static Set<CrosswordBoard> findLargestPossibleCrosswordSquare(WordList wordList, List<Integer> lengthOrder) {
        return findLargestPossibleCrosswordSquare(wordList.lengthMap(), lengthOrder);
    }

    private static Set<CrosswordBoard> findLargestPossibleCrosswordSquare(Map<Integer, WordList> lengthMap, List<Integer> lengthOrder) {
        for(Integer length : lengthOrder) {
            LOGGER.info("{} words with {} length", lengthMap.get(length).size(), length);
        }

        for(Integer length : lengthOrder) {
            LOGGER.info("Searching for crossword square with size {}", length);
            WordList wordListPartition = lengthMap.get(length);

            List<Word> wordList = wordListPartition.parallelStream().toList();
            int wordCount = wordList.size();

            Future<Set<CrosswordBoard>>[] futures = new Future[wordCount];

            try (ExecutorService threadPool = Executors.newFixedThreadPool(64)) {

                for(int i = 0; i < wordCount; i++) {
                    CrosswordBoard seededBoard = new CrosswordBoard(length).cloneAndAppend(wordList.get(i).chars());
                    futures[i] = threadPool.submit(() -> findNextWords(wordListPartition, length, 1, seededBoard));
                }

                Set<CrosswordBoard> crosswordBoards = new HashSet<>();
                for(int i = 0; i < wordCount; i++) {
                    LOGGER.info("Waiting on word {} of {} for length {}", i + 1, wordCount, length);
                    try {
                        Set<CrosswordBoard> result = futures[i].get();
                        if(result != null) {
                            crosswordBoards.addAll(result);
                        }
                    } catch (ExecutionException | InterruptedException e) {
                        LOGGER.error("Concurrency issue", e);
                    }
                }

                if(crosswordBoards.size() > 1) {
                    return crosswordBoards;
                }
            }

            lengthMap.remove(length);
            System.gc();
        }

        return null;
    }

    private static Set<CrosswordBoard> findNextWords(WordList wordList, int length, int depth, CrosswordBoard previousWords) {
        Set<CrosswordBoard> solutions = null;
        if(depth == length) {
            solutions = new HashSet<>();
            solutions.add(previousWords);
            return solutions;
        }
        CrosswordBoard[] seededBoards = new CrosswordBoard[wordList.size()];
        Iterator<Word> wordIterator = wordList.iterator();
        for(int i = 0; i < wordList.size(); i++) {
            Word word = wordIterator.next();
            if (!previousWords.contains(word.chars())
                    && couldWork(wordList, length, depth, previousWords, word)) {
                seededBoards[i] = previousWords.cloneAndAppend(word.chars());
            }
        }
        for (CrosswordBoard nextLayer : seededBoards) {
            if(nextLayer != null) {
                Set<CrosswordBoard> potentialSolutions = findNextWords(wordList, length, depth + 1, nextLayer);
                if(potentialSolutions != null) {
                    if(solutions == null) {
                        solutions = potentialSolutions;
                    } else {
                        solutions.addAll(potentialSolutions);
                    }
                }
            }
        }
        return solutions;
    }

    private static char[] extractColumn(CrosswordBoard previousWords, int column, int depth, Word nextWord) {
        int prefixSize = depth + 1;
        char[] word = new char[prefixSize];
        for(int i = 0; i < prefixSize - 1; i++) {
            word[i] = previousWords.crossword[i][column];
        }
        word[prefixSize - 1] = nextWord.chars()[column];
        return word;
    }

    private static boolean couldWork(WordList wordList, int length, int depth, CrosswordBoard previousWords, Word nextWord) {
        Word[] prefixes = new Word[length];

        boolean finalRow = false;

        for(int i = 0; i < length; i++) {
            prefixes[i] = new Word(
                    extractColumn(previousWords, i, depth, nextWord)
            );
        }
        if(depth + 1 == length) {
            finalRow = true;
        }

        if(finalRow) {
            for(Word prefix : prefixes) {
                if(prefix.equals(nextWord)
                        || previousWords.contains(prefix.chars())
                        || !wordList.contains(prefix)) {
                    return false;
                }
            }
            return true;
        }
        for(Word prefix : prefixes) {
            Set<Word> possibilities = wordList.startsWith(prefix);
            if(possibilities.isEmpty()) {
                return false;
            }
            boolean couldWork = false;
            for(Word possibility : possibilities) {
                if(!previousWords.contains(possibility.chars()) && !possibility.equals(nextWord)) {
                    couldWork = true;
                    break;
                }
            }
            if(!couldWork) {
                return false;
            }
        }
        return true;
    }
}
