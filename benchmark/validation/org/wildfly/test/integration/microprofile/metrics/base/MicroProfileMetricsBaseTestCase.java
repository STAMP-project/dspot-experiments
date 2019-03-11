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
package org.wildfly.test.integration.microprofile.metrics.base;


import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.as.arquillian.api.ContainerResource;
import org.jboss.as.arquillian.container.ManagementClient;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.wildfly.test.integration.microprofile.metrics.MetricsHelper;


/**
 * Test required base metrics that are always present.
 *
 * @author <a href="http://jmesnil.net/">Jeff Mesnil</a> (c) 2018 Red Hat inc.
 */
@RunWith(Arquillian.class)
@RunAsClient
public class MicroProfileMetricsBaseTestCase {
    @ContainerResource
    ManagementClient managementClient;

    @Test
    public void testBaseMetrics() throws Exception {
        long start = System.currentTimeMillis();
        long sleep = 50;
        String metrics = MetricsHelper.getJSONMetrics(managementClient, "base", true);
        double uptime1 = MetricsHelper.getMetricValueFromJSONOutput(metrics, "jvm.uptime");
        Assert.assertTrue((uptime1 > 0));
        Thread.sleep(sleep);
        metrics = MetricsHelper.getJSONMetrics(managementClient, "base", true);
        double uptime2 = MetricsHelper.getMetricValueFromJSONOutput(metrics, "jvm.uptime");
        long interval = (System.currentTimeMillis()) - start;
        Assert.assertTrue((uptime2 > uptime1));
        Assert.assertTrue(((uptime2 - uptime1) >= sleep));
        Assert.assertTrue(((uptime2 - uptime1) <= interval));
    }
}

