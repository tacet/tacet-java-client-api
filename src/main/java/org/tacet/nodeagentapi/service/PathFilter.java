package org.tacet.nodeagentapi.service;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import java.util.regex.Pattern;

/**
 * @author <a href="mailto:thor.aage.eldby@arktekk.no">Thor Åge Eldby (teldby)</a>
 */
public class PathFilter {

    private final String measurementName;
    private final Pattern pathPattern;

    public PathFilter(String measurementName, Pattern pathPattern) {
        this.measurementName = measurementName;
        this.pathPattern = pathPattern;
    }

    public boolean matches(ServletRequest request) {
        if (pathPattern != null && request instanceof HttpServletRequest) {
            String requestURI = ((HttpServletRequest) request).getRequestURI();
            return pathPattern.matcher(requestURI).matches();
        }
        return false;
    }

    @SuppressWarnings({"UnusedDeclaration"})
    public String getMeasurementName() {
        return measurementName;
    }
    
}
