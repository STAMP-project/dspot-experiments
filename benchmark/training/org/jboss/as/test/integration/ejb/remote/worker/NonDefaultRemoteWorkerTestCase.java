/**
 * JBoss, Home of Professional Open Source.
 * Copyright 2017, Red Hat, Inc., and individual contributors
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
package org.jboss.as.test.integration.ejb.remote.worker;


import ControllerLogger.ROOT_LOGGER;
import java.util.List;
import java.util.logging.Level;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.as.arquillian.container.ManagementClient;
import org.jboss.as.controller.PathAddress;
import org.jboss.as.controller.PathElement;
import org.jboss.as.controller.client.helpers.Operations;
import org.jboss.dmr.ModelNode;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Simple test case to check if we get proper feedback on write op to listener->worker
 *
 * @author baranowb
 */
@RunWith(Arquillian.class)
@RunAsClient
public class NonDefaultRemoteWorkerTestCase {
    @ArquillianResource
    protected ManagementClient managementClient;

    private static final String NAME_DEPLOYMENT = "echo-ejb-candy";// module


    private static final String NAME_WORKER = "puppet-master";

    private static final PathAddress ADDRESS_WORKER = PathAddress.pathAddress(PathElement.pathElement(SUBSYSTEM, "io"), PathElement.pathElement("worker", NonDefaultRemoteWorkerTestCase.NAME_WORKER));

    private static final PathAddress ADDRESS_HTTP_LISTENER = PathAddress.pathAddress(PathElement.pathElement(SUBSYSTEM, "undertow"), PathElement.pathElement(SERVER, "default-server"), PathElement.pathElement("http-listener", "default"));

    private static final String BAD_LEVEL = "X_X";

    private AutoCloseable serverSnapshot;

    @Test
    public void testMe() throws Exception {
        ModelNode result = setHttpListenerWorkerTo(NonDefaultRemoteWorkerTestCase.NAME_WORKER, NonDefaultRemoteWorkerTestCase.BAD_LEVEL);
        Assert.assertTrue(result.toString(), Operations.isSuccessfulOutcome(result));
        Assert.assertTrue(result.hasDefined(RESPONSE_HEADERS));
        ModelNode responseHeaders = result.get(RESPONSE_HEADERS);
        Assert.assertTrue(responseHeaders.hasDefined(WARNINGS));
        List<ModelNode> warnings = responseHeaders.get(WARNINGS).asList();
        Assert.assertTrue(((warnings.size()) == 2));
        ModelNode warningLoggerLevel = warnings.get(0);
        String message = warningLoggerLevel.get(WARNING).asString();
        Assert.assertEquals(ROOT_LOGGER.couldntConvertWarningLevel(NonDefaultRemoteWorkerTestCase.BAD_LEVEL), message);
        Level level = Level.parse(warningLoggerLevel.get(LEVEL).asString());
        Assert.assertEquals(Level.ALL, level);
        ModelNode warningWorker = warnings.get(1);
        message = warningWorker.get(WARNING).asString();
        Assert.assertTrue(String.format("Expected message to start with WFLYUT0097: found %s", message), message.startsWith("WFLYUT0097:"));
        level = Level.parse(warningWorker.get(LEVEL).asString());
        Assert.assertEquals(Level.WARNING, level);
        // default level is "WARNING, set to severe and check if there are warnings
        result = setHttpListenerWorkerTo("default", "SEVERE");
        responseHeaders = result.get(RESPONSE_HEADERS);
        Assert.assertFalse(responseHeaders.hasDefined(WARNINGS));
    }
}

