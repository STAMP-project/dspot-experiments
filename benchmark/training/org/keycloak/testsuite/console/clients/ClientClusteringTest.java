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
package org.keycloak.testsuite.console.clients;


import org.jboss.arquillian.graphene.page.Page;
import org.junit.Assert;
import org.junit.Test;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.testsuite.console.page.clients.clustering.ClientClustering;


/**
 *
 *
 * @author <a href="mailto:vramik@redhat.com">Vlastislav Ramik</a>
 */
public class ClientClusteringTest extends AbstractClientTest {
    private ClientRepresentation newClient;

    private ClientRepresentation found;

    @Page
    private ClientClustering clientClusteringPage;

    @Test
    public void basicConfigurationTest() {
        Assert.assertTrue(((found.getNodeReRegistrationTimeout()) == (-1)));
        clientClusteringPage.form().setNodeReRegistrationTimeout("10", "Seconds");
        clientClusteringPage.form().save();
        assertAlertSuccess();
        Assert.assertTrue(((findClientByClientId(TEST_CLIENT_ID).getNodeReRegistrationTimeout()) == 10));
        clientClusteringPage.form().setNodeReRegistrationTimeout("10", "Minutes");
        clientClusteringPage.form().save();
        assertAlertSuccess();
        Assert.assertTrue(((findClientByClientId(TEST_CLIENT_ID).getNodeReRegistrationTimeout()) == 600));
        clientClusteringPage.form().setNodeReRegistrationTimeout("1", "Hours");
        clientClusteringPage.form().save();
        assertAlertSuccess();
        Assert.assertTrue(((findClientByClientId(TEST_CLIENT_ID).getNodeReRegistrationTimeout()) == 3600));
        clientClusteringPage.form().setNodeReRegistrationTimeout("1", "Days");
        clientClusteringPage.form().save();
        assertAlertSuccess();
        Assert.assertTrue(((findClientByClientId(TEST_CLIENT_ID).getNodeReRegistrationTimeout()) == 86400));
        clientClusteringPage.form().setNodeReRegistrationTimeout("", "Days");
        clientClusteringPage.form().save();
        assertAlertDanger();
        clientClusteringPage.form().setNodeReRegistrationTimeout("text", "Days");
        clientClusteringPage.form().save();
        assertAlertDanger();
    }

    @Test
    public void registerNodeTest() {
        clientClusteringPage.form().addNode("new node");
        assertAlertSuccess();
        Assert.assertNotNull(findClientByClientId(TEST_CLIENT_ID).getRegisteredNodes().get("new node"));
        clientClusteringPage.form().addNode("");
        assertAlertDanger();
    }
}

