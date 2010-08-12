package org.tacet.nodeagentapi;

/**
 * @author <a href="mailto:thor.aage.eldby@arktekk.no">Thor Åge Eldby (teldby)</a>
 */
public interface Meter<T> {

    /**
     * @return can return null if not available (for example measurements of relative values from absolutes must sometimes establish base line)  
     */
    ValueMeasurement<T> measure();

}
