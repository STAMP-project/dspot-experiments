/**
 * JBoss, Home of Professional Open Source.
 * Copyright 2016, Red Hat, Inc., and individual contributors
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
package org.jboss.as.test.integration.jca.workmanager;


import java.io.IOException;
import org.hamcrest.CoreMatchers;
import org.hamcrest.core.AllOf;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.as.test.integration.jca.JcaMgmtBase;
import org.jboss.as.test.integration.management.util.MgmtOperationException;
import org.jboss.dmr.ModelNode;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Checks that only one short-running-thread pool or long-running-thread pool is allowed under one workmanager.
 *
 * @author Lin Gao
 */
@RunWith(Arquillian.class)
@RunAsClient
public class WorkManagerThreadsCheckTestCase extends JcaMgmtBase {
    @Test
    public void testOneLongRunningThreadPool() throws IOException {
        ModelNode address = new ModelNode();
        address.add("subsystem", "jca");
        address.add("workmanager", "default");
        address.add("long-running-threads", "Long");
        ModelNode operation = new ModelNode();
        operation.get(OP_ADDR).set(address);
        operation.get(OP).set(ADD);
        operation.get("max-threads").set(10);
        operation.get("queue-length").set(10);
        try {
            executeOperation(operation);
            Assert.fail("NOT HERE!");
        } catch (MgmtOperationException e) {
            String reason = e.getResult().get("failure-description").asString();
            Assert.assertThat("Wrong error message", reason, AllOf.allOf(CoreMatchers.containsString("WFLYJCA0101"), CoreMatchers.containsString("Long"), CoreMatchers.containsString("long-running-threads"), CoreMatchers.containsString("default")));
        }
    }

    @Test
    public void testOneShortRunningThreadPool() throws IOException {
        ModelNode address = new ModelNode();
        address.add("subsystem", "jca");
        address.add("workmanager", "default");
        address.add("short-running-threads", "Short");
        ModelNode operation = new ModelNode();
        operation.get(OP_ADDR).set(address);
        operation.get(OP).set(ADD);
        operation.get("max-threads").set(10);
        operation.get("queue-length").set(10);
        try {
            executeOperation(operation);
            Assert.fail("NOT HERE!");
        } catch (MgmtOperationException e) {
            String reason = e.getResult().get("failure-description").asString();
            Assert.assertThat("Wrong error message", reason, AllOf.allOf(CoreMatchers.containsString("WFLYJCA0101"), CoreMatchers.containsString("Short"), CoreMatchers.containsString("short-running-threads"), CoreMatchers.containsString("default")));
        }
    }

    @Test
    public void testBlockingThreadPool() throws IOException {
        Assert.assertTrue(isBlockingThreadPool("short-running-threads"));
        Assert.assertTrue(isBlockingThreadPool("long-running-threads"));
    }
}

