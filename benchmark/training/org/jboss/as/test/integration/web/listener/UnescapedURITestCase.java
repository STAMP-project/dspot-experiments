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
package org.jboss.as.test.integration.web.listener;


import java.net.URI;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.as.arquillian.api.ServerSetup;
import org.jboss.as.arquillian.container.ManagementClient;
import org.jboss.as.controller.PathAddress;
import org.jboss.as.controller.client.helpers.Operations;
import org.jboss.as.test.integration.management.ManagementOperations;
import org.jboss.as.test.shared.ServerReload;
import org.jboss.as.test.shared.SnapshotRestoreSetupTask;
import org.jboss.dmr.ModelNode;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(Arquillian.class)
@ServerSetup(UnescapedURITestCase.Setup.class)
@RunAsClient
public class UnescapedURITestCase {
    @ArquillianResource
    private URI uri;

    private static final int PORT = 7645;

    private static final String NEWBINDING = "newbinding";

    static class Setup extends SnapshotRestoreSetupTask {
        @Override
        public void doSetup(ManagementClient managementClient, String containerId) throws Exception {
            ModelNode node = Operations.createAddOperation(PathAddress.parseCLIStyleAddress(("/socket-binding-group=standard-sockets/socket-binding=" + (UnescapedURITestCase.NEWBINDING))).toModelNode());
            node.get("port").set(UnescapedURITestCase.PORT);
            ManagementOperations.executeOperation(managementClient.getControllerClient(), node);
            node = Operations.createAddOperation(PathAddress.parseCLIStyleAddress("/subsystem=undertow/server=default-server/http-listener=newlistener").toModelNode());
            node.get("socket-binding").set(UnescapedURITestCase.NEWBINDING);
            node.get("allow-unescaped-characters-in-url").set(true);
            ManagementOperations.executeOperation(managementClient.getControllerClient(), node);
            ServerReload.executeReloadAndWaitForCompletion(managementClient);
        }
    }

    @Test
    public void testForUnescapedCharacterInURLisRejected() throws Exception {
        String res = getResult(uri.getPort());
        Assert.assertTrue(res, res.startsWith("HTTP/1.1 400"));
        Assert.assertFalse(res, res.contains("ECHO"));// we should not have hit the servlet

    }

    @Test
    public void testForUnescapedCharacterInURLisAccepted() throws Exception {
        String res = getResult(UnescapedURITestCase.PORT);
        Assert.assertTrue(res, res.startsWith("HTTP/1.1 200"));
        Assert.assertTrue(res, res.contains("ECHO:/? ?"));
    }
}

