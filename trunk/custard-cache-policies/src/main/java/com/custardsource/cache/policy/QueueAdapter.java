package com.custardsource.cache.policy;

import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Queue;

/**
 * <p>
 * Wraps any other {@link java.util.Collection} to support the {@link Queue} interface. This relies
 * on the ordering of the {@link Iterator} provided by the inner Collection; if that iterator is
 * non-deterministic in order, or not sensible in some way, then it's the caller's fault and no
 * correspondence will be entered into. If obtaining an iterator is expensive, then that's
 * unfortunate too. Sorry. (Various instanceof checks could be introduced here to optimise in the
 * case of e.g. a {@link List} where we can easily remove an element by its index, but they have not
 * been done as yet).
 * </p>
 * <p>
 * Likewise, the wrapped class <em>should</em> ensure that {@link Collection#add(Object)} adds the
 * object in such a way that it will be the <em>last</em> element retrieved by an immediately
 * subsequent invocation of {@link Collection#iterator()} (it adds at the 'tail'); this is obviously
 * true for e.g. most {@link List} implementations but may not be true for anything else (e.g. most
 * {@link Set} implementations); you use such an object as the 'wrappee' of this class at your
 * peril.
 * </p>
 * <p>
 * This class assumes that the wrapped object's {@link Collection#remove(Object)} removes the
 * instance of the supplied object that would be found first by looping through the Object's
 * {@link Iterator}. This, in turn, relies on the assumption that the wrapped class allows
 * duplicate objects; this class may silently omit duplicates if the wrapped class does so (e.g. is
 * a {@link Set}), which is probably in violation of the {@link Queue} contract (but may still be
 * useful).
 * </p>
 * <p>
 * This class makes no implications about thread-safety, and is not in and of itself thread-safe
 * even if the wrapped class is (single operations on this class may result in multiple operations
 * on the wrapped class, and contain implicit assumptions that the wrapped class will not change in
 * between these invocations).
 * </p>
 * 
 * @author pcowan
 * @param <E>
 *            the type of element held in this queue
 */
public class QueueAdapter<E> implements Queue<E> {
    private final Collection<E> inner;

    public QueueAdapter(Collection<E> inner) {
        if (inner == null) {
            throw new NullPointerException();
        }
        this.inner = inner;
    }

    public E element() {
        if (inner.isEmpty()) {
            throw new NoSuchElementException();
        }
        return inner.iterator().next();
    }

    public boolean offer(E e) {
        try {
            return inner.add(e);
        } catch (UnsupportedOperationException uoe) {
            return false;
        } catch (IllegalStateException ise) {
            return false;
        }
    }

    public E peek() {
        if (inner.isEmpty()) {
            return null;
        }
        return inner.iterator().next();
    }

    public E poll() {
        if (inner.isEmpty()) {
            return null;
        }
        E value = inner.iterator().next();
        inner.remove(value);
        return value;
    }

    public E remove() {
        E value = poll();
        if (value == null) {
            throw new NoSuchElementException();
        }
        return value;
    }
    
    // Everything below this line is just a simple delegation

    public boolean add(E e) {
        return inner.add(e);
    }

    public boolean addAll(Collection<? extends E> c) {
        return inner.addAll(c);
    }

    public void clear() {
        inner.clear();
    }

    public boolean contains(Object o) {
        return inner.contains(o);
    }

    public boolean containsAll(Collection<?> c) {
        return inner.containsAll(c);
    }

    public boolean isEmpty() {
        return inner.isEmpty();
    }

    public Iterator<E> iterator() {
        return inner.iterator();
    }

    public boolean remove(Object o) {
        return inner.remove(o);
    }

    public boolean removeAll(Collection<?> c) {
        return inner.removeAll(c);
    }

    public boolean retainAll(Collection<?> c) {
        return inner.retainAll(c);
    }

    public int size() {
        return inner.size();
    }

    public Object[] toArray() {
        return inner.toArray();
    }

    public <T> T[] toArray(T[] a) {
        return inner.toArray(a);
    }

    @Override
    public String toString() {
        return inner.toString();
    }
}