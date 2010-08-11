package org.tacet.nodeagentapi;

import com.google.common.collect.ImmutableMap;
import org.junit.Test;
import org.tacet.nodeagentapi.model.CallMeasurement;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author <a href="mailto:thor.aage.eldby@arktekk.no">Thor Åge Eldby (teldby)</a>
 */
public class CallGraphRecorderTest {

    @Test
    public void call_graph_is_recorded() {
        CallGraphRecorder.start("a", ImmutableMap.of("Hei", "Hopp"));
        CallGraphRecorder.start("b");//TODO:.withTag("stupid");
        CallGraphRecorder.stop("b");
        CallGraphRecorder.start("c");
        CallGraphRecorder.stop("c");
        CallGraphRecorder.stop("a");
        CallMeasurement lastCallGraph = CallGraphRecorder.getAndResetLastCallGraph();
        CallMeasurement expectedCallGraph = CallMeasurement.newInstance("a", 0).withProperty("Hei", "Hopp").withChild(CallMeasurement.newInstance("b", 0)/*.withTag("Stupid")*/).withChild(CallMeasurement.newInstance("c", 0));
        assertCallGraphEquals(expectedCallGraph, lastCallGraph);
    }

    private void assertCallGraphEquals(CallMeasurement expectedCallGraph, CallMeasurement callGraph) {
        assertTrue("Expected: " + expectedCallGraph + "; got: " + callGraph, expectedCallGraph.isEqualExceptTimings(callGraph));
    }

    @Test
    public void call_graph_is_recorded_in_per_thread_context() throws Exception {
        CallGraphRecorder.start("a");
        Thread thread = new Thread(new Runnable() {
            public void run() {
                CallGraphRecorder.start("b");
                CallGraphRecorder.stop("b");
                CallMeasurement expectedCallGraph = CallMeasurement.newInstance("b", 0);
                CallMeasurement lastCallGraph = CallGraphRecorder.getAndResetLastCallGraph();
                assertCallGraphEquals(expectedCallGraph, lastCallGraph);
            }
        });
        thread.start();
        thread.join(100000);
        CallGraphRecorder.stop("a");
        CallMeasurement expectedCallGraph = CallMeasurement.newInstance("a", 0);
        CallMeasurement lastCallGraph = CallGraphRecorder.getAndResetLastCallGraph();
        assertCallGraphEquals(expectedCallGraph, lastCallGraph);
    }

    @Test
    public void call_timings_is_available_recursively() throws Exception {
        CallMeasurement callMeasurement = CallMeasurement.newInstance("a", 90000).withStopNS(90060).withChild(CallMeasurement.newInstance("b", 90020).withStopNS(90040));
        assertEquals(60, callMeasurement.getValue().longValue());
        assertEquals(20, callMeasurement.getChildren().get(0).getValue().longValue());
    }

}
