package net.benjaneer.scrabble.words;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CrosswordSquareSolverTest {

    private static final WordList TEST_WORD_LIST = WordList.fromArray("IM", "IH", "ME", "HE");

    // IH
    // ME

    // IM
    // HE

    @Test
    public void findLargestPossibleCrosswordSquareTest() {
        Set<CrosswordBoard> boardSet = CrosswordSquareSolver.findLargestPossibleCrosswordSquare(TEST_WORD_LIST);
        Assertions.assertEquals(2, boardSet.size());
        boardSet.forEach(this::validateNoRepeatedWords);

        boardSet = CrosswordSquareSolver.findLargestPossibleCrosswordSquare(WordList.scrabble2019List(), List.of(3));
        Assertions.assertEquals(9187509, boardSet.size());
        boardSet.forEach(this::validateNoRepeatedWords);
    }

    private void validateNoRepeatedWords(CrosswordBoard crosswordBoard) {
        Set<String> words = new HashSet<>();
        for(char[] row : crosswordBoard.crossword) {
            String word = new String(row);
            if(words.contains(word)) {
                Assertions.fail("Duplicate word: " + word + " in " + crosswordBoard);
            } else {
                words.add(word);
            }
        }
    }
}
