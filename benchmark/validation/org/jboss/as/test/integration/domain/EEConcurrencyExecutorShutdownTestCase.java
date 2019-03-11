/**
 * JBoss, Home of Professional Open Source.
 * Copyright 2018, Red Hat, Inc., and individual contributors
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
package org.jboss.as.test.integration.domain;


import java.io.File;
import org.jboss.as.controller.PathAddress;
import org.jboss.as.controller.client.helpers.domain.DomainClient;
import org.jboss.as.test.integration.domain.management.util.DomainLifecycleUtil;
import org.jboss.as.test.integration.domain.management.util.DomainTestSupport;
import org.jboss.dmr.ModelNode;
import org.junit.Test;


/**
 * The test case schedules an instance of task ReSchedulingTask.
 * Each instance of ReSchedulingTask sleeps for 10 seconds and then re-schedules another instance of its own class.
 * After the CLI command /host=master/server-config=server-one:stop() is invoked the server should stop.
 * Test for [ WFCORE-3868 ].
 *
 * @author Daniel Cihak
 */
public class EEConcurrencyExecutorShutdownTestCase {
    private static final String ARCHIVE_FILE_NAME = "test.war";

    private static final PathAddress ROOT_DEPLOYMENT_ADDRESS = PathAddress.pathAddress(DEPLOYMENT, EEConcurrencyExecutorShutdownTestCase.ARCHIVE_FILE_NAME);

    private static final PathAddress MAIN_SERVER_GROUP_ADDRESS = PathAddress.pathAddress(SERVER_GROUP, "main-server-group");

    private static final PathAddress MAIN_SERVER_GROUP_DEPLOYMENT_ADDRESS = EEConcurrencyExecutorShutdownTestCase.MAIN_SERVER_GROUP_ADDRESS.append(DEPLOYMENT, EEConcurrencyExecutorShutdownTestCase.ARCHIVE_FILE_NAME);

    public static final String FIRST_SERVER_NAME = "main-one";

    private static DomainTestSupport testSupport;

    public static DomainLifecycleUtil domainMasterLifecycleUtil;

    private static File tmpDir;

    private static DomainClient masterClient;

    /**
     * Tests if the server with running ConcurrencyExecutor can be stopped using cli command
     * /host=master/server-config=server-one:stop(timeout=0)
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testConcurrencyExecutorShutdown() throws Exception {
        ModelNode content = new ModelNode();
        content.get("archive").set(true);
        content.get("path").set(new File(EEConcurrencyExecutorShutdownTestCase.tmpDir, ("archives/" + (EEConcurrencyExecutorShutdownTestCase.ARCHIVE_FILE_NAME))).getAbsolutePath());
        ModelNode deploymentOpMain = createDeploymentOperation(content, EEConcurrencyExecutorShutdownTestCase.MAIN_SERVER_GROUP_DEPLOYMENT_ADDRESS);
        executeForResult(deploymentOpMain, EEConcurrencyExecutorShutdownTestCase.masterClient);
        this.stopServer(EEConcurrencyExecutorShutdownTestCase.FIRST_SERVER_NAME);
    }
}

