/**
 * JBoss, Home of Professional Open Source.
 * Copyright 2011, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.as.test.integration.ejb.pool.override;


import ClientConstants.FAILURE_DESCRIPTION;
import ClientConstants.NAME;
import ClientConstants.OP;
import ClientConstants.OP_ADDR;
import ClientConstants.OUTCOME;
import ClientConstants.SUBSYSTEM;
import ClientConstants.SUCCESS;
import ClientConstants.VALUE;
import ClientConstants.WRITE_ATTRIBUTE_OPERATION;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import javax.naming.InitialContext;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.as.arquillian.api.ServerSetup;
import org.jboss.as.arquillian.api.ServerSetupTask;
import org.jboss.as.arquillian.container.ManagementClient;
import org.jboss.as.test.integration.ejb.remote.common.EJBManagementUtil;
import org.jboss.dmr.ModelNode;
import org.jboss.logging.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Tests {@link org.jboss.ejb3.annotation.Pool} annotation usage and the &lt;pool&gt;
 * element usage in jboss-ejb3.xml for EJBs.
 *
 * @author Jaikiran Pai
 */
@RunWith(Arquillian.class)
@ServerSetup({ PoolOverrideTestCase.PoolOverrideTestCaseSetup.class, PoolOverrideTestCase.AllowPropertyReplacementSetup.class })
public class PoolOverrideTestCase {
    private static final Logger logger = Logger.getLogger(PoolOverrideTestCase.class);

    static class PoolOverrideTestCaseSetup implements ServerSetupTask {
        @Override
        public void setup(final ManagementClient managementClient, final String containerId) throws Exception {
            EJBManagementUtil.createStrictMaxPool(managementClient.getControllerClient(), PoolAnnotatedEJB.POOL_NAME, 1, 10, TimeUnit.MILLISECONDS);
            EJBManagementUtil.createStrictMaxPool(managementClient.getControllerClient(), PoolSetInDDBean.POOL_NAME_IN_DD, 1, 10, TimeUnit.MILLISECONDS);
            EJBManagementUtil.createStrictMaxPool(managementClient.getControllerClient(), PoolAnnotatedAndSetInDDBean.POOL_NAME, 1, 10, TimeUnit.MILLISECONDS);
            EJBManagementUtil.createStrictMaxPool(managementClient.getControllerClient(), PoolAnnotatedWithExpressionEJB.POOL_NAME, 1, 10, TimeUnit.MILLISECONDS);
        }

        @Override
        public void tearDown(final ManagementClient managementClient, final String containerId) throws Exception {
            EJBManagementUtil.removeStrictMaxPool(managementClient.getControllerClient(), PoolAnnotatedEJB.POOL_NAME);
            EJBManagementUtil.removeStrictMaxPool(managementClient.getControllerClient(), PoolSetInDDBean.POOL_NAME_IN_DD);
            EJBManagementUtil.removeStrictMaxPool(managementClient.getControllerClient(), PoolAnnotatedAndSetInDDBean.POOL_NAME);
            EJBManagementUtil.removeStrictMaxPool(managementClient.getControllerClient(), PoolAnnotatedWithExpressionEJB.POOL_NAME);
        }
    }

    static class AllowPropertyReplacementSetup implements ServerSetupTask {
        @Override
        public void setup(ManagementClient managementClient, String s) throws Exception {
            final ModelNode enableSubstitutionOp = new ModelNode();
            enableSubstitutionOp.get(OP_ADDR).set(SUBSYSTEM, "ee");
            enableSubstitutionOp.get(OP).set(WRITE_ATTRIBUTE_OPERATION);
            enableSubstitutionOp.get(NAME).set("annotation-property-replacement");
            enableSubstitutionOp.get(VALUE).set(true);
            try {
                applyUpdate(managementClient, enableSubstitutionOp);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void tearDown(ManagementClient managementClient, String s) throws Exception {
            final ModelNode disableSubstitution = new ModelNode();
            disableSubstitution.get(OP_ADDR).set(SUBSYSTEM, "ee");
            disableSubstitution.get(OP).set(WRITE_ATTRIBUTE_OPERATION);
            disableSubstitution.get(NAME).set("annotation-property-replacement");
            disableSubstitution.get(VALUE).set(false);
            try {
                applyUpdate(managementClient, disableSubstitution);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        private void applyUpdate(final ManagementClient managementClient, final ModelNode update) throws Exception {
            ModelNode result = managementClient.getControllerClient().execute(update);
            if ((result.hasDefined(OUTCOME)) && (SUCCESS.equals(result.get(OUTCOME).asString()))) {
            } else
                if (result.hasDefined(FAILURE_DESCRIPTION)) {
                    final String failureDesc = result.get(FAILURE_DESCRIPTION).toString();
                    throw new RuntimeException(failureDesc);
                } else {
                    throw new RuntimeException(("Operation not successful; outcome = " + (result.get("outcome"))));
                }

        }
    }

    /**
     * Test that a stateless bean configured with the {@link org.jboss.ejb3.annotation.Pool} annotation
     * is processed correctly and the correct pool is used by the bean
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testSLSBWithPoolAnnotation() throws Exception {
        final PoolAnnotatedEJB bean = InitialContext.doLookup(("java:module/" + (PoolAnnotatedEJB.class.getSimpleName())));
        this.testSimulatenousInvocationOnEJBsWithSingleInstanceInPool(bean);
    }

    @Test
    public void testSLSBWithPoolAnnotationWithExpression() throws Exception {
        final PoolAnnotatedWithExpressionEJB bean = InitialContext.doLookup(("java:module/" + (PoolAnnotatedWithExpressionEJB.class.getSimpleName())));
        this.testSimulatenousInvocationOnEJBsWithSingleInstanceInPool(bean);
    }

    /**
     * Test that a stateless bean configured with a pool reference in the jboss-ejb3.xml is processed correctly
     * and the correct pool is used by the bean
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testSLSBWithPoolReferenceInDD() throws Exception {
        final PoolSetInDDBean bean = InitialContext.doLookup(("java:module/" + (PoolSetInDDBean.class.getSimpleName())));
        this.testSimulatenousInvocationOnEJBsWithSingleInstanceInPool(bean);
    }

    /**
     * Test that a stateless bean which has been annotated with a {@link org.jboss.ejb3.annotation.Pool} annotation
     * and also has a jboss-ejb3.xml with a bean instance pool reference, is processed correctly and the deployment
     * descriptor value overrides the annotation value. To make sure that the annotation value is overriden by the
     * deployment descriptor value, the {@link PoolAnnotatedAndSetInDDBean} is annotated with {@link org.jboss.ejb3.annotation.Pool}
     * whose value points to a non-existent pool name
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testPoolConfigInDDOverridesAnnotation() throws Exception {
        final PoolAnnotatedAndSetInDDBean bean = InitialContext.doLookup(("java:module/" + (PoolAnnotatedAndSetInDDBean.class.getSimpleName())));
        this.testSimulatenousInvocationOnEJBsWithSingleInstanceInPool(bean);
    }

    /**
     * Invokes the {@link AbstractSlowBean#delay(long)} bean method
     */
    private class PooledBeanInvoker implements Callable<Void> {
        private AbstractSlowBean bean;

        private long beanProcessingTime;

        PooledBeanInvoker(final AbstractSlowBean bean, final long beanProcessingTime) {
            this.bean = bean;
            this.beanProcessingTime = beanProcessingTime;
        }

        @Override
        public Void call() throws Exception {
            this.bean.delay(this.beanProcessingTime);
            return null;
        }
    }
}

