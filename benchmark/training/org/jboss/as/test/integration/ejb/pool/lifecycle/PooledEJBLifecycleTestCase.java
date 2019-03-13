/**
 * JBoss, Home of Professional Open Source
 * Copyright 2009, Red Hat Middleware LLC, and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.as.test.integration.ejb.pool.lifecycle;


import javax.ejb.EJB;
import javax.naming.InitialContext;
import org.jboss.arquillian.container.test.api.Deployer;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.as.arquillian.api.ServerSetup;
import org.jboss.as.arquillian.api.ServerSetupTask;
import org.jboss.as.arquillian.container.ManagementClient;
import org.jboss.as.test.integration.common.jms.JMSOperations;
import org.jboss.as.test.integration.common.jms.JMSOperationsProvider;
import org.jboss.logging.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Tests instance lifecycle of EJB components which can potentially be pooled. Note that this testcase does *not* mandate that the components being tested be pooled. It's completely upto the container
 * to decide if they want to pool these components by default or not.
 *
 * @author baranowb
 * @author Jaikiran Pai - Updates related to https://issues.jboss.org/browse/WFLY-1506
 */
@RunWith(Arquillian.class)
@ServerSetup(PooledEJBLifecycleTestCase.CreateQueueForPooledEJBLifecycleTestCase.class)
public class PooledEJBLifecycleTestCase {
    private static final String MDB_DEPLOYMENT_NAME = "mdb-pool-ejb-callbacks";// module


    private static final String SLSB_DEPLOYMENT_NAME = "slsb-pool-ejb-callbacks";// module


    private static final String DEPLOYMENT_NAME_SINGLETON = "pool-ejb-callbacks-singleton";// module


    private static final String SINGLETON_JAR = (PooledEJBLifecycleTestCase.DEPLOYMENT_NAME_SINGLETON) + ".jar";// jar name


    private static final String DEPLOYED_SINGLETON_MODULE = "deployment." + (PooledEJBLifecycleTestCase.SINGLETON_JAR);// singleton deployed module name


    private static final String SLSB_JNDI_NAME = ("java:global/" + (PooledEJBLifecycleTestCase.SLSB_DEPLOYMENT_NAME)) + "/PointLessMathBean!org.jboss.as.test.integration.ejb.pool.lifecycle.PointlesMathInterface";

    private static final Logger log = Logger.getLogger(PooledEJBLifecycleTestCase.class.getName());

    @ArquillianResource
    public Deployer deployer;

    @EJB(mappedName = "java:global/pool-ejb-callbacks-singleton/LifecycleTrackerBean!org.jboss.as.test.integration.ejb.pool.lifecycle.LifecycleTracker")
    private LifecycleTracker lifecycleTracker;

    // ------------------- TEST METHODS ---------------------
    @SuppressWarnings("static-access")
    @Test
    public void testMDB() throws Exception {
        boolean requiresUndeploy = false;
        try {
            // do the deployment of the MDB
            PooledEJBLifecycleTestCase.log.trace(("About to deploy MDB archive " + (PooledEJBLifecycleTestCase.MDB_DEPLOYMENT_NAME)));
            deployer.deploy(PooledEJBLifecycleTestCase.MDB_DEPLOYMENT_NAME);
            // we keep track of this to make sure we undeploy before leaving this method
            requiresUndeploy = true;
            PooledEJBLifecycleTestCase.log.trace(("deployed " + (PooledEJBLifecycleTestCase.MDB_DEPLOYMENT_NAME)));
            // now send a messag to the queue on which the MDB is listening
            PooledEJBLifecycleTestCase.log.trace(("Sending a message to the queue on which the MDB " + " is listening"));
            triggerRequestResponseCycleOnQueue();
            Assert.assertTrue("@PostConstruct wasn't invoked on MDB", lifecycleTracker.wasPostConstructInvokedOn(((this.getClass().getPackage().getName()) + ".LifecycleCounterMDB")));
            // undeploy
            PooledEJBLifecycleTestCase.log.trace(("About to undeploy MDB archive " + (PooledEJBLifecycleTestCase.MDB_DEPLOYMENT_NAME)));
            deployer.undeploy(PooledEJBLifecycleTestCase.MDB_DEPLOYMENT_NAME);
            // we have undeployed successfully, there's no need anymore to trigger an undeployment before returning from this method
            requiresUndeploy = false;
            Assert.assertTrue("@PreDestroy wasn't invoked on MDB", lifecycleTracker.wasPreDestroyInvokedOn(((this.getClass().getPackage().getName()) + ".LifecycleCounterMDB")));
        } finally {
            if (requiresUndeploy) {
                try {
                    deployer.undeploy(PooledEJBLifecycleTestCase.MDB_DEPLOYMENT_NAME);
                } catch (Throwable t) {
                    // log and return since we don't want to corrupt any original exceptions that might have caused the test to fail
                    PooledEJBLifecycleTestCase.log.trace(("Ignoring the undeployment failure of " + (PooledEJBLifecycleTestCase.MDB_DEPLOYMENT_NAME)), t);
                }
            }
        }
    }

