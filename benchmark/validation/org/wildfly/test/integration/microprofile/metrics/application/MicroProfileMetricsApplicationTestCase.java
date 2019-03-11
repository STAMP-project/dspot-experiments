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
package org.wildfly.test.integration.microprofile.metrics.application;


import org.jboss.arquillian.container.test.api.Deployer;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.as.arquillian.api.ContainerResource;
import org.jboss.as.arquillian.api.ServerSetup;
import org.jboss.as.arquillian.api.ServerSetupTask;
import org.jboss.as.arquillian.container.ManagementClient;
import org.jboss.as.controller.client.helpers.Operations;
import org.jboss.dmr.ModelNode;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.wildfly.test.integration.microprofile.metrics.MetricsHelper;


/**
 * Test application metrics that are provided by a deployment.
 *
 * @author <a href="http://jmesnil.net/">Jeff Mesnil</a> (c) 2018 Red Hat inc.
 */
@RunWith(Arquillian.class)
@RunAsClient
@ServerSetup({ MicroProfileMetricsApplicationTestCase.EnablesUndertowStatistics.class })
public class MicroProfileMetricsApplicationTestCase {
    static class EnablesUndertowStatistics implements ServerSetupTask {
        @Override
        public void setup(ManagementClient managementClient, String containerId) throws Exception {
            managementClient.getControllerClient().execute(enableStatistics(true));
            reloadIfRequired(managementClient);
        }

        @Override
        public void tearDown(ManagementClient managementClient, String containerId) throws Exception {
            managementClient.getControllerClient().execute(enableStatistics(false));
            reloadIfRequired(managementClient);
        }

        private ModelNode enableStatistics(boolean enabled) {
            final ModelNode address = Operations.createAddress(SUBSYSTEM, "undertow");
            return Operations.createWriteAttributeOperation(address, STATISTICS_ENABLED, enabled);
        }
    }

    @ContainerResource
    ManagementClient managementClient;

    @ArquillianResource
    private Deployer deployer;

    private static int requestCalled = 0;

    @Test
    @InSequence(1)
    public void testApplicationMetricBeforeDeployment() throws Exception {
        MetricsHelper.getPrometheusMetrics(managementClient, "application", false);
        MetricsHelper.getJSONMetrics(managementClient, "application", false);
        // deploy the archive
        deployer.deploy("MicroProfileMetricsApplicationTestCase");
    }

    @Test
    @InSequence(5)
    public void tesApplicationMetricAfterUndeployment() throws Exception {
        deployer.undeploy("MicroProfileMetricsApplicationTestCase");
        checkRequestCount(MicroProfileMetricsApplicationTestCase.requestCalled, false);
        MetricsHelper.getPrometheusMetrics(managementClient, "application", false);
        MetricsHelper.getJSONMetrics(managementClient, "application", false);
    }
}

