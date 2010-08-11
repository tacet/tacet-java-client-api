package org.tacet.nodeagentapi.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * @author <a href="mailto:thor.aage.eldby@arktekk.no">Thor Åge Eldby (teldby)</a>
 */
public class Root {
    private final String source;
    private final Date date;
    private final List<Measurement> measurements;

    private Root(String source, Date date, List<Measurement> measurements) {
        this.source = source;
        this.date = date;
        this.measurements = measurements;
    }

    @SuppressWarnings({"unchecked"})
    public static Root newInstance(String source) {
        return new Root(source, new Date(), Collections.EMPTY_LIST);
    }

    @SuppressWarnings({"UnusedDeclaration"})
    public String getSource() {
        return source;
    }

    @SuppressWarnings({"UnusedDeclaration"})
    public Date getDate() {
        return date;
    }

    @SuppressWarnings({"UnusedDeclaration"})
    public List<Measurement> getMeasurements() {
        return measurements;
    }

    public Root withMeasurement(Measurement measurement) {
        ArrayList<Measurement> newMeasurements = new ArrayList<Measurement>(measurements);
        newMeasurements.add(measurement);
        return new Root(source, date, newMeasurements);
    }

    /**
     * Really just for testing
     *
     * @param date overwrite date
     * @return new root
     */
    public Root withDate(Date date) {
        return new Root(source, date, measurements);
    }

}
