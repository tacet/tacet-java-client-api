package org.tacet.nodeagentapi;

import com.google.common.collect.ImmutableMap;
import org.apache.commons.io.IOUtils;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.nio.SelectChannelConnector;
import org.mortbay.jetty.servlet.ServletHandler;
import org.mortbay.jetty.servlet.ServletHolder;
import org.tacet.nodeagentapi.model.CallMeasurement;
import org.tacet.nodeagentapi.model.Root;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

import static org.junit.Assert.fail;

/**
 * @author <a href="mailto:thor.aage.eldby@arktekk.no">Thor Åge Eldby (teldby)</a>
 */
public class MeasurementSenderTest {

    private static Server server = new Server();
    private static Map<String, ?> result;
    private final static Object resultLock = new Object();

    @BeforeClass
    static public void setupServer() throws Exception {
        server.addConnector(new SelectChannelConnector());
        ServletHandler servletHandler = new ServletHandler();
        servletHandler.addServletWithMapping(new ServletHolder(new HttpServlet() {
            @Override
            protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
                final String tmpResult = IOUtils.toString(req.getInputStream());
                while (true) {
                    synchronized (resultLock) {
                        if (result == null) {
                            result = jsonStringAsMap(tmpResult);
                            return;
                        }
                    }
                    Thread.yield();
                }
            }
        }), "/measurements");
        server.addHandler(servletHandler);
        server.start();
    }

    @AfterClass
    static public void shutdownServer() throws Exception {
        server.stop();
    }

    @Test
    public void can_send_result_asynchronous_as_json() throws Exception {
        CallMeasurement subCallMeasurement = CallMeasurement.newInstance("yeah", 90020).withStopNS(90040);
        CallMeasurement callMeasurement = CallMeasurement.newInstance("hei", 90000).withProperties(ImmutableMap.of("user", "name")).withStopNS(90060);
        new MeasurementSender("http://localhost:" + getPort() + "/measurements").send(Root.newInstance("here").withMeasurement(callMeasurement).withDate(new Date(0)));
        new MeasurementSender("http://localhost:" + getPort() + "/measurements").send(Root.newInstance("there").withMeasurement(callMeasurement.withChild(subCallMeasurement)).withDate(new Date(0)));
        assertResults(jsonFileAsMap("can_send_result_asynchronous_as_json_example1.json"),
                jsonFileAsMap("can_send_result_asynchronous_as_json_example2.json"));
    }

    @SuppressWarnings({"unchecked"})
    private static Map<String, ?> jsonFileAsMap(String resourceName) throws IOException {
        return new JsonFactory(new ObjectMapper()).createJsonParser(MeasurementSenderTest.class.getResourceAsStream(resourceName)).readValueAs(Map.class);
    }

    @SuppressWarnings({"unchecked"})
    private static Map<String, ?> jsonStringAsMap(String content) throws IOException {
        return new JsonFactory(new ObjectMapper()).createJsonParser(content).readValueAs(Map.class);
    }

    @Test
    public void error_in_sending_does_not_bubble_up() {
        new MeasurementSender("http://localhost:0/").send(Root.newInstance("everywhere").withMeasurement(CallMeasurement.newInstance("yeah", 90020).withStopNS(90040)));
    }

    private void assertResults(Map<String, ?>... expectedResults) {
        List<Map<String, ?>> resultsToFind = new ArrayList<Map<String, ?>>(Arrays.asList(expectedResults));
        while (!resultsToFind.isEmpty()) {
            synchronized (resultLock) {
                if (result != null) {
                    if (!resultsToFind.remove(result)) {
                        fail("Result not found in expected: " + result);
                    }
                    result = null;
                }
            }
            Thread.yield();
        }
    }

    private int getPort() {
        return server.getConnectors()[0].getLocalPort();
    }

}
