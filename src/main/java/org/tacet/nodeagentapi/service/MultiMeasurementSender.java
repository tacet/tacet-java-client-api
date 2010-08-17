package org.tacet.nodeagentapi.service;

import org.tacet.nodeagentapi.Root;

import java.util.List;

/**
 * @author <a href="mailto:thor.aage.eldby@arktekk.no">Thor Åge Eldby (teldby)</a>
 */
public class MultiMeasurementSender implements MeasurementSender {

    private List<MeasurementSender> measurementSenders;

    public MultiMeasurementSender(List<MeasurementSender> measurementSenders) {
        this.measurementSenders = measurementSenders;
    }

    @Override
    public void send(Root root) {
        for (MeasurementSender measurementSender : measurementSenders) {
            measurementSender.send(root);
        }
    }

}
