package net.benjaneer.scrabble.words;

public class CrosswordBoard {
    public char[][] crossword;

    public CrosswordBoard(int length) {
        crossword = new char[length][length];
    }

    public CrosswordBoard(Word rowSeed, Word colSeed) {
        if(rowSeed.chars().length != colSeed.chars().length) {
            throw new IllegalArgumentException();
        }
        int length = rowSeed.chars().length;
        crossword = new char[length][length];
        System.arraycopy(rowSeed.chars(), 0, crossword[0], 0, length);
        for(int i = 1; i < length; i++) {
            crossword[i][0] = colSeed.chars()[i];
        }
    }

    /**
     * Checks if the rows or the first column contain the given word
     */
    public boolean contains(char[] word) {
        for (char[] chars : crossword) {
            boolean found = true;
            for (int j = 0; j < chars.length; j++) {
                if (chars[j] != word[j]) {
                    found = false;
                    break;
                }
            }
            if (found) {
                return true;
            }
        }
        for(int i = 0; i < crossword.length; i++) {
            if(crossword[0][i] != word[i]) {
                return false;
            }
        }
        return true;
    }

    public CrosswordBoard cloneAndAppend(char[] word, int to) {
        CrosswordBoard newBoard = new CrosswordBoard(word.length);
        for(int i = 0; i < word.length; i++) {
            System.arraycopy(crossword[i], 0, newBoard.crossword[i], 0, word.length);
        }
        System.arraycopy(word, 0, newBoard.crossword[to], 0, word.length);
        return newBoard;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (char[] chars : crossword) {
            sb.append(chars);
            sb.append("\n");
        }
        return sb.toString();
    }
}
