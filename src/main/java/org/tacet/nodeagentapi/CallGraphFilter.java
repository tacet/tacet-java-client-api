package org.tacet.nodeagentapi;

import org.tacet.nodeagentapi.model.Root;
import org.tacet.nodeagentapi.util.NetworkHelper;

import javax.servlet.*;
import java.io.IOException;

/**
 * Use with org.springframework.web.filter.DelegatingFilterProxy
 * 
 * @author <a href="mailto:thor.aage.eldby@arktekk.no">Thor Åge Eldby (teldby)</a>
 */
@SuppressWarnings({"UnusedDeclaration"})
public class CallGraphFilter implements Filter {

    private final MeasurementSender measurementSender;

    public CallGraphFilter(MeasurementSender measurementSender) {
        this.measurementSender = measurementSender;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        String scheme = request.getScheme();
        try {
            CallGraphRecorder.start(scheme);
            chain.doFilter(request, response);
        } finally {
            CallGraphRecorder.stop(scheme);
            measurementSender.send(Root.newInstance(NetworkHelper.getHostName()).withMeasurement(CallGraphRecorder.getAndResetLastCallGraph()));
        }
    }
    @Override
    public void destroy() {
    }
    
}
