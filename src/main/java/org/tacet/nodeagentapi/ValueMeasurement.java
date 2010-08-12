package org.tacet.nodeagentapi;

/**
 * @author <a href="mailto:thor.aage.eldby@arktekk.no">Thor Åge Eldby (teldby)</a>
 */
public class ValueMeasurement<T> implements Measurement<T> {
    
    private final String kind;
    private final String name;
    private final T value;

    private ValueMeasurement(String kind, String name, T value) {
        this.kind = kind;
        this.name = name;
        this.value = value;
    }

    static <T> Measurement newInstance(String kind, String name, double value) {
        return new ValueMeasurement(kind, name, value);
    }

    @Override
    public String getKind() {
        return kind;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public T getValue() {
        return value;
    }
}
