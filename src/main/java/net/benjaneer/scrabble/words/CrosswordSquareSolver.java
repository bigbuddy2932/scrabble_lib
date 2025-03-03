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
            if(length < 2) {
                LOGGER.info("Skipping invalid size {} for crossword square", length);
                continue;
            }
            LOGGER.info("Searching for crossword square with size {}", length);

            try (ExecutorService threadPool = Executors.newFixedThreadPool(64)) {
                WordList wordSizeListPartition = lengthMap.get(length);

                List<Future<Set<CrosswordBoard>>> futures = new ArrayList<>();

                for(WordList wordListPartition : wordSizeListPartition.firstCharacterMap().values()) {
                    List<Word> wordList = wordListPartition.stream().toList();

                    for(int i = 0; i < wordListPartition.size(); i++) {
                        for(int j = i + 1; j < wordListPartition.size(); j++) {
                            CrosswordBoard seededBoard = new CrosswordBoard(wordList.get(i), wordList.get(j));
                            futures.add(threadPool.submit(() -> findNextWords(wordSizeListPartition, length, 1, seededBoard)));
                        }
                    }
                }
                int wordCount = futures.size();

                Set<CrosswordBoard> crosswordBoards = new HashSet<>();
                for(int i = 0; i < wordCount; i++) {
                    LOGGER.info("Waiting on board {} of {} for length {}", i + 1, wordCount, length);
                    try {
                        Set<CrosswordBoard> result = futures.removeFirst().get();
                        if(result != null) {
                            LOGGER.info("Board {} of {} for length {} has {} valid boards", i + 1, wordCount, length, result.size());
                            crosswordBoards.addAll(result);
                        }
                    } catch (ExecutionException | InterruptedException e) {
                        LOGGER.error("Concurrency issue", e);
                    }
                }

                if(!crosswordBoards.isEmpty()) {
                    return crosswordBoards;
                }
            }

            lengthMap.remove(length);
            System.gc();
        }

        return null;
    }

    private static Set<CrosswordBoard> findNextWords(WordList wordList, int length, int depth, CrosswordBoard previousWords) {
        //Recursive exit condition
        Set<CrosswordBoard> solutions = null;
        if(depth == length) {
            solutions = new HashSet<>();
            solutions.add(previousWords);
            return solutions;
        }

        //Generate next set of findNextWords inputs
        Set<Word> startsWithSet = wordList.startsWith(previousWords.crossword[depth][0]);
        int startsWithSize = startsWithSet.size();
        CrosswordBoard[] seededBoards = new CrosswordBoard[startsWithSize];
        Iterator<Word> wordIterator = startsWithSet.iterator();
        for(int i = 0; i < startsWithSize; i++) {
            Word word = wordIterator.next();
            if (!previousWords.contains(word.chars())
                    && couldWork(wordList, length, depth, previousWords, word)) {
                seededBoards[i] = previousWords.cloneAndAppend(word.chars(), depth);
            }
        }

        //Recursive findNextWords call
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
        char[] word = new char[depth + 1];
        for(int i = 0; i < depth; i++) {
            word[i] = previousWords.crossword[i][column];
        }
        word[depth] = nextWord.chars()[column];
        return word;
    }

    private static boolean couldWork(WordList wordList, int length, int depth, CrosswordBoard previousWords, Word nextWord) {
        Word[] prefixes = new Word[length - 1];

        for(int i = 0; i < length - 1; i++) {
            prefixes[i] = new Word(
                    extractColumn(previousWords, i + 1, depth, nextWord)
            );
        }

        if(depth + 1 == length) {
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
            for(Word possibility : possibilities) {
                if(!previousWords.contains(possibility.chars()) && !possibility.equals(nextWord)) {
                    return true;
                }
            }
        }
        return false;
    }
}
