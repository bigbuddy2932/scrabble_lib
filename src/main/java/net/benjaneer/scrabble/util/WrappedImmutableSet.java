package net.benjaneer.scrabble.util;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.function.Predicate;

/**
 * Generic set implementation with mutation methods marked as unsupported
 * Requires at least one element
 */
public abstract class WrappedImmutableSet<T> implements Set<T> {

    private final Set<T> wrapped;
    private final int size;

    protected WrappedImmutableSet(Set<T> wrapped) {
        if(wrapped == null || wrapped.isEmpty()) {
            throw new IllegalArgumentException("Set is null or empty");
        }
        this.wrapped = wrapped;
        size = wrapped.size();
    }

    @Override
    @Deprecated
    public final boolean removeIf(Predicate<? super T> filter) {
        throw new UnsupportedOperationException("Set is immutable");
    }

    @Override
    @Deprecated
    public final boolean add(T t) {
        throw new UnsupportedOperationException("Set is immutable");
    }

    @Override
    @Deprecated
    public final boolean remove(Object o) {
        throw new UnsupportedOperationException("Set is immutable");
    }

    @Override
    @Deprecated
    public final boolean addAll(Collection<? extends T> c) {
        throw new UnsupportedOperationException("Set is immutable");
    }

    @Override
    @Deprecated
    public final boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException("Set is immutable");
    }

    @Override
    @Deprecated
    public final boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException("Set is immutable");
    }

    @Override
    @Deprecated
    public final void clear() {
        throw new UnsupportedOperationException("Set is immutable");
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public boolean contains(Object o) {
        return wrapped.contains(o);
    }

    @Override
    public Iterator<T> iterator() {
        return wrapped.iterator();
    }

    @Override
    public Object[] toArray() {
        return wrapped.toArray();
    }

    @Override
    public <t> t[] toArray(t[] a) {
        return wrapped.toArray(a);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        for(Object o : c) {
            if(!contains(o)) {
                return false;
            }
        }
        return true;
    }
}
