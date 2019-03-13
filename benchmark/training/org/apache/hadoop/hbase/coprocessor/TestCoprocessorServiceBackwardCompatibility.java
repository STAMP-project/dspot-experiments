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
package org.apache.hadoop.hbase.coprocessor;


import com.google.protobuf.RpcCallback;
import com.google.protobuf.RpcController;
import com.google.protobuf.Service;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.coprocessor.protobuf.generated.DummyRegionServerEndpointProtos.DummyRequest;
import org.apache.hadoop.hbase.coprocessor.protobuf.generated.DummyRegionServerEndpointProtos.DummyResponse;
import org.apache.hadoop.hbase.coprocessor.protobuf.generated.DummyRegionServerEndpointProtos.DummyService;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;


/**
 * Tests to ensure that 2.0 is backward compatible in loading CoprocessorService.
 */
@Category({ MediumTests.class })
public class TestCoprocessorServiceBackwardCompatibility {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestCoprocessorServiceBackwardCompatibility.class);

    private static HBaseTestingUtility TEST_UTIL = null;

    private static Configuration CONF = null;

    private static final long MASTER = 1;

    private static final long REGIONSERVER = 2;

    private static final long REGION = 3;

    public static class DummyCoprocessorService extends DummyService implements CoprocessorService , SingletonCoprocessorService {
        // depending on the value passed thru DummyRequest, the following fields would be incremented
        // value == MASTER
        static int numMaster = 0;

        // value == REGIONSERVER
        static int numRegionServer = 0;

        // value == REGION
        static int numRegion = 0;

        @Override
        public Service getService() {
            return this;
        }

        @Override
        public void dummyCall(RpcController controller, DummyRequest request, RpcCallback<DummyResponse> callback) {
            callback.run(DummyResponse.newBuilder().setValue("").build());
            if ((request.getValue()) == (TestCoprocessorServiceBackwardCompatibility.MASTER)) {
                TestCoprocessorServiceBackwardCompatibility.DummyCoprocessorService.numMaster += request.getValue();
            } else
                if ((request.getValue()) == (TestCoprocessorServiceBackwardCompatibility.REGIONSERVER)) {
                    TestCoprocessorServiceBackwardCompatibility.DummyCoprocessorService.numRegionServer += request.getValue();
                } else
                    if ((request.getValue()) == (TestCoprocessorServiceBackwardCompatibility.REGION)) {
                        TestCoprocessorServiceBackwardCompatibility.DummyCoprocessorService.numRegion += request.getValue();
                    }


        }

        @Override
        public void dummyThrow(RpcController controller, DummyRequest request, RpcCallback<DummyResponse> callback) {
        }
    }

    @Test
    public void testCoprocessorServiceLoadedByMaster() throws Throwable {
        TestCoprocessorServiceBackwardCompatibility.TEST_UTIL.getAdmin().coprocessorService().callBlockingMethod(getDescriptor().findMethodByName("dummyCall"), null, DummyRequest.newBuilder().setValue(TestCoprocessorServiceBackwardCompatibility.MASTER).build(), DummyResponse.getDefaultInstance());
        Assert.assertEquals(TestCoprocessorServiceBackwardCompatibility.MASTER, TestCoprocessorServiceBackwardCompatibility.DummyCoprocessorService.numMaster);
        TestCoprocessorServiceBackwardCompatibility.TEST_UTIL.getAdmin().coprocessorService(TestCoprocessorServiceBackwardCompatibility.TEST_UTIL.getHBaseCluster().getRegionServer(0).getServerName()).callBlockingMethod(getDescriptor().findMethodByName("dummyCall"), null, DummyRequest.newBuilder().setValue(TestCoprocessorServiceBackwardCompatibility.REGIONSERVER).build(), DummyResponse.getDefaultInstance());
        Assert.assertEquals(TestCoprocessorServiceBackwardCompatibility.REGIONSERVER, TestCoprocessorServiceBackwardCompatibility.DummyCoprocessorService.numRegionServer);
        TestCoprocessorServiceBackwardCompatibility.TEST_UTIL.getConnection().getTable(TableName.valueOf("hbase:meta")).batchCoprocessorService(getDescriptor().findMethodByName("dummyCall"), DummyRequest.newBuilder().setValue(TestCoprocessorServiceBackwardCompatibility.REGION).build(), Bytes.toBytes(""), Bytes.toBytes(""), DummyResponse.getDefaultInstance());
        Assert.assertEquals(TestCoprocessorServiceBackwardCompatibility.REGION, TestCoprocessorServiceBackwardCompatibility.DummyCoprocessorService.numRegion);
    }
}

