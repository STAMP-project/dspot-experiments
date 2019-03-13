/**
 * JBoss, Home of Professional Open Source.
 * Copyright 2012, Red Hat, Inc., and individual contributors
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
package org.jboss.as.test.integration.jpa.hibernate.management;


import javax.naming.InitialContext;
import org.jboss.arquillian.container.test.api.Deployer;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.as.arquillian.api.ContainerResource;
import org.jboss.as.arquillian.container.ManagementClient;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * JPA management operations to ensure jboss-cli.sh/admin console will work with jpa statistics
 *
 * @author Scott Marlow
 */
@RunWith(Arquillian.class)
@RunAsClient
public class ManagementTestCase {
    private static final String ARCHIVE_NAME = "jpa_ManagementTestCase";

    @ArquillianResource
    private Deployer deployer;

    @ArquillianResource
    private InitialContext iniCtx;

    /**
     * Test that we can get the entity-insert-count attribute from the Hibernate 4 management statistics.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void getEntityInsertCountAttribute() throws Exception {
        try {
            deployer.deploy(ManagementTestCase.ARCHIVE_NAME);
            Assert.assertTrue("obtained entity-insert-count attribute from JPA persistence unit", (0 == (getEntityInsertCount())));
        } finally {
            deployer.undeploy(ManagementTestCase.ARCHIVE_NAME);
        }
    }

    @ContainerResource
    private ManagementClient managementClient;
}

