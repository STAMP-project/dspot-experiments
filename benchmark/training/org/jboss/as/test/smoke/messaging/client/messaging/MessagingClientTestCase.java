/**
 * JBoss, Home of Professional Open Source.
 * Copyright 2010, Red Hat, Inc., and individual contributors
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
package org.jboss.as.test.smoke.messaging.client.messaging;


import org.apache.activemq.artemis.api.core.client.ClientSessionFactory;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.as.arquillian.api.ContainerResource;
import org.jboss.as.arquillian.container.ManagementClient;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Demo using the AS management API to create and destroy a Artemis core queue.
 *
 * @author Emanuel Muckenhuber
 * @author Kabir Khan
 */
@RunWith(Arquillian.class)
@RunAsClient
public class MessagingClientTestCase {
    private final String queueName = "queue.standalone";

    private final int messagingPort = 5445;

    private String messagingSocketBindingName = "messaging";

    private String remoteAcceptorName = "netty";

    @ContainerResource
    private ManagementClient managementClient;

    @Test
    public void testMessagingClientUsingMessagingPort() throws Exception {
        final ClientSessionFactory sf = createClientSessionFactory(managementClient.getWebUri().getHost(), messagingPort, false);
        doMessagingClient(sf);
        sf.close();
    }

    @Test
    public void testMessagingClientUsingHTTPPort() throws Exception {
        final ClientSessionFactory sf = createClientSessionFactory(managementClient.getWebUri().getHost(), managementClient.getWebUri().getPort(), true);
        doMessagingClient(sf);
        sf.close();
    }
}

