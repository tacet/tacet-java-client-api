package org.tacet.nodeagentapi.service;

import org.tacet.nodeagentapi.Root;

/**
 * @author <a href="mailto:thor.aage.eldby@arktekk.no">Thor Åge Eldby (teldby)</a>
 */
public interface MeasurementSender {

    void send(Root root);
    
}
