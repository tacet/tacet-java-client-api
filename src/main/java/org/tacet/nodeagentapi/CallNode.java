package org.tacet.nodeagentapi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:thor.aage.eldby@arktekk.no">Thor Åge Eldby (teldby)</a>
 */
public class CallNode {
    
    private final String name;
    private final Map<String, String> properties;
    private final List<CallNode> subCallNodes;
    private final long startTime;
    private final long stopTime;

    public CallNode(String name, Map<String, String> properties, List<CallNode> subCallNodes, long startTime, long stopTime) {
        this.name = name;
        this.properties = properties;
        this.subCallNodes = subCallNodes;
        this.startTime = startTime;
        this.stopTime = stopTime;
    }

    @SuppressWarnings({"unchecked"})
    public static CallNode newInstance(String name, long time, Map<String, String> properties) {
        return new CallNode(name, properties, Collections.EMPTY_LIST, time, -1);
    }

    @SuppressWarnings({"unchecked"})
    public static CallNode newInstance(String name, long time) {
        return newInstance(name, time, Collections.EMPTY_MAP);
    }

    public String getName() {
        return name;
    }

    public Map<String, String> getProperties() {
        return properties;
    }

    public List<CallNode> getSubCallNodes() {
        return subCallNodes;
    }

    public long getStartTime() {
        return startTime;
    }

    public CallNode withSubCallNode(CallNode callNode) {
        List<CallNode> newSubCallNodes = new ArrayList<CallNode>(subCallNodes);
        newSubCallNodes.add(callNode);
        return new CallNode(name, properties, newSubCallNodes, startTime, stopTime);
    }

    public boolean isEqualExceptTimings(CallNode other) {
        int nodeCount = subCallNodes.size();
        if (other.getName().equals(name) && other.getProperties().equals(properties) && other.getSubCallNodes().size() == nodeCount) {
            for (int i = 0; i < nodeCount; ++i) {
                if (!subCallNodes.get(i).isEqualExceptTimings(other.getSubCallNodes().get(i))) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return "name=" + name + ", properties=" + properties + ", subCallNodes=" + subCallNodes;
    }

    public long getAggregatedTimeMS() {
        return stopTime - startTime;
    }

    public CallNode withStopTime(long time) {
        return new CallNode(name, properties, subCallNodes, startTime, time);
    }

    public long getOwnTimeMS() {
        long subCallTime = 0;
        for (CallNode callNode : subCallNodes) {
            subCallTime += callNode.getAggregatedTimeMS();
        }
        return getAggregatedTimeMS() - subCallTime;
    }
    
}
