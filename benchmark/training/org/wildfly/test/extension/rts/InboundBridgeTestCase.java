/**
 * JBoss, Home of Professional Open Source.
 * Copyright 2013, Red Hat Middleware LLC, and individual contributors
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
package org.wildfly.test.extension.rts;


import org.codehaus.jettison.json.JSONArray;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.as.arquillian.container.ManagementClient;
import org.jboss.jbossts.star.util.TxStatusMediaType;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 *
 *
 * @author <a href="mailto:gytis@redhat.com">Gytis Trikleris</a>
 */
@RunAsClient
@RunWith(Arquillian.class)
public final class InboundBridgeTestCase extends AbstractTestCase {
    private static final String DEPENDENCIES = "Dependencies: org.jboss.narayana.rts, org.jboss.jts, org.codehaus.jettison\n";

    private String inboundBridgeResourceUrl;

    private String loggingRestATParticipantUrl;

    private String loggingRestATParticipantInvocationsUrl;

    @ArquillianResource
    private ManagementClient managementClient;

    @Test
    public void testCommit() throws Exception {
        txSupport.startTx();
        enlistInboundBridgeResource();
        txSupport.commitTx();
        final JSONArray jsonArray = getInboundBridgeResourceInvocations();
        Assert.assertEquals(4, jsonArray.length());
        Assert.assertEquals("LoggingXAResource.start", jsonArray.get(0));
        Assert.assertEquals("LoggingXAResource.end", jsonArray.get(1));
        Assert.assertEquals("LoggingXAResource.prepare", jsonArray.get(2));
        Assert.assertEquals("LoggingXAResource.commit", jsonArray.get(3));
    }

    @Test
    public void testRollback() throws Exception {
        txSupport.startTx();
        enlistInboundBridgeResource();
        txSupport.rollbackTx();
        JSONArray jsonArray = getInboundBridgeResourceInvocations();
        Assert.assertEquals(3, jsonArray.length());
        Assert.assertEquals("LoggingXAResource.start", jsonArray.get(0));
        Assert.assertEquals("LoggingXAResource.end", jsonArray.get(1));
        Assert.assertEquals("LoggingXAResource.rollback", jsonArray.get(2));
    }

    @Test
    public void testCommitWithTwoParticipants() throws Exception {
        txSupport.startTx();
        enlistLoggingRestATParticipant();
        enlistInboundBridgeResource();
        txSupport.commitTx();
        JSONArray participantResourceInvocations = getLoggingRestATParticipantInvocations();
        JSONArray xaResourceInvocations = getInboundBridgeResourceInvocations();
        Assert.assertEquals(2, participantResourceInvocations.length());
        Assert.assertEquals((("LoggingRestATResource.terminateParticipant(" + (TxStatusMediaType.TX_PREPARED)) + ")"), participantResourceInvocations.get(0));
        Assert.assertEquals((("LoggingRestATResource.terminateParticipant(" + (TxStatusMediaType.TX_COMMITTED)) + ")"), participantResourceInvocations.get(1));
        Assert.assertEquals(4, xaResourceInvocations.length());
        Assert.assertEquals("LoggingXAResource.start", xaResourceInvocations.get(0));
        Assert.assertEquals("LoggingXAResource.end", xaResourceInvocations.get(1));
        Assert.assertEquals("LoggingXAResource.prepare", xaResourceInvocations.get(2));
        Assert.assertEquals("LoggingXAResource.commit", xaResourceInvocations.get(3));
    }

    @Test
    public void testRollbackWithTwoParticipants() throws Exception {
        txSupport.startTx();
        enlistLoggingRestATParticipant();
        enlistInboundBridgeResource();
        txSupport.rollbackTx();
        JSONArray participantResourceInvocations = getLoggingRestATParticipantInvocations();
        JSONArray xaResourceInvocations = getInboundBridgeResourceInvocations();
        Assert.assertEquals(1, participantResourceInvocations.length());
        Assert.assertEquals((("LoggingRestATResource.terminateParticipant(" + (TxStatusMediaType.TX_ROLLEDBACK)) + ")"), participantResourceInvocations.get(0));
        Assert.assertEquals(3, xaResourceInvocations.length());
        Assert.assertEquals("LoggingXAResource.start", xaResourceInvocations.get(0));
        Assert.assertEquals("LoggingXAResource.end", xaResourceInvocations.get(1));
        Assert.assertEquals("LoggingXAResource.rollback", xaResourceInvocations.get(2));
    }
}

