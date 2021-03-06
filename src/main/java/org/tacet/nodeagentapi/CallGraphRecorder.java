package org.tacet.nodeagentapi;

import org.apache.log4j.Logger;
import org.tacet.nodeagentapi.util.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

/**
 * @author <a href="mailto:thor.aage.eldby@arktekk.no">Thor Åge Eldby (teldby)</a>
 */
public class CallGraphRecorder {

    private static Logger logger = Logger.getLogger(CallGraphRecorder.class);

    private static ThreadLocal<ThreadInfo> threadLocalThreadInfo = new ThreadLocal<ThreadInfo>();

    public static void startRecorder() {
       if (threadLocalThreadInfo.get() != null) {
           logger.warn("Call recording started at multiple points (should only be started on entry point");
       } else {
           threadLocalThreadInfo.set(new ThreadInfo());
       }
    }

    private static class ThreadInfo {
        public Stack<Pair<Integer, String>> idStack = new Stack<Pair<Integer, String>>();
        public Stack<CallMeasurement> children = new Stack<CallMeasurement>();

        public Pair<Integer, String> pushNewId(String name) {
            return idStack.push(Pair.from(idStack.isEmpty() ? 1 : idStack.peek().first + 1, name));
        }
    }

    public static CallMeasurement start(String name) {
        try {
            ThreadInfo threadInfo = threadLocalThreadInfo.get();
            int id = -1;
            if (threadInfo != null) {
                id = threadInfo.pushNewId(name).first;
            }
            return CallMeasurement.newInstance(id, name, System.nanoTime());
        } catch (Exception e) {
            // We do this to avoid leaking exceptions into application 
            logger.fatal("Call graph recorder failed when adding '" + name + "'", e);
            return CallMeasurement.newInstance(-1, "failure", 0);
        }
    }

    static void commit(CallMeasurement measurement) {
        measurement = measurement.withStopNS(System.nanoTime());
        try {
            ThreadInfo threadInfo = threadLocalThreadInfo.get();
            if (threadInfo == null) {
                return;
            }
            if (threadInfo.idStack.isEmpty()) {
                logger.fatal("Received commit after empty id stack.");
            } else {
                int idStackHead = threadInfo.idStack.peek().first;
                int id = measurement.getId();
                if (idStackHead == id) {
                    threadInfo.idStack.pop();
                    List<CallMeasurement> children = new ArrayList<CallMeasurement>();
                    while (!threadInfo.children.isEmpty() && threadInfo.children.peek().getId() > id) {
                        children.add(threadInfo.children.pop());
                    }
                    Collections.reverse(children);
                    threadInfo.children.push(measurement.withChildren(children));
                } else if (id == -1) {
                    // This is a failure measurement issued only to avoid NPE
                    logger.warn("Failure measurement commited");
                } else if (idStackHead > id) {
                    Pair<Integer, String> pair = threadInfo.idStack.pop();
                    logger.error("The call measurement '" + pair.second + "' is dropped since it has not been stopped correctly.");
                    commit(measurement);
                } else {
                    logger.error("The call measurement '" + measurement.getName() + "' id dropped since it does not conform to tree topology (start/stop out of order or multiple times).");
                }
            }
        } catch (Exception e) {
            // We do this to avoid leaking exceptions into application
            logger.fatal("Call graph recorder failed", e);
        }
    }

    public static CallMeasurement getAndResetLastCallGraph() {
        ThreadInfo threadInfo = threadLocalThreadInfo.get();
        if (threadInfo == null) {
            logger.fatal("Recording not started");
            return null;
        }
        Stack<CallMeasurement> measurements = threadInfo.children;
        CallMeasurement measurement;
        if (measurements.isEmpty()) {
            logger.warn("No call measurements found.");
            measurement = null;
        } else {
            measurement = measurements.pop();
            if (!measurements.isEmpty()) {
                logger.error("Found multiple measurements. Dropped '" + measurement.getName() + "'");
                return getAndResetLastCallGraph();
            }
        }
        threadLocalThreadInfo.remove();
        return measurement;
    }

}