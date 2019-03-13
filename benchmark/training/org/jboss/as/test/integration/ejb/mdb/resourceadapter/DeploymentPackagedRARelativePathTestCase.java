/**
 * JBoss, Home of Professional Open Source.
 * Copyright 2014, Red Hat, Inc., and individual contributors
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
package org.jboss.as.test.integration.ejb.mdb.resourceadapter;


import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Tests that a resource adapter packaged in a .ear can be used by a MDB as its resource adapter. Resource name is specified
 * relative to ear.
 *
 * @author <a href="mailto:tadamski@redhat.com">Tomasz Adamski</a>
 * @author <a href="mailto:jmartisk@redhat.com">Jan Martiska</a>
 */
@RunWith(Arquillian.class)
public class DeploymentPackagedRARelativePathTestCase {
    private static final String EAR_NAME = "ear-containing-rar";

    private static final String RAR_NAME = "rar-within-a-ear";

    private static final String EJB_JAR_NAME = "ejb-jar";

    private static final String DEPLOYMENT_ANNOTATED = "annotated";

    private static final String DEPLOYMENT_WITH_DEPLOYMENT_DESCRIPTOR = "deployment-descriptor";

    /**
     * Tests that a RA deployed within the .ear is deployed and started successfully and it's endpoint
     * activation is invoked
     *
     * @throws Exception
     * 		
     */
    @Test
    @OperateOnDeployment(DeploymentPackagedRARelativePathTestCase.DEPLOYMENT_ANNOTATED)
    public void testRADeploymentAnnotated() throws Exception {
        Assert.assertTrue("Resource adapter deployed in the .ear was not started", ResourceAdapterDeploymentTracker.INSTANCE.wasEndpointStartCalled());
        Assert.assertTrue("Resource adapter's endpoint was not activated", ResourceAdapterDeploymentTracker.INSTANCE.wasEndpointActivationCalled());
    }

    /**
     * The same as testRADeploymentAnnotated, except we use a deployment descriptor rather than annotations.
     */
    @Test
    @OperateOnDeployment(DeploymentPackagedRARelativePathTestCase.DEPLOYMENT_WITH_DEPLOYMENT_DESCRIPTOR)
    public void testRADeploymentWithDeploymentDescriptor() throws Exception {
        Assert.assertTrue("Resource adapter deployed in the .ear was not started", ResourceAdapterDeploymentTracker.INSTANCE.wasEndpointStartCalled());
        Assert.assertTrue("Resource adapter's endpoint was not activated", ResourceAdapterDeploymentTracker.INSTANCE.wasEndpointActivationCalled());
    }
}

