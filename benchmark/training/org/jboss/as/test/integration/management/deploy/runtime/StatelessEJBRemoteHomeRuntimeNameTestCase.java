package org.jboss.as.test.integration.management.deploy.runtime;


import ModelDescriptionConstants.ADDRESS;
import ModelDescriptionConstants.DEPLOYMENT;
import ModelDescriptionConstants.INCLUDE_RUNTIME;
import ModelDescriptionConstants.OP;
import ModelDescriptionConstants.READ_RESOURCE_OPERATION;
import ModelDescriptionConstants.RECURSIVE;
import ModelDescriptionConstants.SUBDEPLOYMENT;
import ModelDescriptionConstants.SUBSYSTEM;
import javax.naming.InitialContext;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.as.controller.client.ModelControllerClient;
import org.jboss.as.controller.client.helpers.Operations;
import org.jboss.as.test.integration.ejb.home.remotehome.SimpleHome;
import org.jboss.as.test.integration.ejb.home.remotehome.SimpleInterface;
import org.jboss.as.test.integration.ejb.home.remotehome.annotation.SimpleStatelessBean;
import org.jboss.as.test.integration.ejb.remote.common.EJBManagementUtil;
import org.jboss.as.test.shared.TestSuiteEnvironment;
import org.jboss.dmr.ModelNode;
import org.jboss.logging.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(Arquillian.class)
@RunAsClient
public class StatelessEJBRemoteHomeRuntimeNameTestCase extends AbstractRuntimeTestCase {
    private static Logger log = Logger.getLogger(StatelessEJBRemoteHomeRuntimeNameTestCase.class);

    private static final String EJB_TYPE = EJBManagementUtil.STATELESS;

    private static final Package BEAN_PACKAGE = SimpleHome.class.getPackage();

    private static final Class<?> BEAN_CLASS = SimpleStatelessBean.class;

    private static final String RT_MODULE_NAME = "nooma-nooma3-" + (StatelessEJBRemoteHomeRuntimeNameTestCase.EJB_TYPE);

    private static final String RT_NAME = (StatelessEJBRemoteHomeRuntimeNameTestCase.RT_MODULE_NAME) + ".ear";

    private static final String DEPLOYMENT_MODULE_NAME = ("test3-" + (StatelessEJBRemoteHomeRuntimeNameTestCase.EJB_TYPE)) + "-test";

    private static final String DEPLOYMENT_NAME = (StatelessEJBRemoteHomeRuntimeNameTestCase.DEPLOYMENT_MODULE_NAME) + ".ear";

    private static final String SUB_DEPLOYMENT_MODULE_NAME = "ejb";

    private static final String SUB_DEPLOYMENT_NAME = (StatelessEJBRemoteHomeRuntimeNameTestCase.SUB_DEPLOYMENT_MODULE_NAME) + ".jar";

    private static ModelControllerClient controllerClient = TestSuiteEnvironment.getModelControllerClient();

    private static InitialContext context;

    @Test
    @InSequence(1)
    public void testGetEjbHome() throws Exception {
        Object home = StatelessEJBRemoteHomeRuntimeNameTestCase.context.lookup(getEJBHomeJNDIBinding());
        Assert.assertTrue((home instanceof SimpleHome));
    }

    @Test
    @InSequence(2)
    public void testStatelessLocalHome() throws Exception {
        SimpleHome home = ((SimpleHome) (StatelessEJBRemoteHomeRuntimeNameTestCase.context.lookup(getEJBHomeJNDIBinding())));
        SimpleInterface ejbInstance = home.createSimple();
        Assert.assertEquals("Hello World", ejbInstance.sayHello());
        home = ((SimpleHome) (getEJBHome()));
        ejbInstance = home.createSimple();
        Assert.assertEquals("Hello World", ejbInstance.sayHello());
    }

    @Test
    @InSequence(3)
    public void testStepByStep() throws Exception {
        ModelNode readResource = new ModelNode();
        readResource.get(ADDRESS).add(DEPLOYMENT, StatelessEJBRemoteHomeRuntimeNameTestCase.DEPLOYMENT_NAME);
        readResource.get(OP).set(READ_RESOURCE_OPERATION);
        readResource.get(INCLUDE_RUNTIME).set(true);
        ModelNode result = StatelessEJBRemoteHomeRuntimeNameTestCase.controllerClient.execute(readResource);
        // just to blow up
        Assert.assertTrue(("Failed to list resources: " + result), Operations.isSuccessfulOutcome(result));
        readResource.get(ADDRESS).add(SUBDEPLOYMENT, StatelessEJBRemoteHomeRuntimeNameTestCase.SUB_DEPLOYMENT_NAME);
        result = StatelessEJBRemoteHomeRuntimeNameTestCase.controllerClient.execute(readResource);
        // just to blow up
        Assert.assertTrue(("Failed to list resources: " + result), Operations.isSuccessfulOutcome(result));
        readResource.get(ADDRESS).add(SUBSYSTEM, "ejb3");
        result = StatelessEJBRemoteHomeRuntimeNameTestCase.controllerClient.execute(readResource);
        // just to blow up
        Assert.assertTrue(("Failed to list resources: " + result), Operations.isSuccessfulOutcome(result));
        readResource.get(ADDRESS).add(StatelessEJBRemoteHomeRuntimeNameTestCase.EJB_TYPE, StatelessEJBRemoteHomeRuntimeNameTestCase.BEAN_CLASS.getSimpleName());
        result = StatelessEJBRemoteHomeRuntimeNameTestCase.controllerClient.execute(readResource);
        // just to blow up
        Assert.assertTrue(("Failed to list resources: " + result), Operations.isSuccessfulOutcome(result));
    }

    @Test
    @InSequence(4)
    public void testRecursive() throws Exception {
        ModelNode readResource = new ModelNode();
        readResource.get(ADDRESS).add(DEPLOYMENT, StatelessEJBRemoteHomeRuntimeNameTestCase.DEPLOYMENT_NAME);
        readResource.get(OP).set(READ_RESOURCE_OPERATION);
        readResource.get(INCLUDE_RUNTIME).set(true);
        readResource.get(RECURSIVE).set(true);
        ModelNode result = StatelessEJBRemoteHomeRuntimeNameTestCase.controllerClient.execute(readResource);
        // just to blow up
        Assert.assertTrue(("Failed to list resources: " + result), Operations.isSuccessfulOutcome(result));
    }
}

