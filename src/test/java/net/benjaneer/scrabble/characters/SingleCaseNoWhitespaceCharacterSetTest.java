package net.benjaneer.scrabble.characters;

import net.benjaneer.scrabble.util.WrappedImmutableSetValidator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.Set;

import static net.benjaneer.scrabble.characters.SingleCaseNoWhitespaceCharacterSet.ENGLISH_CHARSET;

public class SingleCaseNoWhitespaceCharacterSetTest {

    private static final SingleCaseNoWhitespaceCharacterSet FILTER_SET_TEST = new SingleCaseNoWhitespaceCharacterSet("A B");
    private static final Set<SingleCaseNoWhitespaceCharacterSet> SETS_TO_TEST = Set.of(ENGLISH_CHARSET, FILTER_SET_TEST);

    @Test
    public void containsTest() {
        for(SingleCaseNoWhitespaceCharacterSet set : SETS_TO_TEST) {
            Assertions.assertTrue(set.contains('a'));
            Assertions.assertTrue(set.contains('A'));
            Assertions.assertTrue(set.contains(Character.valueOf('a')));
            Assertions.assertTrue(set.contains(Character.valueOf('A')));

            Assertions.assertFalse(set.contains(' '));
            Assertions.assertFalse(set.contains(Character.valueOf(' ')));
            Assertions.assertFalse(set.contains('_'));
            Assertions.assertFalse(set.contains(Character.valueOf('-')));
            Assertions.assertFalse(set.contains('\n'));
            Assertions.assertFalse(set.contains(Character.valueOf('\n')));

            Assertions.assertTrue(set.containsAll(Set.of('a', 'A')));
            Assertions.assertTrue(set.containsAll(Set.of(Character.valueOf('a'), Character.valueOf('A'))));

            Assertions.assertFalse(set.containsAll(Set.of(' ', 'A')));
            Assertions.assertFalse(set.containsAll(Set.of(Character.valueOf(' '), Character.valueOf('A'))));

            Assertions.assertFalse(set.contains(null));
            Assertions.assertFalse(set.contains(new Date()));
        }
    }

    @Test
    public void wordConformsTest() {
        for(SingleCaseNoWhitespaceCharacterSet set : SETS_TO_TEST) {
            Assertions.assertFalse(set.wordConforms("potato "));
            Assertions.assertFalse(set.wordConforms("pot ato"));
            Assertions.assertFalse(set.wordConforms("POTATO "));
            Assertions.assertFalse(set.wordConforms("POT ATO"));
        }

        Assertions.assertTrue(ENGLISH_CHARSET.wordConforms("potato"));
        Assertions.assertTrue(ENGLISH_CHARSET.wordConforms("POTATO"));

        Assertions.assertTrue(FILTER_SET_TEST.wordConforms("abba"));
        Assertions.assertTrue(FILTER_SET_TEST.wordConforms("ABBA"));
    }

    @Test
    public void transformTest() {
        for(SingleCaseNoWhitespaceCharacterSet set : SETS_TO_TEST) {
            Assertions.assertEquals("ABBA", set.transform("ABBA"));
            Assertions.assertEquals("ABBA", set.transform("abba"));

            Assertions.assertNull(set.transform(null));
            Assertions.assertNull(set.transform("ab  ba"));
        }
    }

    @Test
    public void immutableTest() {
        for(SingleCaseNoWhitespaceCharacterSet set : SETS_TO_TEST) {
            WrappedImmutableSetValidator.validateWrappedImmutableSet(set);
        }
    }
}
