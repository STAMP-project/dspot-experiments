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
package org.apache.hadoop.yarn.client;


import FinalApplicationStatus.SUCCEEDED;
import java.io.IOException;
import java.util.ArrayList;
import org.apache.hadoop.yarn.api.protocolrecords.AllocateRequest;
import org.apache.hadoop.yarn.api.protocolrecords.AllocateResponse;
import org.apache.hadoop.yarn.api.protocolrecords.FinishApplicationMasterRequest;
import org.apache.hadoop.yarn.api.protocolrecords.FinishApplicationMasterResponse;
import org.apache.hadoop.yarn.api.protocolrecords.RegisterApplicationMasterRequest;
import org.apache.hadoop.yarn.api.protocolrecords.RegisterApplicationMasterResponse;
import org.apache.hadoop.yarn.api.records.ResourceBlacklistRequest;
import org.apache.hadoop.yarn.exceptions.YarnException;
import org.junit.Assert;
import org.junit.Test;


public class TestApplicationMasterServiceProtocolOnHA extends ApplicationMasterServiceProtoTestBase {
    @Test(timeout = 15000)
    public void testRegisterApplicationMasterOnHA() throws IOException, YarnException {
        RegisterApplicationMasterRequest request = RegisterApplicationMasterRequest.newInstance("localhost", 0, "");
        RegisterApplicationMasterResponse response = getAMClient().registerApplicationMaster(request);
        Assert.assertEquals(response, this.cluster.createFakeRegisterApplicationMasterResponse());
    }

    @Test(timeout = 15000)
    public void testFinishApplicationMasterOnHA() throws IOException, YarnException {
        FinishApplicationMasterRequest request = FinishApplicationMasterRequest.newInstance(SUCCEEDED, "", "");
        FinishApplicationMasterResponse response = getAMClient().finishApplicationMaster(request);
        Assert.assertEquals(response, this.cluster.createFakeFinishApplicationMasterResponse());
    }

    @Test(timeout = 15000)
    public void testAllocateOnHA() throws IOException, YarnException {
        AllocateRequest request = AllocateRequest.newInstance(0, 50.0F, new ArrayList<org.apache.hadoop.yarn.api.records.ResourceRequest>(), new ArrayList<org.apache.hadoop.yarn.api.records.ContainerId>(), ResourceBlacklistRequest.newInstance(new ArrayList<String>(), new ArrayList<String>()));
        AllocateResponse response = getAMClient().allocate(request);
        Assert.assertEquals(response, this.cluster.createFakeAllocateResponse());
    }
}

