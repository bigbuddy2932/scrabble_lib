package net.benjaneer.scrabble.util;

import org.junit.jupiter.api.Assertions;

import java.util.Set;

public class WrappedImmutableSetValidator {

    public static void validateWrappedImmutableSet(WrappedImmutableSet<?> set) {
        Assertions.assertThrows(UnsupportedOperationException.class, () -> set.removeIf(x -> x == null));
        Assertions.assertThrows(UnsupportedOperationException.class, () -> set.remove(0));
        Assertions.assertThrows(UnsupportedOperationException.class, () -> set.retainAll(Set.of(new Object(){})));
        Assertions.assertThrows(UnsupportedOperationException.class, () -> set.removeAll(Set.of(new Object(){})));
        Assertions.assertThrows(UnsupportedOperationException.class, set::clear);
    }
}
