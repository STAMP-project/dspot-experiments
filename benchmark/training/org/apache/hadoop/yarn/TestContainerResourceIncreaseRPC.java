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
package org.apache.hadoop.yarn;


import java.io.IOException;
import org.apache.hadoop.yarn.api.ContainerManagementProtocol;
import org.apache.hadoop.yarn.api.protocolrecords.CommitResponse;
import org.apache.hadoop.yarn.api.protocolrecords.ContainerUpdateRequest;
import org.apache.hadoop.yarn.api.protocolrecords.ContainerUpdateResponse;
import org.apache.hadoop.yarn.api.protocolrecords.GetContainerStatusesRequest;
import org.apache.hadoop.yarn.api.protocolrecords.GetContainerStatusesResponse;
import org.apache.hadoop.yarn.api.protocolrecords.GetLocalizationStatusesRequest;
import org.apache.hadoop.yarn.api.protocolrecords.GetLocalizationStatusesResponse;
import org.apache.hadoop.yarn.api.protocolrecords.IncreaseContainersResourceRequest;
import org.apache.hadoop.yarn.api.protocolrecords.IncreaseContainersResourceResponse;
import org.apache.hadoop.yarn.api.protocolrecords.ReInitializeContainerRequest;
import org.apache.hadoop.yarn.api.protocolrecords.ReInitializeContainerResponse;
import org.apache.hadoop.yarn.api.protocolrecords.ResourceLocalizationRequest;
import org.apache.hadoop.yarn.api.protocolrecords.ResourceLocalizationResponse;
import org.apache.hadoop.yarn.api.protocolrecords.RestartContainerResponse;
import org.apache.hadoop.yarn.api.protocolrecords.RollbackResponse;
import org.apache.hadoop.yarn.api.protocolrecords.SignalContainerRequest;
import org.apache.hadoop.yarn.api.protocolrecords.SignalContainerResponse;
import org.apache.hadoop.yarn.api.protocolrecords.StartContainersRequest;
import org.apache.hadoop.yarn.api.protocolrecords.StartContainersResponse;
import org.apache.hadoop.yarn.api.protocolrecords.StopContainersRequest;
import org.apache.hadoop.yarn.api.protocolrecords.StopContainersResponse;
import org.apache.hadoop.yarn.api.records.ContainerId;
import org.apache.hadoop.yarn.exceptions.YarnException;
import org.apache.hadoop.yarn.ipc.HadoopYarnProtoRPC;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/* Test that the container resource increase rpc times out properly.
This is used by AM to increase container resource.
 */
public class TestContainerResourceIncreaseRPC {
    private static final Logger LOG = LoggerFactory.getLogger(TestContainerResourceIncreaseRPC.class);

    @Test
    public void testHadoopProtoRPCTimeout() throws Exception {
        testRPCTimeout(HadoopYarnProtoRPC.class.getName());
    }

    public class DummyContainerManager implements ContainerManagementProtocol {
        @Override
        public StartContainersResponse startContainers(StartContainersRequest requests) throws IOException, YarnException {
            Exception e = new Exception("Dummy function", new Exception("Dummy function cause"));
            throw new YarnException(e);
        }

        @Override
        public StopContainersResponse stopContainers(StopContainersRequest requests) throws IOException, YarnException {
            Exception e = new Exception("Dummy function", new Exception("Dummy function cause"));
            throw new YarnException(e);
        }

        @Override
        public GetContainerStatusesResponse getContainerStatuses(GetContainerStatusesRequest request) throws IOException, YarnException {
            Exception e = new Exception("Dummy function", new Exception("Dummy function cause"));
            throw new YarnException(e);
        }

        @Override
        @Deprecated
        public IncreaseContainersResourceResponse increaseContainersResource(IncreaseContainersResourceRequest request) throws IOException, YarnException {
            return null;
        }

        @Override
        public ContainerUpdateResponse updateContainer(ContainerUpdateRequest request) throws IOException, YarnException {
            try {
                // make the thread sleep to look like its not going to respond
                Thread.sleep(10000);
            } catch (Exception e) {
                TestContainerResourceIncreaseRPC.LOG.error(e.toString());
                throw new YarnException(e);
            }
            throw new YarnException("Shouldn't happen!!");
        }

        @Override
        public SignalContainerResponse signalToContainer(SignalContainerRequest request) throws IOException, YarnException {
            return null;
        }

        @Override
        public ResourceLocalizationResponse localize(ResourceLocalizationRequest request) throws IOException, YarnException {
            return null;
        }

        @Override
        public ReInitializeContainerResponse reInitializeContainer(ReInitializeContainerRequest request) throws IOException, YarnException {
            return null;
        }

        @Override
        public RestartContainerResponse restartContainer(ContainerId containerId) throws IOException, YarnException {
            return null;
        }

        @Override
        public RollbackResponse rollbackLastReInitialization(ContainerId containerId) throws IOException, YarnException {
            return null;
        }

        @Override
        public CommitResponse commitLastReInitialization(ContainerId containerId) throws IOException, YarnException {
            return null;
        }

        @Override
        public GetLocalizationStatusesResponse getLocalizationStatuses(GetLocalizationStatusesRequest request) throws IOException, YarnException {
            return null;
        }
    }
}

