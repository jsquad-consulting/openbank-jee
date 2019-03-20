package se.jsquad.batch;

import se.jsquad.batch.status.BatchStatus;
import se.jsquad.batch.status.Status;

import java.util.concurrent.TimeUnit;

public class SlowMockBatch {
    private int sleepTime = 5;

    public BatchStatus startBatch() throws InterruptedException {
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
