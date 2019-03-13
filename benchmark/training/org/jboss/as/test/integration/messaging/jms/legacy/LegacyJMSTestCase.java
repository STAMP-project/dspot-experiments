/**
 * JBoss, Home of Professional Open Source.
 * Copyright 2015, Red Hat, Inc., and individual contributors
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
package org.jboss.as.test.integration.messaging.jms.legacy;


import javax.naming.Context;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.as.arquillian.api.ContainerResource;
import org.jboss.as.arquillian.container.ManagementClient;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Test that a legacy (HornetQ) clients can lookup JMS resources managed by the messaging-activemq subsystem
 * when they lookup a legacy entry.
 *
 * @author <a href="http://jmesnil.net/">Jeff Mesnil</a> (c) 2015 Red Hat inc.
 */
@RunWith(Arquillian.class)
@RunAsClient
public class LegacyJMSTestCase {
    private static final String QUEUE_NAME = "LegacyJMSTestCase-Queue";

    private static final String QUEUE_ENTRY = "java:jboss/exported/jms/" + (LegacyJMSTestCase.QUEUE_NAME);

    private static final String LEGACY_QUEUE_LOOKUP = "legacy/jms/" + (LegacyJMSTestCase.QUEUE_NAME);

    private static final String LEGACY_QUEUE_ENTRY = "java:jboss/exported/" + (LegacyJMSTestCase.LEGACY_QUEUE_LOOKUP);

    private static final String TOPIC_NAME = "LegacyJMSTestCase-Topic";

    private static final String TOPIC_ENTRY = "java:jboss/exported/jms/" + (LegacyJMSTestCase.TOPIC_NAME);

    private static final String LEGACY_TOPIC_LOOKUP = "legacy/jms/" + (LegacyJMSTestCase.TOPIC_NAME);

    private static final String LEGACY_TOPIC_ENTRY = "java:jboss/exported/" + (LegacyJMSTestCase.LEGACY_TOPIC_LOOKUP);

    private static final String CF_NAME = "LegacyJMSTestCase-CF";

    private static final String LEGACY_CF_LOOKUP = "legacy/jms/" + (LegacyJMSTestCase.CF_NAME);

    private static final String LEGACY_CF_ENTRY = "java:jboss/exported/" + (LegacyJMSTestCase.LEGACY_CF_LOOKUP);

    @ContainerResource
    private Context remoteContext;

    @ContainerResource
    private ManagementClient managementClient;

    @Test
    public void testSendAndReceiveFromLegacyQueue() throws Exception {
        doSendAndReceive(LegacyJMSTestCase.LEGACY_CF_LOOKUP, LegacyJMSTestCase.LEGACY_QUEUE_LOOKUP);
    }

    @Test
    public void testSendAndReceiveFromLegacyTopic() throws Exception {
        doSendAndReceive(LegacyJMSTestCase.LEGACY_CF_LOOKUP, LegacyJMSTestCase.LEGACY_TOPIC_LOOKUP);
    }
}

