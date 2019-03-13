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
package org.apache.hadoop.hbase.regionserver;


import java.io.IOException;
import java.io.StringWriter;
import java.util.List;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.ServerName;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.RegionInfo;
import org.apache.hadoop.hbase.client.RegionInfoBuilder;
import org.apache.hadoop.hbase.ipc.RpcServerInterface;
import org.apache.hadoop.hbase.shaded.protobuf.ResponseConverter;
import org.apache.hadoop.hbase.shaded.protobuf.generated.AdminProtos.GetOnlineRegionRequest;
import org.apache.hadoop.hbase.shaded.protobuf.generated.AdminProtos.GetServerInfoResponse;
import org.apache.hadoop.hbase.testclassification.RegionServerTests;
import org.apache.hadoop.hbase.testclassification.SmallTests;
import org.apache.hadoop.hbase.tmpl.regionserver.RSStatusTmpl;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hbase.thirdparty.com.google.common.collect.Lists;
import org.apache.hbase.thirdparty.com.google.protobuf.RpcController;
import org.apache.hbase.thirdparty.com.google.protobuf.ServiceException;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TestName;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Tests for the region server status page and its template.
 */
@Category({ RegionServerTests.class, SmallTests.class })
public class TestRSStatusServlet {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestRSStatusServlet.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestRSStatusServlet.class);

    private HRegionServer rs;

    private RSRpcServices rpcServices;

    private RpcServerInterface rpcServer;

    static final int FAKE_IPC_PORT = 1585;

    static final int FAKE_WEB_PORT = 1586;

    private final ServerName fakeServerName = ServerName.valueOf("localhost", TestRSStatusServlet.FAKE_IPC_PORT, 11111);

    private final GetServerInfoResponse fakeResponse = ResponseConverter.buildGetServerInfoResponse(fakeServerName, TestRSStatusServlet.FAKE_WEB_PORT);

    private final ServerName fakeMasterAddress = ServerName.valueOf("localhost", 60010, 1212121212);

    @Rule
    public TestName name = new TestName();

    @Test
    public void testBasic() throws IOException, ServiceException {
        new RSStatusTmpl().render(new StringWriter(), rs);
    }

    @Test
    public void testWithRegions() throws IOException, ServiceException {
        HTableDescriptor htd = new HTableDescriptor(TableName.valueOf(name.getMethodName()));
        List<RegionInfo> regions = Lists.newArrayList(RegionInfoBuilder.newBuilder(htd.getTableName()).setStartKey(Bytes.toBytes("a")).setEndKey(Bytes.toBytes("d")).build(), RegionInfoBuilder.newBuilder(htd.getTableName()).setStartKey(Bytes.toBytes("d")).setEndKey(Bytes.toBytes("z")).build());
        Mockito.doReturn(ResponseConverter.buildGetOnlineRegionResponse(regions)).when(rpcServices).getOnlineRegion(((RpcController) (Mockito.any())), ((GetOnlineRegionRequest) (Mockito.any())));
        new RSStatusTmpl().render(new StringWriter(), rs);
    }
}

