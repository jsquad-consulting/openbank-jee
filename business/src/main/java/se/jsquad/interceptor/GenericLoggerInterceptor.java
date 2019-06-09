package se.jsquad.interceptor;

import se.jsquad.qualifier.Log;

import javax.annotation.Priority;
import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import java.io.Serializable;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

@Priority(Interceptor.Priority.APPLICATION)
@LoggerInterceptor
@Interceptor
public class GenericLoggerInterceptor implements Serializable {
    private static final String METHOD_CALLED = "Method called: ";
    private static final String WITH_PARAMETERS = "With parameters: %s";
    private static final String WITH_PARAMETER_VALUES = "With parameter values: %s";

    @Inject @Log
    private Logger logger;

    @AroundInvoke
    public Object interceptMethod(InvocationContext invocationContext) throws Exception {
        logger.entering(invocationContext.getTarget().toString(), invocationContext.getMethod().getName());
        try {
            logger.log(Level.FINE, METHOD_CALLED + invocationContext.getMethod().getName());

            String withParameters = String.format(WITH_PARAMETERS,
                    Arrays.toString(invocationContext.getMethod().getParameters()));

            logger.log(Level.FINE, withParameters);

            String withParameterValues = String.format(WITH_PARAMETER_VALUES,
                    Arrays.toString(invocationContext.getParameters()));

            logger.log(Level.FINE, withParameterValues);

            return invocationContext.proceed();
        } catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage(), e);

            logger.log(Level.SEVERE, METHOD_CALLED + invocationContext.getMethod().getName());

            String withParameters = String.format(WITH_PARAMETERS,
                    Arrays.toString(invocationContext.getMethod().getParameters()));

            logger.log(Level.SEVERE, withParameters);

            String withParameterValues = String.format(WITH_PARAMETER_VALUES,
                    Arrays.toString(invocationContext.getParameters()));

            logger.log(Level.SEVERE, withParameterValues);

            throw new LogException(e.getMessage(), e);
        } finally {
            logger.exiting(invocationContext.getTarget().toString(), invocationContext.getMethod().getName());
        }
    }
}
