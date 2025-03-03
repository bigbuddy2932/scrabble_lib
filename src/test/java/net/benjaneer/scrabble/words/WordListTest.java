package net.benjaneer.scrabble.words;

import net.benjaneer.scrabble.util.WrappedImmutableSetValidator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Date;
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

    @Test
    public void lengthTest() {
        int sum = 0;
        for(char i = 0; i <= 'Z'; i++) {
            Set<Word> words = WordList.scrabble2019List().startsWith(i);
            if(words != null) {
                sum += words.size();
            }
            switch (i) {
                case 'A':
                    Assertions.assertEquals(16202, words.size());
                    break;
                case 'B':
                    Assertions.assertEquals(15235, words.size());
                    break;
                case 'C':
                    Assertions.assertEquals(25042, words.size());
                    break;
                case 'D':
                    Assertions.assertEquals(16620, words.size());
                    break;
                case 'E':
                    Assertions.assertEquals(11334, words.size());
                    break;
                case 'F':
                    Assertions.assertEquals(10634, words.size());
                    break;
                case 'G':
                    Assertions.assertEquals(9353, words.size());
                    break;
                case 'H':
                    Assertions.assertEquals(10524, words.size());
                    break;
                case 'I':
                    Assertions.assertEquals(9604, words.size());
                    break;
                case 'J':
                    Assertions.assertEquals(2311, words.size());
                    break;
                case 'K':
                    Assertions.assertEquals(3395, words.size());
                    break;
                case 'L':
                    Assertions.assertEquals(8058, words.size());
                    break;
                case 'M':
                    Assertions.assertEquals(15826, words.size());
                    break;
                case 'N':
                    Assertions.assertEquals(6568, words.size());
                    break;
                case 'O':
                    Assertions.assertEquals(8895, words.size());
                    break;
                case 'P':
                    Assertions.assertEquals(24346, words.size());
                    break;
                case 'Q':
                    Assertions.assertEquals(1411, words.size());
                    break;
                case 'R':
                    Assertions.assertEquals(15021, words.size());
                    break;
                case 'S':
                    Assertions.assertEquals(31994, words.size());
                    break;
                case 'T':
                    Assertions.assertEquals(14569, words.size());
                    break;
                case 'U':
                    Assertions.assertEquals(9524, words.size());
                    break;
                case 'V':
                    Assertions.assertEquals(4590, words.size());
                    break;
                case 'W':
                    Assertions.assertEquals(5923, words.size());
                    break;
                case 'X':
                    Assertions.assertEquals(309, words.size());
                    break;
                case 'Y':
                    Assertions.assertEquals(1036, words.size());
                    break;
                case 'Z':
                    Assertions.assertEquals(1172, words.size());
                    break;
                default:
                    Assertions.assertNull(words);
            }
        }

        Assertions.assertEquals(WordList.scrabble2019List().size(), sum);
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
