package org.tacet.nodeagentapi;

import org.junit.Test;

import static junit.framework.Assert.assertNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author <a href="mailto:thor.aage.eldby@arktekk.no">Thor Åge Eldby (teldby)</a>
 */
public class CallGraphRecorderTest {

    @Test
    public void call_graph_is_recorded() {
        CallGraphRecorder.startRecorder();
        CallMeasurement a = CallGraphRecorder.start("a").withProperty("Hei", "Hopp");
        CallMeasurement b = CallGraphRecorder.start("b").withTag("stupid");
        b.stop();
        CallMeasurement c = CallGraphRecorder.start("c");
        c.stop();
        a.stop();
        CallMeasurement lastCallGraph = CallGraphRecorder.getAndResetLastCallGraph();
        CallMeasurement expectedCallGraph = CallMeasurement.newInstance(1, "a", 0).withProperty("Hei", "Hopp").withChild(CallMeasurement.newInstance(2, "b", 0).withTag("stupid")).withChild(CallMeasurement.newInstance(1, "c", 0));
        assertCallGraphEquals(expectedCallGraph, lastCallGraph);
    }

    private void assertCallGraphEquals(CallMeasurement expectedCallGraph, CallMeasurement callGraph) {
        assertTrue("Expected: " + expectedCallGraph + "; got: " + callGraph, expectedCallGraph.isEqualExceptTimings(callGraph));
    }

    @Test
    public void call_graph_is_recorded_in_per_thread_context() throws Exception {
        CallGraphRecorder.startRecorder();
        CallMeasurement a = CallGraphRecorder.start("a");
        Thread thread = new Thread(new Runnable() {
            public void run() {
                CallMeasurement b = CallGraphRecorder.start("b");
                b.stop();
                CallMeasurement expectedCallGraph = CallMeasurement.newInstance(1, "b", 0);
                CallMeasurement lastCallGraph = CallGraphRecorder.getAndResetLastCallGraph();
                assertCallGraphEquals(expectedCallGraph, lastCallGraph);
            }
        });
        thread.start();
        thread.join(100000);
        a.stop();
        CallMeasurement expectedCallGraph = CallMeasurement.newInstance(1, "a", 0);
        CallMeasurement lastCallGraph = CallGraphRecorder.getAndResetLastCallGraph();
        assertCallGraphEquals(expectedCallGraph, lastCallGraph);
    }

    @Test
    public void call_timings_is_available_recursively() throws Exception {
        CallGraphRecorder.startRecorder();
        CallMeasurement callMeasurement = CallMeasurement.newInstance(1, "a", 90000).withStopNS(90060).withChild(CallMeasurement.newInstance(2, "b", 90020).withStopNS(90040));
        assertEquals(60, callMeasurement.getValue().longValue());
        assertEquals(20, callMeasurement.getChildren().get(0).getValue().longValue());
    }

    @Test
    public void unclosed_measurements_will_be_dropped() {
        CallGraphRecorder.startRecorder();
        CallMeasurement a = CallGraphRecorder.start("a");
        CallGraphRecorder.start("b");
        a.stop();
        assertCallGraphEquals(CallMeasurement.newInstance(1, "a", 0), CallGraphRecorder.getAndResetLastCallGraph());
    }

    @Test
    public void unclosed_measurements_with_children_will_be_dropped_and_the_result_will_be_moved_to_the_parent() {
        CallGraphRecorder.startRecorder();
        CallMeasurement a = CallGraphRecorder.start("a");
        CallGraphRecorder.start("b");
        CallMeasurement c = CallGraphRecorder.start("c");
        c.stop();
        a.stop();
        assertCallGraphEquals(CallMeasurement.newInstance(1, "a", 0).withChild(CallMeasurement.newInstance(3, "c", 0)), CallGraphRecorder.getAndResetLastCallGraph());
    }

    @Test
    public void closing_out_of_order_will_lead_to_dropping_of_the_last_item() {
        CallGraphRecorder.startRecorder();
        CallMeasurement a = CallGraphRecorder.start("a");
        CallMeasurement b = CallGraphRecorder.start("b");
        CallMeasurement c = CallGraphRecorder.start("c");
        b.stop();
        c.stop();
        a.stop();
        assertCallGraphEquals(CallMeasurement.newInstance(1, "a", 0).withChild(CallMeasurement.newInstance(2, "b", 0)), CallGraphRecorder.getAndResetLastCallGraph());
    }

    @Test
    public void closing_multiple_times_will_lead_to_log_warnings() {
        CallGraphRecorder.startRecorder();
        CallMeasurement a = CallGraphRecorder.start("a");
        CallMeasurement b = CallGraphRecorder.start("b");
        b.stop();
        b.stop();
        a.stop();
        assertCallGraphEquals(CallMeasurement.newInstance(1, "a", 0).withChild(CallMeasurement.newInstance(2, "b", 0)), CallGraphRecorder.getAndResetLastCallGraph());
    }

    @Test
    public void recording_calls_without_starting_recorder_should_work_but_not_lead_to_any_result() {
        CallMeasurement a = CallGraphRecorder.start("a").withProperty("hei", "hopp");
        a.stop();
        assertNull(CallGraphRecorder.getAndResetLastCallGraph());
    }

}
