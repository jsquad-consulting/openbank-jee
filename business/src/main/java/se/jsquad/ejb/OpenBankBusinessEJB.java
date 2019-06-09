package se.jsquad.ejb;

import se.jsquad.batch.SlowMockBatch;
import se.jsquad.batch.status.BatchStatus;
import se.jsquad.interceptor.LoggerInterceptor;
import se.jsquad.qualifier.Log;

import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.concurrent.Future;
import java.util.logging.Logger;

@Stateless
@LoggerInterceptor
public class OpenBankBusinessEJB {
    private  static final String HELLO_WORLD = "Hello world!";

    @Inject @Log
    private Logger logger;

    @Inject
    private SlowMockBatch slowMockBatch;

    public String getHelloWorld() {
        return HELLO_WORLD;
    }

    @Asynchronous
    public Future<BatchStatus> startSlowBatch() throws InterruptedException {
        return new AsyncResult<>(slowMockBatch.startBatch());
    }
}
