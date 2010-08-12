package org.tacet.nodeagentapi;

import com.sun.management.OperatingSystemMXBean;
import sun.management.ManagementFactory;


/**
 * @author <a href="mailto:thor.aage.eldby@arktekk.no">Thor Åge Eldby (teldby)</a>
 */
@SuppressWarnings({"UnusedDeclaration"})
public class JvmCpuMeter implements Meter {

    private final OperatingSystemMXBean mxBean;
    private boolean initial = true;
    private long lastNanos;
    private long lastCpuTimeNanos;

    public JvmCpuMeter() {
        mxBean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
    }

    @Override
    public ValueMeasurement<Double> measure() {
        long nanos = System.nanoTime();
        long cpuTimeNanos = mxBean.getProcessCpuTime();
        ValueMeasurement<Double> measurement;
        if (initial || nanos == lastNanos) {
            measurement = null;
            initial = false;
        } else {
            measurement = ValueMeasurement.newInstance("cpu-load", "jvm", ((double) (cpuTimeNanos - lastCpuTimeNanos)) / ((double) (nanos - lastNanos)));
        }
        lastNanos = nanos;
        lastCpuTimeNanos = cpuTimeNanos;
        return measurement;
    }

}
