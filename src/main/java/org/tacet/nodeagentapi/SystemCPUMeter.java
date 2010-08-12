package org.tacet.nodeagentapi;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;

/**
 * @author <a href="mailto:thor.aage.eldby@arktekk.no">Thor Åge Eldby (teldby)</a>
 */
@SuppressWarnings({"UnusedDeclaration"})
public class SystemCPUMeter implements Meter {
    
    private final OperatingSystemMXBean mxbean;

    public SystemCPUMeter() {
        mxbean = ManagementFactory.getOperatingSystemMXBean();
    }

    @Override
    public Measurement measure() {
        return ValueMeasurement.newInstance("cpu-load", "system", mxbean.getSystemLoadAverage());
    }

}
