package org.tacet.nodeagentapi.service;

import org.tacet.nodeagentapi.Measurement;
import org.tacet.nodeagentapi.Meter;
import org.tacet.nodeagentapi.Root;
import org.tacet.nodeagentapi.util.NetworkHelper;

/**
 * @author <a href="mailto:thor.aage.eldby@arktekk.no">Thor Åge Eldby (teldby)</a>
 */
@SuppressWarnings({"UnusedDeclaration"})
public class SchedulableMeasurementReporter {
    private final MeasurementSender measurementSender;
    private final Meter[] meters;

    public SchedulableMeasurementReporter(MeasurementSender measurementSender, Meter... meters) {
        this.measurementSender = measurementSender;
        this.meters = meters;
    }

    public void report() {
        Root root = Root.newInstance(NetworkHelper.getHostIp());
        for (Meter meter : meters) {
            Measurement measurement = meter.measure();
            if (measurement != null) {
                root = root.withMeasurement(measurement);
            }
        }
        measurementSender.send(root);
    }

}
