package org.tacet.nodeagentapi.service;

import org.apache.log4j.Logger;
import org.tacet.nodeagentapi.CallGraphRecorder;
import org.tacet.nodeagentapi.CallMeasurement;
import org.tacet.nodeagentapi.Root;
import org.tacet.nodeagentapi.util.NetworkHelper;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

/**
 * Use with org.springframework.web.filter.DelegatingFilterProxy
 *
 * @author <a href="mailto:thor.aage.eldby@arktekk.no">Thor Åge Eldby (teldby)</a>
 */
@SuppressWarnings({"UnusedDeclaration"})
public class CallGraphFilter implements Filter {

    private static final Logger logger = Logger.getLogger(CallGraphFilter.class);

    private final MeasurementSender measurementSender;
    private final List<PathFilter> pathFilters;

    public CallGraphFilter(MeasurementSender measurementSender, List<PathFilter> pathFilters) {
        this.measurementSender = measurementSender;
        this.pathFilters = pathFilters;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        PathFilter pathFilter = getNameFilter(request);
        if (pathFilter == null) {
            chain.doFilter(request, response);
        } else {
            CallMeasurement measurement = CallGraphRecorder.start(pathFilter.getMeasurementName());
            if (request instanceof HttpServletRequest) {
                measurement = measurement.withProperty("path", ((HttpServletRequest) request).getRequestURI());
            }
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
    }

    private PathFilter getNameFilter(ServletRequest request) {
        PathFilter pathFilter = null;
        for (PathFilter candidateFilter : pathFilters) {
            if (candidateFilter.matches(request)) {
                pathFilter = candidateFilter;
                break;
            }
        }
        return pathFilter;
    }

    @Override
    public void destroy() {
    }

}
