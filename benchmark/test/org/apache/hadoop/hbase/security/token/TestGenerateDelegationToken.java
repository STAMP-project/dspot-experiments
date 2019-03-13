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
package org.apache.hadoop.hbase.security.token;


import AuthenticationMethod.TOKEN;
import AuthenticationProtos.AuthenticationService.BlockingInterface;
import HConstants.EMPTY_START_ROW;
import TableName.META_TABLE_NAME;
import com.google.protobuf.ServiceException;
import java.io.IOException;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.ipc.CoprocessorRpcChannel;
import org.apache.hadoop.hbase.protobuf.ProtobufUtil;
import org.apache.hadoop.hbase.protobuf.generated.AuthenticationProtos;
import org.apache.hadoop.hbase.protobuf.generated.AuthenticationProtos.GetAuthenticationTokenRequest;
import org.apache.hadoop.hbase.protobuf.generated.AuthenticationProtos.WhoAmIRequest;
import org.apache.hadoop.hbase.protobuf.generated.AuthenticationProtos.WhoAmIResponse;
import org.apache.hadoop.hbase.security.AccessDeniedException;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.testclassification.SecurityTests;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
@Category({ SecurityTests.class, MediumTests.class })
public class TestGenerateDelegationToken extends SecureTestCluster {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestGenerateDelegationToken.class);

    @Parameterized.Parameter
    public String rpcClientImpl;

    @Test
    public void test() throws Exception {
        try (Connection conn = ConnectionFactory.createConnection(SecureTestCluster.TEST_UTIL.getConfiguration());Table table = conn.getTable(META_TABLE_NAME)) {
            CoprocessorRpcChannel rpcChannel = table.coprocessorService(EMPTY_START_ROW);
            AuthenticationProtos.AuthenticationService.BlockingInterface service = AuthenticationProtos.AuthenticationService.newBlockingStub(rpcChannel);
            WhoAmIResponse response = service.whoAmI(null, WhoAmIRequest.getDefaultInstance());
            Assert.assertEquals(SecureTestCluster.USERNAME, response.getUsername());
            Assert.assertEquals(TOKEN.name(), response.getAuthMethod());
            try {
                service.getAuthenticationToken(null, GetAuthenticationTokenRequest.getDefaultInstance());
            } catch (ServiceException e) {
                IOException ioe = ProtobufUtil.getRemoteException(e);
                Assert.assertThat(ioe, CoreMatchers.instanceOf(AccessDeniedException.class));
                Assert.assertThat(ioe.getMessage(), CoreMatchers.containsString("Token generation only allowed for Kerberos authenticated clients"));
            }
        }
    }
}

