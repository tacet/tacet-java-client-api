package org.tacet.nodeagentapi.util;

import java.util.Enumeration;
import java.util.Iterator;

/**
 * @author <a href="mailto:thor.aage.eldby@arktekk.no">Thor Åge Eldby (teldby)</a>
 */
public class EnumerationWrapper<T> implements Iterable<T> {
    private final Enumeration<T> enumeration;

    public EnumerationWrapper(Enumeration<T> enumeration) {
        this.enumeration = enumeration;
    }

    public static <T> Iterable<T> from(Enumeration<T> enumeration) {
        return new EnumerationWrapper<T>(enumeration);
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            @Override
            public boolean hasNext() {
                return enumeration.hasMoreElements();
            }

            @Override
            public T next() {
                return enumeration.nextElement();
            }

            @Override
            public void remove() {
                enumeration.nextElement();
            }
        };
    }
}
