package net.benjaneer.scrabble.words;

import java.util.Arrays;

public record Word(char[] chars) {

    @Override
    public int hashCode() {
        return Arrays.hashCode(chars);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o instanceof Word) {
            return compareChars(chars, ((Word) o).chars);
        }
        if (o instanceof char[]) {
            return compareChars(chars, ((char[]) o));
        }
        if (o instanceof String) {
            return compareChars(chars, ((String) o).toCharArray());
        }
        return false;
    }

    @Override
    public String toString() {
        return new String(chars);
    }

    private boolean compareChars(char[] a, char[] b) {
        if(a.length != b.length) {
            return false;
        }
        for(int i = 0; i < a.length; i++) {
            if(a[i] != b[i]) {
                return false;
            }
        }
        return true;
    }
}
