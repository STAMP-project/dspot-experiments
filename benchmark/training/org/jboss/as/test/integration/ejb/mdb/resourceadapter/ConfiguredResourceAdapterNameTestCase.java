package org.jboss.as.test.integration.ejb.mdb.resourceadapter;


import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.jms.Message;
import javax.jms.Queue;
import javax.jms.TextMessage;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.as.arquillian.api.ServerSetup;
import org.jboss.as.arquillian.api.ServerSetupTask;
import org.jboss.as.arquillian.container.ManagementClient;
import org.jboss.as.test.integration.common.jms.JMSOperations;
import org.jboss.as.test.integration.common.jms.JMSOperationsProvider;
import org.jboss.as.test.integration.ejb.mdb.JMSMessagingUtil;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(Arquillian.class)
@ServerSetup({ ConfiguredResourceAdapterNameTestCase.JmsQueueSetup.class })
public class ConfiguredResourceAdapterNameTestCase {
    private static final String REPLY_QUEUE_JNDI_NAME = "java:jboss/override-resource-adapter-name-test/replyQueue";

    public static final String QUEUE_JNDI_NAME = "java:jboss/override-resource-adapter-name-test/queue";

    @EJB(mappedName = "java:module/JMSMessagingUtil")
    private JMSMessagingUtil jmsUtil;

    @Resource(mappedName = ConfiguredResourceAdapterNameTestCase.REPLY_QUEUE_JNDI_NAME)
    private Queue replyQueue;

    @Resource(mappedName = ConfiguredResourceAdapterNameTestCase.QUEUE_JNDI_NAME)
    private Queue queue;

    static class JmsQueueSetup implements ServerSetupTask {
        private JMSOperations jmsAdminOperations;

        @Override
        public void setup(ManagementClient managementClient, String containerId) throws Exception {
            jmsAdminOperations = JMSOperationsProvider.getInstance(managementClient.getControllerClient());
            jmsAdminOperations.createJmsQueue("override-resource-adapter-name-test/queue", ConfiguredResourceAdapterNameTestCase.QUEUE_JNDI_NAME);
            jmsAdminOperations.createJmsQueue("override-resource-adapter-name-test/reply-queue", ConfiguredResourceAdapterNameTestCase.REPLY_QUEUE_JNDI_NAME);
        }

        @Override
        public void tearDown(ManagementClient managementClient, String containerId) throws Exception {
            if ((jmsAdminOperations) != null) {
                jmsAdminOperations.removeJmsQueue("override-resource-adapter-name-test/queue");
                jmsAdminOperations.removeJmsQueue("override-resource-adapter-name-test/reply-queue");
                jmsAdminOperations.close();
            }
        }
    }

    @Test
    public void testMDBWithOverriddenResourceAdapterName() throws Exception {
        final String goodMorning = "Hello World";
        // send as TextMessage
        this.jmsUtil.sendTextMessage(goodMorning, this.queue, this.replyQueue);
        // wait for an reply
        final Message reply = this.jmsUtil.receiveMessage(replyQueue, 5000);
        // test the reply
        final TextMessage textMessage = ((TextMessage) (reply));
        Assert.assertNotNull(textMessage);
        Assert.assertEquals(("Unexpected reply message on reply queue: " + (this.replyQueue)), ConfiguredResourceAdapterNameMDB.REPLY, textMessage.getText());
    }
}

