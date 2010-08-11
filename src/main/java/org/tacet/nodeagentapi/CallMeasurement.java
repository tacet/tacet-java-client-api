package org.tacet.nodeagentapi;

import java.util.*;

/**
 * @author <a href="mailto:thor.aage.eldby@arktekk.no">Thor Åge Eldby (teldby)</a>
 */
public class CallMeasurement extends Measurement {

    private final String name;
    private final Map<String, String> properties;
    private final List<CallMeasurement> children;
    private final List<String> tags;
    private final long startNS;
    private final long stopNS;

    public CallMeasurement(String name, Map<String, String> properties, List<CallMeasurement> children, long startNS, long stopNS, List<String> tags) {
        this.name = name;
        this.properties = properties;
        this.children = children;
        this.startNS = startNS;
        this.stopNS = stopNS;
        this.tags = tags;
    }

    @SuppressWarnings({"unchecked"})
    public static CallMeasurement newInstance(String name, long startNS) {
        return new CallMeasurement(name, Collections.EMPTY_MAP, Collections.EMPTY_LIST, startNS, -1, Collections.EMPTY_LIST);
    }

    public String getName() {
        return name;
    }

    public Map<String, String> getProperties() {
        return properties;
    }

    public List<CallMeasurement> getChildren() {
        return children;
    }

    public List<String> getTags() {
        return tags;
    }

    @Override
    public String getKind() {
        return "java-call";
    }

    public Long getValue() {
        return stopNS - startNS;
    }

    public CallMeasurement withChild(CallMeasurement callMeasurement) {
        List<CallMeasurement> newChildren = new ArrayList<CallMeasurement>(children);
        newChildren.add(callMeasurement);
        return new CallMeasurement(name, properties, newChildren, startNS, stopNS, tags);
    }

    public boolean isEqualExceptTimings(CallMeasurement other) {
        int nodeCount = children.size();
        if (other.getName().equals(name) && other.getProperties().equals(properties) && other.getChildren().size() == nodeCount) {
            for (int i = 0; i < nodeCount; ++i) {
                if (!children.get(i).isEqualExceptTimings(other.getChildren().get(i))) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return "name=" + name + ", properties=" + properties + ", children=" + children;
    }

    public CallMeasurement withStopNS(long stopNS) {
        return new CallMeasurement(name, properties, children, startNS, stopNS, tags);
    }

    public CallMeasurement withProperties(Map<String, String> properties) {
        return new CallMeasurement(name, properties, children, startNS, stopNS, tags); 
    }

    public CallMeasurement withProperty(String name, String value) {
        HashMap<String, String> newProperties = new HashMap<String, String>(properties);
        newProperties.put(name, value);
        return new CallMeasurement(this.name, newProperties, children, startNS, stopNS, tags);
    }
    
}
