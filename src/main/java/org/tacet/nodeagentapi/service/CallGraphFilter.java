package org.tacet.nodeagentapi.service;

import org.apache.log4j.Logger;
import org.tacet.nodeagentapi.CallGraphRecorder;
import org.tacet.nodeagentapi.CallMeasurement;
import org.tacet.nodeagentapi.Root;
import org.tacet.nodeagentapi.util.NetworkHelper;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * Use with org.springframework.web.filter.DelegatingFilterProxy
 *
 * @author <a href="mailto:thor.aage.eldby@arktekk.no">Thor Åge Eldby (teldby)</a>
 */
@SuppressWarnings({"UnusedDeclaration"})
public class CallGraphFilter implements Filter {

    private static final Logger logger = Logger.getLogger(CallGraphFilter.class);

    private final MeasurementSender measurementSender;

    public CallGraphFilter(MeasurementSender measurementSender) {
        this.measurementSender = measurementSender;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        CallMeasurement measurement = CallGraphRecorder.start(getName(request));
        try {
            chain.doFilter(request, response);
        } finally {
            measurement.stop();
            CallMeasurement head = CallGraphRecorder.getAndResetLastCallGraph();
            if (head == null) {
                logger.fatal("Measurement graph empty. Quite impossible.");
            } else {
                measurementSender.send(Root.newInstance(NetworkHelper.getHostIp()).withMeasurement(head));
            }
        }
    }

    private String getName(ServletRequest request) {
        if (request instanceof HttpServletRequest) {
            return ((HttpServletRequest) request).getRequestURI();
        } else {
            return request.getScheme();
        }
    }

    @Override
    public void destroy() {
    }

}
