package org.tacet.nodeagentapi;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;

/**
 * @author <a href="mailto:thor.aage.eldby@arktekk.no">Thor Åge Eldby (teldby)</a>
 */
public class HeapMemoryUsageMeter implements Meter<Double> {

    private final MemoryMXBean mxBean = ManagementFactory.getMemoryMXBean();

    @Override
    public ValueMeasurement<Double> measure() {
        MemoryUsage heapMemoryUsage = mxBean.getHeapMemoryUsage();
        return ValueMeasurement.newInstance("memory-usage", "heap", (double) heapMemoryUsage.getUsed() / (double) heapMemoryUsage.getMax());
    }

}
