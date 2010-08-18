package org.tacet.nodeagentapi.service;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import org.junit.Before;
import org.junit.Test;
import org.tacet.nodeagentapi.Measurement;
import org.tacet.nodeagentapi.Root;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author <a href="mailto:thor.aage.eldby@arktekk.no">Thor Åge Eldby (teldby)</a>
 */
public class CallGraphFilterTest {

    private MeasurementSenderMock measurementSenderMock;
    private CallGraphFilter graphFilter;

    @Before
    public void setup() {
        measurementSenderMock = new MeasurementSenderMock();
        PathFilter kontoPathFilter = new PathFilter("konto", Pattern.compile("^\\/account\\/.*"));
        PathFilter htmlPathFilter = new PathFilter("html", Pattern.compile(".*\\.html$"));
        graphFilter = new CallGraphFilter(measurementSenderMock, ImmutableList.of(kontoPathFilter, htmlPathFilter));
    }

    @Test
    public void path_filters_decide_what_name_a_measurement_gets() throws Exception {
        HttpServletRequest servletRequestMock = mock(HttpServletRequest.class);
        when(servletRequestMock.getRequestURI()).thenReturn("/tull/test.html");
        graphFilter.doFilter(servletRequestMock, null, mock(FilterChain.class));
        List<Root> roots = measurementSenderMock.getRoots();
        assertEquals(1, roots.size());
        List<Measurement> measurements = roots.get(0).getMeasurements();
        assertEquals(1, measurements.size());
        assertEquals("html", measurements.get(0).getName());
    }

    @Test
    public void if_no_path_filter_matches_then_no_measurement_is_done() throws Exception {
        HttpServletRequest servletRequestMock = mock(HttpServletRequest.class);
        when(servletRequestMock.getRequestURI()).thenReturn("/tull/test.htmlish");
        graphFilter.doFilter(servletRequestMock, null, mock(FilterChain.class));
        List<Root> roots = measurementSenderMock.getRoots();
        assertEquals(0, roots.size());
    }

    @Test
    public void first_match_filter_wins() throws Exception {
        HttpServletRequest servletRequestMock = mock(HttpServletRequest.class);
        when(servletRequestMock.getRequestURI()).thenReturn("/account/test.html");
        graphFilter.doFilter(servletRequestMock, null, mock(FilterChain.class));
        List<Root> roots = measurementSenderMock.getRoots();
        assertEquals(1, roots.size());
        List<Measurement> measurements = roots.get(0).getMeasurements();
        assertEquals(1, measurements.size());
        assertEquals("konto", measurements.get(0).getName());
    }

    private static class MeasurementSenderMock implements MeasurementSender {
        private final ArrayList<Root> roots = Lists.newArrayList();

        @Override
        public void send(Root root) {
            roots.add(root);
        }

        public List<Root> getRoots() {
            return roots;
        }
    }

}
