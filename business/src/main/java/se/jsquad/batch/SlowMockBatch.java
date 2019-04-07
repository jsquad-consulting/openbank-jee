package se.jsquad.batch;

import se.jsquad.batch.status.BatchStatus;
import se.jsquad.batch.status.Status;
import se.jsquad.qualifier.Log;

import javax.inject.Inject;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SlowMockBatch {
    @Inject @Log
    private Logger logger;

    private int sleepTime = 5;

    public BatchStatus startBatch() throws InterruptedException {
        logger.log(Level.FINE, "startBatch()");
        waitForNumberOfSeconds(sleepTime);
        BatchStatus batchStatus = new BatchStatus();

        batchStatus.setStatus(Status.SUCCESS);
        batchStatus.setMessage("Batch job went just fine.");

        return batchStatus;
    }

    private void waitForNumberOfSeconds(int seconds) throws InterruptedException {
        TimeUnit.SECONDS.sleep(seconds);
    }
}
