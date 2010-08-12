package org.tacet.nodeagentapi.service;

import org.apache.log4j.Logger;
import org.tacet.nodeagentapi.Root;

/**
 * @author <a href="mailto:thor.aage.eldby@arktekk.no">Thor Åge Eldby (teldby)</a>
 */
public class LogMeasurementSender implements MeasurementSender {

    private static final Logger logger = Logger.getLogger(LogMeasurementSender.class);

    @Override
    public void send(Root root) {
        logger.info(root);
    }

}
