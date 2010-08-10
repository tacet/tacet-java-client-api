package org.tacet.nodeagentapi;

import javax.servlet.*;
import java.io.IOException;

/**
 * Use with org.springframework.web.filter.DelegatingFilterProxy
 * 
 * @author <a href="mailto:thor.aage.eldby@arktekk.no">Thor Åge Eldby (teldby)</a>
 */
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
            measurementSender.send(CallGraphRecorder.getAndResetLastCallGraph());
        }
    }

    @Override
    public void destroy() {
    }
    
}
