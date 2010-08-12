package org.tacet.nodeagentapi;

import java.util.*;

/**
 * @author <a href="mailto:thor.aage.eldby@arktekk.no">Thor Åge Eldby (teldby)</a>
 */
public class CallMeasurement extends Measurement {

    private final int id;
    private final String name;
    private final Map<String, String> properties;
    private final List<CallMeasurement> children;
    private final List<String> tags;
    private final long startNS;
    private final long stopNS;

    private CallMeasurement(int id, String name, Map<String, String> properties, List<CallMeasurement> children, long startNS, long stopNS, List<String> tags) {
        this.id = id;
        this.name = name;
        this.properties = properties;
        this.children = children;
        this.startNS = startNS;
        this.stopNS = stopNS;
        this.tags = tags;
    }

    @SuppressWarnings({"unchecked"})
    static CallMeasurement newInstance(int id, String name, long startNS) {
        return new CallMeasurement(id, name, Collections.EMPTY_MAP, Collections.EMPTY_LIST, startNS, -1, Collections.EMPTY_LIST);
    }

    int getId() {
        return id;
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

    public boolean isEqualExceptTimings(CallMeasurement other) {
        int nodeCount = children.size();
        if (other.getName().equals(name) && other.getProperties().equals(properties) && other.getChildren().size() == nodeCount && other.getTags().equals(tags)) {
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
        return "CallMeasurement(name=" + name + ", properties=" + properties + ", tags=" + tags + ", children=" + children + ")";
    }

    CallMeasurement withChild(CallMeasurement callMeasurement) {
        List<CallMeasurement> newChildren = new ArrayList<CallMeasurement>(children);
        newChildren.add(callMeasurement);
        return new CallMeasurement(id, name, properties, newChildren, startNS, stopNS, tags);
    }

    CallMeasurement withStopNS(long stopNS) {
        return new CallMeasurement(id, name, properties, children, startNS, stopNS, tags);
    }

    public CallMeasurement withProperties(Map<String, String> properties) {
        return new CallMeasurement(id, name, properties, children, startNS, stopNS, tags);
    }

    public CallMeasurement withProperty(String name, String value) {
        HashMap<String, String> newProperties = new HashMap<String, String>(properties);
        newProperties.put(name, value);
        return new CallMeasurement(id, this.name, newProperties, children, startNS, stopNS, tags);
    }

    public CallMeasurement withTag(String tag) {
        ArrayList<String> newTags = new ArrayList<String>(tags);
        newTags.add(tag);
        return new CallMeasurement(id, name, properties, children, startNS, stopNS, newTags);
    }

    CallMeasurement withChildren(List<CallMeasurement> children) {
        return new CallMeasurement(id, name, properties, children, startNS, stopNS, tags);
    }

    public void stop() {
        CallGraphRecorder.commit(this);
    }

}
