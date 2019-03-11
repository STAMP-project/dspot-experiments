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
import org.apache.hadoop.yarn.exceptions.YarnException;
import org.apache.hadoop.yarn.server.api.ResourceTracker;
import org.apache.hadoop.yarn.server.api.protocolrecords.NodeHeartbeatRequest;
import org.apache.hadoop.yarn.server.api.protocolrecords.NodeHeartbeatResponse;
import org.apache.hadoop.yarn.server.api.protocolrecords.RegisterNodeManagerRequest;
import org.apache.hadoop.yarn.server.api.protocolrecords.RegisterNodeManagerResponse;
import org.apache.hadoop.yarn.server.api.protocolrecords.UnRegisterNodeManagerRequest;
import org.apache.hadoop.yarn.server.api.protocolrecords.UnRegisterNodeManagerResponse;
import org.junit.Test;


public class TestYSCRPCFactories {
    @Test
    public void test() {
        testPbServerFactory();
        testPbClientFactory();
    }

    public class ResourceTrackerTestImpl implements ResourceTracker {
        @Override
        public RegisterNodeManagerResponse registerNodeManager(RegisterNodeManagerRequest request) throws IOException, YarnException {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public NodeHeartbeatResponse nodeHeartbeat(NodeHeartbeatRequest request) throws IOException, YarnException {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public UnRegisterNodeManagerResponse unRegisterNodeManager(UnRegisterNodeManagerRequest request) throws IOException, YarnException {
            // TODO Auto-generated method stub
            return null;
        }
    }
}

