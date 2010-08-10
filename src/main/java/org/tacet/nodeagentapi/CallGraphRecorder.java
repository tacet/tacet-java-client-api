package org.tacet.nodeagentapi;

import java.util.Collections;
import java.util.Map;
import java.util.Stack;

/**
 * @author <a href="mailto:thor.aage.eldby@arktekk.no">Thor Åge Eldby (teldby)</a>
 */
public class CallGraphRecorder {

    //private static Logger logger = Logger.getLogger(CallGraphRecorder.class);

    private static ThreadLocal<Stack<CallNode>> threadCallStack = new ThreadLocal<Stack<CallNode>>();
    private static ThreadLocal<CallNode> threadLastCallGraph = new ThreadLocal<CallNode>();

    public static void start(String name, Map<String, String> properties) {
        getCallStack().push(CallNode.newInstance(name, System.currentTimeMillis(), properties));
    }

    @SuppressWarnings({"unchecked"})
    public static void start(String name) {
        start(name, Collections.EMPTY_MAP);
    }

    public static void stop(String name) {
        Stack<CallNode> callStack = getCallStack();
        CallNode callNode = callStack.pop().withStopTime(System.currentTimeMillis());
        /*if (!callNode.getName().equals(name)) {
            logger.warn("Expected stop of '" + callNode.getName() + "', but found '" + name);
        }*/
        if (callStack.isEmpty()) {
            threadLastCallGraph.set(callNode);
        } else {
            callStack.push(callStack.pop().withSubCallNode(callNode));
        }
    }

    public static CallNode getAndResetLastCallGraph() {
        CallNode callGraph = threadLastCallGraph.get();
        threadLastCallGraph.set(null);
        return callGraph;
    }

    private static Stack<CallNode> getCallStack() {
        Stack<CallNode> value = threadCallStack.get();
        if (value == null) {
            value = new Stack<CallNode>();
            threadCallStack.set(value);
        }
        return value;
    }

}