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
package org.jboss.as.test.xts.suspend;


import java.net.URL;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.as.arquillian.container.ManagementClient;
import org.junit.Test;


/**
 *
 *
 * @author <a href="mailto:gytis@redhat.com">Gytis Trikleris</a>
 */
public abstract class AbstractTestCase {
    protected static final String EXECUTOR_SERVICE_CONTAINER = "default-server";

    protected static final String REMOTE_SERVICE_CONTAINER = "alternative-server";

    protected static final String EXECUTOR_SERVICE_ARCHIVE_NAME = "executorService";

    protected static final String REMOTE_SERVICE_ARCHIVE_NAME = "remoteService";

    @ArquillianResource
    @OperateOnDeployment(AbstractTestCase.EXECUTOR_SERVICE_ARCHIVE_NAME)
    private URL executorServiceUrl;

    @ArquillianResource
    @OperateOnDeployment(AbstractTestCase.REMOTE_SERVICE_ARCHIVE_NAME)
    private URL remoteServiceUrl;

    @ArquillianResource
    @OperateOnDeployment(AbstractTestCase.REMOTE_SERVICE_ARCHIVE_NAME)
    private ManagementClient remoteServiceContainerManager;

    private ExecutorService executorService;

    private RemoteService remoteService;

    @Test(expected = Exception.class)
    public void testBeginTransactionAfterSuspend() throws Exception {
        suspendServer();
        executorService.begin();
    }

    @Test
    public void testCommitAfterSuspend() throws Exception {
        executorService.begin();
        suspendServer();
        executorService.commit();
    }

    @Test
    public void testRollbackAfterSuspend() throws Exception {
        executorService.begin();
        suspendServer();
        executorService.reset();
    }

    @Test
    public void testRemoteServiceAfterSuspend() throws Exception {
        executorService.begin();
        suspendServer();
        executorService.enlistParticipant();
        executorService.execute();
        executorService.commit();
        resumeServer();
        assertParticipantInvocations(executorService.getParticipantInvocations());
        assertParticipantInvocations(remoteService.getParticipantInvocations());
    }
}

