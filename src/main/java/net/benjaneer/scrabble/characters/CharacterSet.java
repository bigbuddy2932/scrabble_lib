package net.benjaneer.scrabble.characters;

import net.benjaneer.scrabble.util.WrappedImmutableSet;

import java.util.*;

public abstract class CharacterSet extends WrappedImmutableSet<Character> {

    protected CharacterSet(String characters) {
        this(charArrayToSet(characters.toCharArray()));
    }

    protected CharacterSet(Set<Character> characters) {
        super(characters);
    }

    /**
     * Character set implementations should implement this to handle transform logic
     */
    protected String attemptTransform(String word) {
        return word;
    }

    /**
     * Attempts to transform input word to be in the character set
     * Returns null if transform does not work
     */
    public final String transform(String word) {
        if(word == null) {
            return null;
        }
        word = attemptTransform(word);
        if (word == null || !wordConforms(word)) {
            return null;
        }
        return word;
    }

    /**
     * Check if word conforms with this character set
     */
    public final boolean wordConforms(String word) {
        for(Character character : word.toCharArray()) {
            if(!this.contains(character)) {
                return false;
            }
        }
        return true;
    }

    private static Set<Character> charArrayToSet(char[] characters) {
        Set<Character> characterSet = new HashSet<>();
        for(char character : characters) {
            characterSet.add(character);
        }
        return characterSet;
    }
}
