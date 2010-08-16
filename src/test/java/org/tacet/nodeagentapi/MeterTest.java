package org.tacet.nodeagentapi;

import org.junit.Test;

import java.io.File;
import java.lang.management.ManagementFactory;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * @author <a href="mailto:thor.aage.eldby@arktekk.no">Thor Åge Eldby (teldby)</a>
 */
public class MeterTest {

    @Test
    public void system_cpu_meter_reports_measurements() {
        assertNotNull(new SystemCpuMeter().measure());
    }

    @Test
    public void jvm_cpu_meter_reports_first_null_and_then_positive_measurements() {
        JvmCpuMeter meter = new JvmCpuMeter();
        assertNull(meter.measure());
        Measurement<Double> measurement = meter.measure();
        assertNotNull(measurement);
        assertTrue(measurement.getValue().toString(), measurement.getValue() >= 0.0);
    }

    @Test
    public void free_disk_space_meter_reports_positive_measurements() {
        ValueMeasurement<Long> meter = new FreeDiskSpaceMeter(new File(".")).measure();
        assertNotNull(meter);
        assertTrue(meter.getValue() > 0);
    }

    @Test
    public void memory_usage_is_between_0_and_1() {
        HeapMemoryUsageMeter meter = new HeapMemoryUsageMeter();
        Double value = meter.measure().getValue();
        System.out.println("%: " + value);
        assertTrue(value > .0);
        assertTrue(value <= 1.0);
    }

}
