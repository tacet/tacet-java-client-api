package org.tacet.nodeagentapi;

import java.util.Collections;
import java.util.Map;
import java.util.Stack;

/**
 * @author <a href="mailto:thor.aage.eldby@arktekk.no">Thor Åge Eldby (teldby)</a>
 */
public class CallGraphRecorder {

    //private static Logger logger = Logger.getLogger(CallGraphRecorder.class);

    private static ThreadLocal<Stack<CallMeasurement>> threadCallStack = new ThreadLocal<Stack<CallMeasurement>>();
    private static ThreadLocal<CallMeasurement> threadLastCallGraph = new ThreadLocal<CallMeasurement>();

    public static void start(String name, Map<String, String> properties) {
        getCallStack().push(CallMeasurement.newInstance(name, System.nanoTime()).withProperties(properties));
    }

    @SuppressWarnings({"unchecked"})
    public static void start(String name) {
        start(name, Collections.EMPTY_MAP);
    }

    public static void stop(String name) {
        Stack<CallMeasurement> callStack = getCallStack();
        CallMeasurement callMeasurement = callStack.pop().withStopNS(System.nanoTime());
        /*if (!callMeasurement.getName().equals(name)) {
            logger.warn("Expected stop of '" + callMeasurement.getName() + "', but found '" + name);
        }*/
        if (callStack.isEmpty()) {
            threadLastCallGraph.set(callMeasurement);
        } else {
            callStack.push(callStack.pop().withChild(callMeasurement));
        }
    }

    public static CallMeasurement getAndResetLastCallGraph() {
        CallMeasurement callGraph = threadLastCallGraph.get();
        threadLastCallGraph.set(null);
        return callGraph;
    }

    private static Stack<CallMeasurement> getCallStack() {
        Stack<CallMeasurement> value = threadCallStack.get();
        if (value == null) {
            value = new Stack<CallMeasurement>();
            threadCallStack.set(value);
        }
        return value;
    }

}