    @SuppressWarnings("static-access")
    @Test
    public void testSLSB() throws Exception {
        boolean requiresUndeploy = false;
        try {
            // deploy the SLSB
            PooledEJBLifecycleTestCase.log.trace(("About to deploy SLSB archive " + (PooledEJBLifecycleTestCase.SLSB_DEPLOYMENT_NAME)));
            deployer.deploy(PooledEJBLifecycleTestCase.SLSB_DEPLOYMENT_NAME);
            requiresUndeploy = true;
            PooledEJBLifecycleTestCase.log.trace(("deployed " + (PooledEJBLifecycleTestCase.SLSB_DEPLOYMENT_NAME)));
            // invoke on bean
            final PointlesMathInterface mathBean = ((PointlesMathInterface) (new InitialContext().lookup(PooledEJBLifecycleTestCase.SLSB_JNDI_NAME)));
            mathBean.pointlesMathOperation(4, 5, 6);
            Assert.assertTrue("@PostConstruct wasn't invoked on SLSB", lifecycleTracker.wasPostConstructInvokedOn(((this.getClass().getPackage().getName()) + ".PointLessMathBean")));
            PooledEJBLifecycleTestCase.log.trace(("About to undeploy SLSB archive " + (PooledEJBLifecycleTestCase.SLSB_DEPLOYMENT_NAME)));
            deployer.undeploy(PooledEJBLifecycleTestCase.SLSB_DEPLOYMENT_NAME);
            requiresUndeploy = false;
            Assert.assertTrue("@PreDestroy wasn't invoked on SLSB", lifecycleTracker.wasPreDestroyInvokedOn(((this.getClass().getPackage().getName()) + ".PointLessMathBean")));
        } finally {
            if (requiresUndeploy) {
                try {
                    deployer.undeploy(PooledEJBLifecycleTestCase.SLSB_DEPLOYMENT_NAME);
                } catch (Throwable t) {
                    // log and return since we don't want to corrupt any original exceptions that might have caused the test to fail
                    PooledEJBLifecycleTestCase.log.trace(("Ignoring the undeployment failure of " + (PooledEJBLifecycleTestCase.SLSB_DEPLOYMENT_NAME)), t);
                }
            }
        }
    }

    /**
     * Responsible for creating and removing the queue required by this testcase
     */
    static class CreateQueueForPooledEJBLifecycleTestCase implements ServerSetupTask {
        private static final String QUEUE_NAME = "Queue-for-" + (PooledEJBLifecycleTestCase.class.getName());

        @Override
        public void setup(ManagementClient managementClient, String containerId) throws Exception {
            // create the JMS queue
            final JMSOperations jmsOperations = JMSOperationsProvider.getInstance(managementClient);
            jmsOperations.createJmsQueue(PooledEJBLifecycleTestCase.CreateQueueForPooledEJBLifecycleTestCase.QUEUE_NAME, Constants.QUEUE_JNDI_NAME);
        }

        @Override
        public void tearDown(ManagementClient managementClient, String containerId) throws Exception {
            // destroy the JMS queue
            final JMSOperations jmsOperations = JMSOperationsProvider.getInstance(managementClient);
            jmsOperations.removeJmsQueue(PooledEJBLifecycleTestCase.CreateQueueForPooledEJBLifecycleTestCase.QUEUE_NAME);
        }
    }
}

