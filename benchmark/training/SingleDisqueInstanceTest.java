

import biz.paluch.spinach.api.AddJobArgs;
import biz.paluch.spinach.api.Job;
import biz.paluch.spinach.api.sync.DisqueCommands;
import java.util.concurrent.TimeUnit;
import org.junit.Rule;
import org.junit.Test;
import org.testcontainers.containers.GenericContainer;


/**
 * Created by rnorth on 03/01/2016.
 */
public class SingleDisqueInstanceTest {
    @Rule
    public GenericContainer container = new GenericContainer("richnorth/disque:1.0-rc1").withExposedPorts(7711);

    private DisqueCommands<String, String> connection;

    private AddJobArgs retryAfter1Second;

    @Test
    public void testJobStoreAndRetrieve() {
        info("Adding a job to the queue");
        connection.addjob("main_queue", "body", 1, TimeUnit.MINUTES);
        info("Getting a job from the queue");
        Job<String, String> job = connection.getjob("main_queue");
        assertEquals("The retrieved job is the same as the one that was added", "body", job.getBody());
        info("Acknowledging the job to mark completion");
        connection.ackjob(job.getId());
    }

    @Test
    public void testFailureToAckJobBeforeTimeout() throws InterruptedException {
        info("Adding a job to the queue");
        connection.addjob("main_queue", "body", 1, TimeUnit.MINUTES, retryAfter1Second);
        info("Getting a job from the queue");
        Job<String, String> job = connection.getjob("main_queue");
        assertEquals("The retrieved job is the same as the one that was added", "body", job.getBody());
        info("Simulating a failure to ack the job before the timeout (1s)");
        TimeUnit.SECONDS.sleep(2);
        info("Attempting to get another job from the queue - the RETRY setting for the job means it should have reappeared");
        // The timeout specified here is how long the command will wait for a job to appear
        Job<String, String> job2 = connection.getjob(5, TimeUnit.SECONDS, "main_queue");
        assertNotNull("After re-getting the original job is back on the queue", job2);
        assertEquals("The retrieved job is the same as the one that was added", "body", job2.getBody());
    }
}

