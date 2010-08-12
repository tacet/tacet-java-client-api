package org.tacet.nodeagentapi.util;

/**
 * @author <a href="mailto:thor.aage.eldby@arktekk.no">Thor Åge Eldby (teldby)</a>
 */
public class Pair<A, B> {

    public final A first;
    public final B second;

    private Pair(A first, B second) {
        this.first = first;
        this.second = second;
    }

    public static <A, B> Pair<A, B> from(A first, B second) {
        return new Pair<A, B>(first, second);
    }

    @Override
    public String toString() {
        return "Pair{" +
                "first=" + first +
                ", second=" + second +
                '}';
    }
}
