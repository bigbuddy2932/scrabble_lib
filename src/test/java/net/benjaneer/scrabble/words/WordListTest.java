package net.benjaneer.scrabble.words;

import net.benjaneer.scrabble.util.WrappedImmutableSetValidator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class WordListTest {

    @Test
    public void arrayTest() {
        assertConstruction(WordList.fromArray("TEST", "words", "wow wow", "1234"));
    }

    @Test
    public void collectionTest() {
        assertConstruction(WordList.fromCollection(Set.of("TEST", "words", "wow wow", "1234")));
    }

    @Test
    public void fileTest() {
        assertConstruction(WordList.fromFile(new File("src/test/resources/testWordList.txt")));

        Assertions.assertThrows(IllegalArgumentException.class, () -> WordList.fromFile(null));
        Assertions.assertThrows(IllegalArgumentException.class, () -> WordList.fromFile(new File("src/test/resources")));
        Assertions.assertThrows(IllegalArgumentException.class, () -> WordList.fromFile(new File("src/test/resources/iDoNotExist.txt")));
    }

    @Test
    public void lengthMapTest() {
        Map<Integer, WordList> sizeMap = WordList.scrabble2019List().lengthMap();
        int size = 0;
        for(WordList wordList : sizeMap.values()) {
            size += wordList.size();
        }
        Assertions.assertEquals(WordList.scrabble2019List().size(), size);
    }

    private void assertConstruction(WordList testList) {

        Assertions.assertEquals(2, testList.size());

        Assertions.assertTrue(testList.contains(new Word("TEST".toCharArray())));
        Assertions.assertTrue(testList.contains(new Word("WORDS".toCharArray())));

        Assertions.assertFalse(testList.contains(new Word("test".toCharArray())));
        Assertions.assertFalse(testList.contains(new Word("words".toCharArray())));
        Assertions.assertFalse(testList.contains(new Word("wow wow".toCharArray())));
        Assertions.assertFalse(testList.contains(new Word("1234".toCharArray())));
        Assertions.assertFalse(testList.contains(new Date()));

        Assertions.assertFalse(testList.isEmpty());

        WrappedImmutableSetValidator.validateWrappedImmutableSet(testList);
    }
}
