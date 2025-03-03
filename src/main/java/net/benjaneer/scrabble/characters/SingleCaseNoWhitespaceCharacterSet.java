package net.benjaneer.scrabble.characters;

import java.util.regex.Pattern;

public class SingleCaseNoWhitespaceCharacterSet extends CharacterSet {

    public static final SingleCaseNoWhitespaceCharacterSet ENGLISH_CHARSET = new SingleCaseNoWhitespaceCharacterSet("ABCDEFGHIJKLMNOPQRSTUVWXYZ");

    private static final String WHITESPACE_PATTERN_STRING = "\\s";
    private static final Pattern WHITESPACE_PATTERN = Pattern.compile(WHITESPACE_PATTERN_STRING);

    public SingleCaseNoWhitespaceCharacterSet(String characters) {
        super(characters.toUpperCase().replaceAll(WHITESPACE_PATTERN_STRING, ""));
    }

    protected String attemptTransform(String word) {
        String transformedWord = word.toUpperCase();
        if(WHITESPACE_PATTERN.matcher(transformedWord).find()) {
            return null;
        }
        return transformedWord;
    }

    @Override
    public boolean contains(Object o) {
        if (!(o instanceof Character)) {
            return false;
        }
        return super.contains(Character.toUpperCase((Character) o));
    }
}
