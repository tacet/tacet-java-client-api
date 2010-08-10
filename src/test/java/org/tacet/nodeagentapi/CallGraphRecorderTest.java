package org.tacet.nodeagentapi;

import com.google.common.collect.ImmutableMap;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author <a href="mailto:thor.aage.eldby@arktekk.no">Thor Åge Eldby (teldby)</a>
 */
public class CallGraphRecorderTest {

    @Test
    public void call_graph_is_recorded() {
        CallGraphRecorder.start("a", ImmutableMap.of("Hei", "Hopp"));
        CallGraphRecorder.start("b");
        CallGraphRecorder.stop("b");
        CallGraphRecorder.start("c");
        CallGraphRecorder.stop("c");
        CallGraphRecorder.stop("a");
        CallNode lastCallGraph = CallGraphRecorder.getAndResetLastCallGraph();
        CallNode expectedCallGraph = CallNode.newInstance("a", 0, ImmutableMap.of("Hei", "Hopp")).withSubCallNode(CallNode.newInstance("b", 0)).withSubCallNode(CallNode.newInstance("c", 0));
        assertCallGraphEquals(expectedCallGraph, lastCallGraph);
    }

    private void assertCallGraphEquals(CallNode expectedCallGraph, CallNode callGraph) {
        assertTrue("Expected: " + expectedCallGraph + "; got: " + callGraph, expectedCallGraph.isEqualExceptTimings(callGraph));
    }

    @Test
    public void call_graph_is_recorded_in_per_thread_context() throws Exception {
        CallGraphRecorder.start("a");
        Thread thread = new Thread(new Runnable() {
            public void run() {
                CallGraphRecorder.start("b");
                CallGraphRecorder.stop("b");
                CallNode expectedCallGraph = CallNode.newInstance("b", 0);
                CallNode lastCallGraph = CallGraphRecorder.getAndResetLastCallGraph();
                assertCallGraphEquals(expectedCallGraph, lastCallGraph);
            }
        });
        thread.start();
        thread.join(100000);
        CallGraphRecorder.stop("a");
        CallNode expectedCallGraph = CallNode.newInstance("a", 0);
        CallNode lastCallGraph = CallGraphRecorder.getAndResetLastCallGraph();
        assertCallGraphEquals(expectedCallGraph, lastCallGraph);
    }

    @Test
    public void call_timings_is_available_recursively() throws Exception {
        CallNode callNode = CallNode.newInstance("a", 90000).withStopTime(90060).withSubCallNode(CallNode.newInstance("b", 90020).withStopTime(90040));
        assertEquals(60, callNode.getAggregatedTimeMS());
        assertEquals(40, callNode.getOwnTimeMS());
        assertEquals(20, callNode.getSubCallNodes().get(0).getAggregatedTimeMS());
        assertEquals(20, callNode.getSubCallNodes().get(0).getOwnTimeMS());
    }

}
