package org.tacet.nodeagentapi;

import com.google.common.collect.ImmutableMap;
import org.apache.commons.io.IOUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.nio.SelectChannelConnector;
import org.mortbay.jetty.servlet.ServletHandler;
import org.mortbay.jetty.servlet.ServletHolder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.fail;

/**
 * @author <a href="mailto:thor.aage.eldby@arktekk.no">Thor Åge Eldby (teldby)</a>
 */
public class MeasurementSenderTest {

    private static Server server = new Server();
    private static String result;
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
                            result = tmpResult;
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
        new MeasurementSender("http://localhost:" + getPort() + "/measurements").send(callMeasurement);
        new MeasurementSender("http://localhost:" + getPort() + "/measurements").send(callMeasurement.withChild(subCallMeasurement));
        assertResults(IOUtils.toString(getClass().getResourceAsStream("can_send_result_asynchronous_as_json_example1.json")),
                IOUtils.toString(getClass().getResourceAsStream("can_send_result_asynchronous_as_json_example2.json")));
    }

    @Test
    public void error_in_sending_does_not_bubble_up() {
        new MeasurementSender("http://localhost:0/").send(CallMeasurement.newInstance("yeah", 90020).withStopNS(90040));
    }

    private void assertResults(String... expectedResults) {
        List<String> results = new ArrayList<String>(Arrays.asList(expectedResults));
        while (!results.isEmpty()) {
            synchronized (resultLock) {
                if (result != null) {
                    if (!results.remove(result)) {
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
