package org.tacet.nodeagentapi;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author <a href="mailto:thor.aage.eldby@arktekk.no">Thor Åge Eldby (teldby)</a>
 */
public class CallMeasurementTest {

    @Test
    public void adding_more_tags_is_possible() {
        assertEquals(ImmutableList.of("atag", "another"), CallMeasurement.newInstance(1, "hei", 0).withTag("atag").withTag("another").getTags());
    }

    @Test
    public void adding_more_properties_is_possible() {
        assertEquals(ImmutableMap.of("hei", "hopp", "dont", "stop"), CallMeasurement.newInstance(1, "yo", 0).withProperty("dont", "stop").withProperty("hei", "hopp").getProperties());
    }
    
}
