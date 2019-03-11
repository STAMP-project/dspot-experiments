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
package org.jboss.as.test.integration.ejb.mdb.cmt.notsupported;


import javax.annotation.Resource;
import javax.jms.ConnectionFactory;
import javax.jms.Queue;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.as.arquillian.api.ServerSetup;
import org.jboss.as.arquillian.api.ServerSetupTask;
import org.jboss.as.arquillian.container.ManagementClient;
import org.jboss.as.test.integration.common.jms.JMSOperations;
import org.jboss.as.test.integration.common.jms.JMSOperationsProvider;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 *
 *
 * @author <a href="http://jmesnil.net/">Jeff Mesnil</a> (c) 2013 Red Hat inc.
 */
@RunWith(Arquillian.class)
@ServerSetup({ ContainerManagedTransactionNotSupportedTestCase.JmsQueueSetup.class })
public class ContainerManagedTransactionNotSupportedTestCase {
    public static final String QUEUE_JNDI_NAME_FOR_ANNOTATION = "java:jboss/queue/cmt-not-supported-annotation";

    public static final String QUEUE_JNDI_NAME_FOR_DD = "java:jboss/queue/cmt-not-supported-dd";

    public static final String EXCEPTION_PROP_NAME = "setRollbackOnlyThrowsIllegalStateException";

    static class JmsQueueSetup implements ServerSetupTask {
        private JMSOperations jmsAdminOperations;

        @Override
        public void setup(ManagementClient managementClient, String containerId) throws Exception {
            jmsAdminOperations = JMSOperationsProvider.getInstance(managementClient);
            jmsAdminOperations.createJmsQueue("ContainerManagedTransactionNotSupportedTestCaseQueueWithAnnotation", ContainerManagedTransactionNotSupportedTestCase.QUEUE_JNDI_NAME_FOR_ANNOTATION);
            jmsAdminOperations.createJmsQueue("ContainerManagedTransactionNotSupportedTestCaseQueueWithDD", ContainerManagedTransactionNotSupportedTestCase.QUEUE_JNDI_NAME_FOR_DD);
        }

        @Override
        public void tearDown(ManagementClient managementClient, String containerId) throws Exception {
            if ((jmsAdminOperations) != null) {
                jmsAdminOperations.removeJmsQueue("ContainerManagedTransactionNotSupportedTestCaseQueueWithAnnotation");
                jmsAdminOperations.removeJmsQueue("ContainerManagedTransactionNotSupportedTestCaseQueueWithDD");
                jmsAdminOperations.close();
            }
        }
    }

    @Resource(mappedName = ContainerManagedTransactionNotSupportedTestCase.QUEUE_JNDI_NAME_FOR_ANNOTATION)
    private Queue queueForAnnotation;

    @Resource(mappedName = ContainerManagedTransactionNotSupportedTestCase.QUEUE_JNDI_NAME_FOR_DD)
    private Queue queueForDD;

    @Resource(mappedName = "java:/ConnectionFactory")
    private ConnectionFactory cf;

    /**
     * Test that the {@link javax.ejb.MessageDrivenContext#setRollbackOnly()} throws an IllegalStateException
     * when a CMT MDB with NOT_SUPPORTED transaction attribute is invoked.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testSetRollbackOnlyInContainerManagedTransactionNotSupportedMDBThrowsIllegalStateExceptionWithAnnotation() throws Exception {
        doSetRollbackOnlyInContainerManagedTransactionNotSupportedMDBThrowsIllegalStateException(queueForAnnotation);
    }

    @Test
    public void testSetRollbackOnlyInContainerManagedTransactionNotSupportedMDBThrowsIllegalStateExceptionWithDD() throws Exception {
        doSetRollbackOnlyInContainerManagedTransactionNotSupportedMDBThrowsIllegalStateException(queueForDD);
    }
}

