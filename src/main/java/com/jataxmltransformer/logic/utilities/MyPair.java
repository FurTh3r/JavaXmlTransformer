package com.jataxmltransformer.logic.utilities;

/**
 * A generic pair class for storing two related objects.
 *
 * @param <T> The type of the first element.
 * @param <E> The type of the second element.
 */
public class MyPair<T, E> {
    private T first;
    private E second;

    /**
     * Constructs a new pair with the specified values.
     *
     * @param first  The first element of the pair.
     * @param second The second element of the pair.
     */
    public MyPair(T first, E second) {
        this.first = first;
        this.second = second;
    }

    /**
     * Returns the first element of the pair.
     *
     * @return The first element.
     */
    public T getFirst() {
        return first;
    }

    /**
     * Sets the first element of the pair.
     *
     * @param first The new value for the first element.
     */
    public void setFirst(T first) {
        this.first = first;
    }

    /**
     * Returns the second element of the pair.
     *
     * @return The second element.
     */
    public E getSecond() {
        return second;
    }

    /**
     * Sets the second element of the pair.
     *
     * @param second The new value for the second element.
     */
    public void setSecond(E second) {
        this.second = second;
    }

    /**
     * Returns a string representation of the pair in the format "first - second".
     *
     * @return A string representation of the pair.
     */
    @Override
    public String toString() {
        return first + " - " + second;
    }
}