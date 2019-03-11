/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.flink.yarn;


import FinalApplicationStatus.SUCCEEDED;
import YarnApplicationState.FINISHED;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import org.apache.flink.client.deployment.ClusterDeploymentException;
import org.apache.flink.client.deployment.ClusterRetrieveException;
import org.apache.flink.client.deployment.ClusterSpecification;
import org.apache.flink.client.program.ClusterClient;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.runtime.jobgraph.JobGraph;
import org.apache.flink.util.Preconditions;
import org.apache.flink.util.TestLogger;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.api.records.ApplicationReport;
import org.apache.hadoop.yarn.client.api.YarnClient;
import org.apache.hadoop.yarn.client.api.impl.YarnClientImpl;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.exceptions.YarnException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


/**
 * Tests for the {@link AbstractYarnClusterDescriptor}.
 */
public class AbstractYarnClusterTest extends TestLogger {
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    /**
     * Tests that the cluster retrieval of a finished YARN application fails.
     */
    @Test(expected = ClusterRetrieveException.class)
    public void testClusterClientRetrievalOfFinishedYarnApplication() throws Exception {
        final ApplicationId applicationId = ApplicationId.newInstance(System.currentTimeMillis(), 42);
        final ApplicationReport applicationReport = createApplicationReport(applicationId, FINISHED, SUCCEEDED);
        final YarnClient yarnClient = new AbstractYarnClusterTest.TestingYarnClient(Collections.singletonMap(applicationId, applicationReport));
        final YarnConfiguration yarnConfiguration = new YarnConfiguration();
        yarnClient.init(yarnConfiguration);
        yarnClient.start();
        final AbstractYarnClusterTest.TestingAbstractYarnClusterDescriptor clusterDescriptor = new AbstractYarnClusterTest.TestingAbstractYarnClusterDescriptor(new Configuration(), yarnConfiguration, temporaryFolder.newFolder().getAbsolutePath(), yarnClient, false);
        try {
            clusterDescriptor.retrieve(applicationId);
        } finally {
            close();
        }
    }

    private static final class TestingYarnClient extends YarnClientImpl {
        private final Map<ApplicationId, ApplicationReport> applicationReports;

        private TestingYarnClient(Map<ApplicationId, ApplicationReport> applicationReports) {
            this.applicationReports = Preconditions.checkNotNull(applicationReports);
        }

        @Override
        public ApplicationReport getApplicationReport(ApplicationId appId) throws IOException, YarnException {
            final ApplicationReport applicationReport = applicationReports.get(appId);
            if (applicationReport != null) {
                return applicationReport;
            } else {
                return super.getApplicationReport(appId);
            }
        }
    }

    private static final class TestingAbstractYarnClusterDescriptor extends AbstractYarnClusterDescriptor {
        private TestingAbstractYarnClusterDescriptor(Configuration flinkConfiguration, YarnConfiguration yarnConfiguration, String configurationDirectory, YarnClient yarnClient, boolean sharedYarnClient) {
            super(flinkConfiguration, yarnConfiguration, configurationDirectory, yarnClient, sharedYarnClient);
        }

        @Override
        protected String getYarnSessionClusterEntrypoint() {
            throw new UnsupportedOperationException("Not needed for testing");
        }

        @Override
        protected String getYarnJobClusterEntrypoint() {
            throw new UnsupportedOperationException("Not needed for testing");
        }

        @Override
        protected ClusterClient<ApplicationId> createYarnClusterClient(AbstractYarnClusterDescriptor descriptor, int numberTaskManagers, int slotsPerTaskManager, ApplicationReport report, Configuration flinkConfiguration, boolean perJobCluster) throws Exception {
            throw new UnsupportedOperationException("Not needed for testing");
        }

        @Override
        public ClusterClient<ApplicationId> deployJobCluster(ClusterSpecification clusterSpecification, JobGraph jobGraph, boolean detached) throws ClusterDeploymentException {
            throw new UnsupportedOperationException("Not needed for testing");
        }
    }
}

