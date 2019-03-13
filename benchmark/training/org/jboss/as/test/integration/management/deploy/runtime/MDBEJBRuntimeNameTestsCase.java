package org.jboss.as.test.integration.management.deploy.runtime;


import ModelDescriptionConstants.ADDRESS;
import ModelDescriptionConstants.DEPLOYMENT;
import ModelDescriptionConstants.INCLUDE_RUNTIME;
import ModelDescriptionConstants.OP;
import ModelDescriptionConstants.READ_RESOURCE_OPERATION;
import ModelDescriptionConstants.RECURSIVE;
import ModelDescriptionConstants.SUBDEPLOYMENT;
import ModelDescriptionConstants.SUBSYSTEM;
import Session.AUTO_ACKNOWLEDGE;
import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueReceiver;
import javax.jms.QueueSession;
import javax.naming.InitialContext;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.as.arquillian.api.ContainerResource;
import org.jboss.as.arquillian.container.ManagementClient;
import org.jboss.as.controller.client.helpers.Operations;
import org.jboss.as.test.integration.common.jms.JMSOperations;
import org.jboss.as.test.integration.ejb.remote.common.EJBManagementUtil;
import org.jboss.as.test.integration.management.deploy.runtime.ejb.message.Constants;
import org.jboss.as.test.integration.management.deploy.runtime.ejb.message.SimpleMDB;
import org.jboss.as.test.shared.TimeoutUtil;
import org.jboss.dmr.ModelNode;
import org.jboss.logging.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(Arquillian.class)
@RunAsClient
public class MDBEJBRuntimeNameTestsCase extends AbstractRuntimeTestCase {
    private static final Logger log = Logger.getLogger(MDBEJBRuntimeNameTestsCase.class);

    private static final String QUEUE_NAME = "Queue-for-" + (MDBEJBRuntimeNameTestsCase.class.getName());

    private static final String EJB_TYPE = EJBManagementUtil.MESSAGE_DRIVEN;

    private static final Class BEAN_CLASS = SimpleMDB.class;

    private static final Package BEAN_PACKAGE = MDBEJBRuntimeNameTestsCase.BEAN_CLASS.getPackage();

    private static final String BEAN_NAME = "POINT";

    private static final String RT_MODULE_NAME = "nooma-nooma5-" + (MDBEJBRuntimeNameTestsCase.EJB_TYPE);

    private static final String RT_NAME = (MDBEJBRuntimeNameTestsCase.RT_MODULE_NAME) + ".ear";

    private static final String DEPLOYMENT_MODULE_NAME = ("test5-" + (MDBEJBRuntimeNameTestsCase.EJB_TYPE)) + "-test";

    private static final String DEPLOYMENT_NAME = (MDBEJBRuntimeNameTestsCase.DEPLOYMENT_MODULE_NAME) + ".ear";

    private static final String SUB_DEPLOYMENT_MODULE_NAME = "ejb";

    private static final String SUB_DEPLOYMENT_NAME = (MDBEJBRuntimeNameTestsCase.SUB_DEPLOYMENT_MODULE_NAME) + ".jar";

    @ContainerResource
    private InitialContext context;

    @ContainerResource
    private ManagementClient managementClient;

    private JMSOperations adminSupport;

    @Test
    @InSequence(1)
    public void testMDB() throws Exception {
        final QueueConnectionFactory factory = ((QueueConnectionFactory) (context.lookup("java:/jms/RemoteConnectionFactory")));
        final QueueConnection connection = factory.createQueueConnection("guest", "guest");
        try {
            connection.start();
            final QueueSession session = connection.createQueueSession(false, AUTO_ACKNOWLEDGE);
            final Queue replyDestination = session.createTemporaryQueue();
            final String requestMessage = "test";
            final Message message = session.createTextMessage(requestMessage);
            message.setJMSReplyTo(replyDestination);
            final Destination destination = ((Destination) (context.lookup(Constants.QUEUE_JNDI_NAME)));
            final MessageProducer producer = session.createProducer(destination);
            producer.send(message);
            producer.close();
            // wait for a reply
            final QueueReceiver receiver = session.createReceiver(replyDestination);
            final Message reply = receiver.receive(TimeoutUtil.adjust(1000));
            Assert.assertNotNull("Did not receive a reply on the reply queue. Perhaps the original (request) message didn't make it to the MDB?", reply);
            final String result = getText();
            Assert.assertEquals("Unexpected reply messsage", ((Constants.REPLY_MESSAGE_PREFIX) + requestMessage), result);
        } finally {
            if (connection != null) {
                // just closing the connection will close the session and other related resources (@see javax.jms.Connection)
                MDBEJBRuntimeNameTestsCase.safeClose(connection);
            }
        }
    }

    @Test
    @InSequence(2)
    public void testStepByStep() throws Exception {
        ModelNode readResource = new ModelNode();
        readResource.get(ADDRESS).add(DEPLOYMENT, MDBEJBRuntimeNameTestsCase.DEPLOYMENT_NAME);
        readResource.get(OP).set(READ_RESOURCE_OPERATION);
        readResource.get(INCLUDE_RUNTIME).set(true);
        ModelNode result = managementClient.getControllerClient().execute(readResource);
        // just to blow up
        Assert.assertTrue(("Failed to list resources: " + result), Operations.isSuccessfulOutcome(result));
        readResource.get(ADDRESS).add(SUBDEPLOYMENT, MDBEJBRuntimeNameTestsCase.SUB_DEPLOYMENT_NAME);
        result = managementClient.getControllerClient().execute(readResource);
        // just to blow up
        Assert.assertTrue(("Failed to list resources: " + result), Operations.isSuccessfulOutcome(result));
        readResource.get(ADDRESS).add(SUBSYSTEM, "ejb3");
        result = managementClient.getControllerClient().execute(readResource);
        // just to blow up
        Assert.assertTrue(("Failed to list resources: " + result), Operations.isSuccessfulOutcome(result));
        readResource.get(ADDRESS).add(MDBEJBRuntimeNameTestsCase.EJB_TYPE, MDBEJBRuntimeNameTestsCase.BEAN_NAME);
        result = managementClient.getControllerClient().execute(readResource);
        // just to blow up
        Assert.assertTrue(("Failed to list resources: " + result), Operations.isSuccessfulOutcome(result));
    }

    @Test
    @InSequence(3)
    public void testRecursive() throws Exception {
        ModelNode readResource = new ModelNode();
        readResource.get(ADDRESS).add(DEPLOYMENT, MDBEJBRuntimeNameTestsCase.DEPLOYMENT_NAME);
        readResource.get(OP).set(READ_RESOURCE_OPERATION);
        readResource.get(INCLUDE_RUNTIME).set(true);
        readResource.get(RECURSIVE).set(true);
        ModelNode result = managementClient.getControllerClient().execute(readResource);
        // just to blow up
        Assert.assertTrue(("Failed to list resources: " + result), Operations.isSuccessfulOutcome(result));
    }
}

