package org.tacet.nodeagentapi;

import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * @author <a href="mailto:thor.aage.eldby@arktekk.no">Thor Åge Eldby (teldby)</a>
 */
public class MeterTest {

    @Test
    public void system_cpu_meter_reports_measurements() {
        assertNotNull(new SystemCpuMeter().measure());
    }

    @Test
    public void jvm_cpu_meter_reports_first_null_and_then_measurements() {
        JvmCpuMeter jvmCpuMeter = new JvmCpuMeter();
        assertNull(jvmCpuMeter.measure());
        assertNotNull(jvmCpuMeter.measure());
    }

}
