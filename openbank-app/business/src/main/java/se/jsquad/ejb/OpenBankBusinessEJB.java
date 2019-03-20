package se.jsquad.ejb;

import se.jsquad.batch.SlowMockBatch;
import se.jsquad.batch.status.BatchStatus;

import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

@Stateless
public class OpenBankBusinessEJB {
    private static final Logger logger = Logger.getLogger(OpenBankBusinessEJB.class.getName());

    @Inject
    private SlowMockBatch slowMockBatch;

    public String getHelloWorld() {
        logger.log(Level.FINE, "Hello world!");
        return "Hello world!";
    }

    @Asynchronous
    public Future<BatchStatus> startSlowBatch() throws InterruptedException {
        logger.log(Level.FINE, "startSlowBatch() returning a asynchronous BatchStatus object.");
        return new AsyncResult<>(slowMockBatch.startBatch());
    }
}
