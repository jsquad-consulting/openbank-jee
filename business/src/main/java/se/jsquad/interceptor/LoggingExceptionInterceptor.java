package se.jsquad.interceptor;

import se.jsquad.qualifier.Log;

import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

@Interceptor
public class LoggingExceptionInterceptor {
    @Inject @Log
    private Logger logger;

    @AroundInvoke
    public Object logException(InvocationContext invocationContext) throws Exception {
        logger.entering(invocationContext.getTarget().toString(), invocationContext.getMethod().getName());
        try {
            return invocationContext.proceed();
        } catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
            logger.log(Level.SEVERE, invocationContext.getMethod().getName());
            logger.log(Level.SEVERE, "parameters({0})", invocationContext.getParameters());
            logger.log(Level.SEVERE, "contextData({0})",
                    new Object[] {Arrays.asList(invocationContext.getContextData().keySet())});
            throw new LogException(e.getMessage(), e);
        } finally {
            logger.exiting(invocationContext.getTarget().toString(), invocationContext.getMethod().getName());
        }
    }
}
