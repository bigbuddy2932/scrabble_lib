package net.benjaneer.scrabble.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class CollectionUtil {

    public static <T> List<T> cloneAndAppend(List<T> collection, T element) {
        List<T> result = new ArrayList<>(collection);
        result.add(element);
        return result;
    }

    public static <T> List<T> collapseList(List<List<T>> lists) {
        return lists.stream().flatMap(Collection::stream).collect(Collectors.toList());
    }
}
