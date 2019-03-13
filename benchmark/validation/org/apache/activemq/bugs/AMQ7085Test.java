package org.apache.activemq.bugs;


import javax.management.ObjectName;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.jmx.BrokerView;
import org.apache.activemq.broker.jmx.QueueViewMBean;
import org.apache.activemq.command.ActiveMQQueue;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests to ensure when the temp usage limit is updated on the broker the queues also have their
 * temp usage limits automatically updated.
 */
public class AMQ7085Test {
    private BrokerService brokerService;

    private String testQueueName = "testAMQ7085Queue";

    private ActiveMQQueue queue = new ActiveMQQueue(testQueueName);

    @Test
    public void testQueueTempUsageWhenSetExplicitly() throws Exception {
        ObjectName queueViewMBeanName = new ObjectName(("org.apache.activemq:type=Broker,brokerName=localhost,destinationType=Queue,destinationName=" + (queue.getQueueName())));
        QueueViewMBean queueViewMBean = ((QueueViewMBean) (brokerService.getManagementContext().newProxyInstance(queueViewMBeanName, QueueViewMBean.class, true)));
        // Check that by default the queue's temp limit is the same as the broker's.
        BrokerView brokerView = brokerService.getAdminView();
        long brokerTempLimit = brokerView.getTempLimit();
        Assert.assertEquals(brokerTempLimit, queueViewMBean.getTempUsageLimit());
        // Change the queue's temp limit independently of the broker's setting and check the broker's limit does not
        // change.
        long queueTempLimit = brokerTempLimit + 111;
        queueViewMBean.setTempUsageLimit(queueTempLimit);
        Assert.assertEquals(queueViewMBean.getTempUsageLimit(), queueTempLimit);
        Assert.assertEquals(brokerView.getTempLimit(), brokerTempLimit);
        // Now increase the broker's temp limit.  Since the queue's limit was explicitly changed it should remain
        // unchanged.
        long newBrokerTempLimit = brokerTempLimit + 555;
        brokerView.setTempLimit(newBrokerTempLimit);
        Assert.assertEquals(brokerView.getTempLimit(), newBrokerTempLimit);
        Assert.assertEquals(queueViewMBean.getTempUsageLimit(), queueTempLimit);
    }

    @Test
    public void testQueueTempUsageWhenBrokerTempUsageUpdated() throws Exception {
        ObjectName queueViewMBeanName = new ObjectName(("org.apache.activemq:type=Broker,brokerName=localhost,destinationType=Queue,destinationName=" + (queue.getQueueName())));
        QueueViewMBean queueViewMBean = ((QueueViewMBean) (brokerService.getManagementContext().newProxyInstance(queueViewMBeanName, QueueViewMBean.class, true)));
        // Check that by default the queue's temp limit is the same as the broker's.
        BrokerView brokerView = brokerService.getAdminView();
        long brokerTempLimit = brokerView.getTempLimit();
        Assert.assertEquals(brokerTempLimit, queueViewMBean.getTempUsageLimit());
        // Increase the broker's temp limit and check the queue's limit is updated to the same value.
        long newBrokerTempLimit = brokerTempLimit + 555;
        brokerView.setTempLimit(newBrokerTempLimit);
        Assert.assertEquals(brokerView.getTempLimit(), newBrokerTempLimit);
        Assert.assertEquals(queueViewMBean.getTempUsageLimit(), newBrokerTempLimit);
    }
}

