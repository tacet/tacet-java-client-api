package org.tacet.nodeagentapi.service;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.tacet.nodeagentapi.CallGraphRecorder;
import org.tacet.nodeagentapi.CallMeasurement;

import java.lang.reflect.Method;

/**
 * @author <a href="mailto:thor.aage.eldby@arktekk.no">Thor Åge Eldby (teldby)</a>
 */
@SuppressWarnings({"UnusedDeclaration"})
public class CallGraphMethodInterceptor implements MethodInterceptor {

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        CallMeasurement measurement = CallGraphRecorder.start(getName(invocation));
        try {
            return invocation.proceed();
        } finally {
            measurement.stop();
        }
    }

    private String getName(MethodInvocation invocation) {
        Method method = invocation.getMethod();
        return method.getDeclaringClass().getSimpleName() + "." + method.getName();
    }

}
