package net.benjaneer.scrabble.words;

public class CrosswordBoard {
    public char[][] crossword;

    public CrosswordBoard(int length) {
        crossword = new char[length][length];
    }

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
        return false;
    }

    public CrosswordBoard cloneAndAppend(char[] word) {
        CrosswordBoard newBoard = new CrosswordBoard(word.length);
        for(int i = 0; i < word.length; i++) {
            if(crossword[i][0] == 0) {
                System.arraycopy(word, 0, newBoard.crossword[i], 0, word.length);
                break;
            }
            System.arraycopy(crossword[i], 0, newBoard.crossword[i], 0, word.length);
        }
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
