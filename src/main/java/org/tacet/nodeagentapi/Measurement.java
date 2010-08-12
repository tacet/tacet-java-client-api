package org.tacet.nodeagentapi;

/**
 * @author <a href="mailto:thor.aage.eldby@arktekk.no">Thor Åge Eldby (teldby)</a>
 */
public interface Measurement<T> {

    String getKind();

    String getName();

    T getValue();

}
