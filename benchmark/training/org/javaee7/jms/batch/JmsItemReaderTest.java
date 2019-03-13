package org.javaee7.jms.batch;


import javax.annotation.Resource;
import javax.ejb.EJB;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import static Resources.TOPIC;


/**
 * This test demonstrates programmatical creation of durable consumer, and reading
 * its subscribed messages in a batch job in form of an +ItemReader+.
 *
 * include::JmsItemReader[]
 *
 * The items are then fed into the writer, that performs the aggregation and stores
 * the result into a +@Singleton+ EJB.
 *
 * include::SummingItemWriter[]
 *
 * @author Patrik Dudits
 */
@RunWith(Arquillian.class)
public class JmsItemReaderTest {
    @Resource(lookup = "java:comp/DefaultJMSConnectionFactory")
    ConnectionFactory factory;

    @Resource(lookup = TOPIC)
    Topic topic;

    @EJB
    ResultCollector collector;

    /**
     * In this test case we verify that the subscription is really created upon deployment
     * and thus messages are waiting for the job even before the first run of it.
     *
     * The subscription is not deleted even after the application is undeployed, because
     * the physical topic and its subscription in the message broker still exist,
     * even after the application scoped managed objects are deleted.
     *
     * Following method is used to generate the payload:
     *
     * include::JmsItemReaderTest#sendMessages[]
     *
     * So we send 10 random numbers, and verify that summing integers works exactly the
     * same way on both ends. Or that the job really picked up all the numbers submitted
     * for the computation.
     */
    @InSequence(1)
    @Test
    public void worksAfterDeployment() throws InterruptedException {
        int sum = sendMessages(10);
        runJob();
        Assert.assertEquals(10, collector.getLastItemCount());
        Assert.assertEquals(sum, collector.getLastSum());
        Assert.assertEquals(1, collector.getNumberOfJobs());
    }

    /**
     * To verify that the durable subscription really collects messages we do few
     *  more runs.
     */
    @InSequence(2)
    @Test
    public void worksInMultipleRuns() throws InterruptedException {
        int sum = sendMessages(14);
        runJob();
        Assert.assertEquals(14, collector.getLastItemCount());
        Assert.assertEquals(sum, collector.getLastSum());
        Assert.assertEquals(2, collector.getNumberOfJobs());
        sum = sendMessages(8);// <1> Sending messages from separate connections makes no difference

        sum += sendMessages(4);
        runJob();
        Assert.assertEquals(12, collector.getLastItemCount());
        Assert.assertEquals(sum, collector.getLastSum());
        Assert.assertEquals(3, collector.getNumberOfJobs());
    }
}

