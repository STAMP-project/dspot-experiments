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
package org.jboss.as.test.integration.messaging.mgmt;


import javax.naming.Context;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.as.arquillian.api.ContainerResource;
import org.jboss.as.arquillian.container.ManagementClient;
import org.jboss.as.test.shared.ServerSnapshot;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Tests the statistics for pooled-connection-factory.
 */
@RunAsClient
@RunWith(Arquillian.class)
public class PooledConnectionFactoryStatisticsTestCase {
    @ContainerResource
    private ManagementClient managementClient;

    @ContainerResource
    private Context context;

    @Test
    public void testStatistics() throws Exception {
        try (AutoCloseable snapshot = ServerSnapshot.takeSnapshot(managementClient)) {
            checkStatisticsAreDisabled();
            enableStatistics();
            Assert.assertEquals(0, readStatistic("InUseCount"));
            RemoteConnectionHolding bean = ((RemoteConnectionHolding) (context.lookup("PooledConnectionFactoryStatisticsTestCase/ConnectionHoldingBean!org.jboss.as.test.integration.messaging.mgmt.RemoteConnectionHolding")));
            bean.createConnection();
            Assert.assertEquals(1, readStatistic("InUseCount"));
            bean.closeConnection();
            Assert.assertEquals(0, readStatistic("InUseCount"));
        }
    }
}

