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
import org.jboss.as.test.integration.ejb.home.remotehome.SimpleInterface;
import org.jboss.as.test.integration.ejb.home.remotehome.SimpleStatefulHome;
import org.jboss.as.test.integration.ejb.home.remotehome.annotation.SimpleStatefulBean;
import org.jboss.as.test.integration.ejb.remote.common.EJBManagementUtil;
import org.jboss.as.test.shared.TestSuiteEnvironment;
import org.jboss.dmr.ModelNode;
import org.jboss.logging.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(Arquillian.class)
@RunAsClient
public class StatefulEJBRemoteHomeRuntimeNameTestCase extends AbstractRuntimeTestCase {
    private static Logger log = Logger.getLogger(StatefulEJBRemoteHomeRuntimeNameTestCase.class);

    private static final String EJB_TYPE = EJBManagementUtil.STATEFUL;

    private static final Package BEAN_PACKAGE = SimpleStatefulHome.class.getPackage();

    private static final Class<?> BEAN_CLASS = SimpleStatefulBean.class;

    private static final String RT_MODULE_NAME = "nooma-nooma1-" + (StatefulEJBRemoteHomeRuntimeNameTestCase.EJB_TYPE);

    private static final String RT_NAME = (StatefulEJBRemoteHomeRuntimeNameTestCase.RT_MODULE_NAME) + ".ear";

    private static final String DEPLOYMENT_MODULE_NAME = ("test1-" + (StatefulEJBRemoteHomeRuntimeNameTestCase.EJB_TYPE)) + "-test";

    private static final String DEPLOYMENT_NAME = (StatefulEJBRemoteHomeRuntimeNameTestCase.DEPLOYMENT_MODULE_NAME) + ".ear";

    private static final String SUB_DEPLOYMENT_MODULE_NAME = "ejb";

    private static final String SUB_DEPLOYMENT_NAME = (StatefulEJBRemoteHomeRuntimeNameTestCase.SUB_DEPLOYMENT_MODULE_NAME) + ".jar";

    private static ModelControllerClient controllerClient = TestSuiteEnvironment.getModelControllerClient();

    private static InitialContext context;

    @Test
    @InSequence(1)
    public void testGetEjbHome() throws Exception {
        Object home = StatefulEJBRemoteHomeRuntimeNameTestCase.context.lookup(getEJBHomeJNDIBinding());
        Assert.assertTrue((home instanceof SimpleStatefulHome));
    }

    @Test
    @InSequence(2)
    public void testStatefulLocalHome() throws Exception {
        SimpleStatefulHome home = ((SimpleStatefulHome) (StatefulEJBRemoteHomeRuntimeNameTestCase.context.lookup(getEJBHomeJNDIBinding())));
        SimpleInterface ejbInstance = home.createSimple("Hello World");
        Assert.assertEquals("Hello World", ejbInstance.sayHello());
        home = ((SimpleStatefulHome) (getEJBHome()));
        ejbInstance = home.createSimple("Hello World");
        Assert.assertEquals("Hello World", ejbInstance.sayHello());
    }

    @Test
    @InSequence(3)
    public void testStepByStep() throws Exception {
        ModelNode readResource = new ModelNode();
        readResource.get(ADDRESS).add(DEPLOYMENT, StatefulEJBRemoteHomeRuntimeNameTestCase.DEPLOYMENT_NAME);
        readResource.get(OP).set(READ_RESOURCE_OPERATION);
        readResource.get(INCLUDE_RUNTIME).set(true);
        ModelNode result = StatefulEJBRemoteHomeRuntimeNameTestCase.controllerClient.execute(readResource);
        // just to blow up
        Assert.assertTrue(("Failed to list resources: " + result), Operations.isSuccessfulOutcome(result));
        readResource.get(ADDRESS).add(SUBDEPLOYMENT, StatefulEJBRemoteHomeRuntimeNameTestCase.SUB_DEPLOYMENT_NAME);
        result = StatefulEJBRemoteHomeRuntimeNameTestCase.controllerClient.execute(readResource);
        // just to blow up
        Assert.assertTrue(("Failed to list resources: " + result), Operations.isSuccessfulOutcome(result));
        readResource.get(ADDRESS).add(SUBSYSTEM, "ejb3");
        result = StatefulEJBRemoteHomeRuntimeNameTestCase.controllerClient.execute(readResource);
        // just to blow up
        Assert.assertTrue(("Failed to list resources: " + result), Operations.isSuccessfulOutcome(result));
        readResource.get(ADDRESS).add(StatefulEJBRemoteHomeRuntimeNameTestCase.EJB_TYPE, StatefulEJBRemoteHomeRuntimeNameTestCase.BEAN_CLASS.getSimpleName());
        result = StatefulEJBRemoteHomeRuntimeNameTestCase.controllerClient.execute(readResource);
        // just to blow up
        Assert.assertTrue(("Failed to list resources: " + result), Operations.isSuccessfulOutcome(result));
    }

    @Test
    @InSequence(4)
    public void testRecursive() throws Exception {
        ModelNode readResource = new ModelNode();
        readResource.get(ADDRESS).add(DEPLOYMENT, StatefulEJBRemoteHomeRuntimeNameTestCase.DEPLOYMENT_NAME);
        readResource.get(OP).set(READ_RESOURCE_OPERATION);
        readResource.get(INCLUDE_RUNTIME).set(true);
        readResource.get(RECURSIVE).set(true);
        ModelNode result = StatefulEJBRemoteHomeRuntimeNameTestCase.controllerClient.execute(readResource);
        // just to blow up
        Assert.assertTrue(("Failed to list resources: " + result), Operations.isSuccessfulOutcome(result));
    }
}

