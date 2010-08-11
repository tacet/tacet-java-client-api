package org.tacet.nodeagentapi;

/**
 * @author <a href="mailto:thor.aage.eldby@arktekk.no">Thor Åge Eldby (teldby)</a>
 */
abstract public class Measurement {

    abstract public String getKind();

    abstract public Object getValue();

}
