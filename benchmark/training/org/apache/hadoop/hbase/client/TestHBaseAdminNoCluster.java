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
package org.apache.hadoop.hbase.client;


import java.util.ArrayList;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.shaded.protobuf.generated.MasterProtos.BalanceRequest;
import org.apache.hadoop.hbase.shaded.protobuf.generated.MasterProtos.EnableCatalogJanitorRequest;
import org.apache.hadoop.hbase.shaded.protobuf.generated.MasterProtos.GetTableDescriptorsRequest;
import org.apache.hadoop.hbase.shaded.protobuf.generated.MasterProtos.GetTableNamesRequest;
import org.apache.hadoop.hbase.shaded.protobuf.generated.MasterProtos.IsCatalogJanitorEnabledRequest;
import org.apache.hadoop.hbase.shaded.protobuf.generated.MasterProtos.MoveRegionRequest;
import org.apache.hadoop.hbase.shaded.protobuf.generated.MasterProtos.OfflineRegionRequest;
import org.apache.hadoop.hbase.shaded.protobuf.generated.MasterProtos.RunCatalogScanRequest;
import org.apache.hadoop.hbase.shaded.protobuf.generated.MasterProtos.SetBalancerRunningRequest;
import org.apache.hadoop.hbase.testclassification.ClientTests;
import org.apache.hadoop.hbase.testclassification.SmallTests;
import org.apache.hbase.thirdparty.com.google.protobuf.RpcController;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TestName;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Category({ SmallTests.class, ClientTests.class })
public class TestHBaseAdminNoCluster {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestHBaseAdminNoCluster.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestHBaseAdminNoCluster.class);

    @Rule
    public TestName name = new TestName();

    @Test
    public void testMasterOperationsRetries() throws Exception {
        // Admin.listTables()
        testMasterOperationIsRetried(new TestHBaseAdminNoCluster.MethodCaller() {
            @Override
            public void call(Admin admin) throws Exception {
                admin.listTables();
            }

            @Override
            public void verify(MasterKeepAliveConnection masterAdmin, int count) throws Exception {
                Mockito.verify(masterAdmin, Mockito.atLeast(count)).getTableDescriptors(((RpcController) (Mockito.any())), ((GetTableDescriptorsRequest) (Mockito.any())));
            }
        });
        // Admin.listTableNames()
        testMasterOperationIsRetried(new TestHBaseAdminNoCluster.MethodCaller() {
            @Override
            public void call(Admin admin) throws Exception {
                admin.listTableNames();
            }

            @Override
            public void verify(MasterKeepAliveConnection masterAdmin, int count) throws Exception {
                Mockito.verify(masterAdmin, Mockito.atLeast(count)).getTableNames(((RpcController) (Mockito.any())), ((GetTableNamesRequest) (Mockito.any())));
            }
        });
        // Admin.getTableDescriptor()
        testMasterOperationIsRetried(new TestHBaseAdminNoCluster.MethodCaller() {
            @Override
            public void call(Admin admin) throws Exception {
                admin.getTableDescriptor(TableName.valueOf(name.getMethodName()));
            }

            @Override
            public void verify(MasterKeepAliveConnection masterAdmin, int count) throws Exception {
                Mockito.verify(masterAdmin, Mockito.atLeast(count)).getTableDescriptors(((RpcController) (Mockito.any())), ((GetTableDescriptorsRequest) (Mockito.any())));
            }
        });
        // Admin.getTableDescriptorsByTableName()
        testMasterOperationIsRetried(new TestHBaseAdminNoCluster.MethodCaller() {
            @Override
            public void call(Admin admin) throws Exception {
                admin.getTableDescriptorsByTableName(new ArrayList());
            }

            @Override
            public void verify(MasterKeepAliveConnection masterAdmin, int count) throws Exception {
                Mockito.verify(masterAdmin, Mockito.atLeast(count)).getTableDescriptors(((RpcController) (Mockito.any())), ((GetTableDescriptorsRequest) (Mockito.any())));
            }
        });
        // Admin.move()
        testMasterOperationIsRetried(new TestHBaseAdminNoCluster.MethodCaller() {
            @Override
            public void call(Admin admin) throws Exception {
                admin.move(new byte[0], null);
            }

            @Override
            public void verify(MasterKeepAliveConnection masterAdmin, int count) throws Exception {
                Mockito.verify(masterAdmin, Mockito.atLeast(count)).moveRegion(((RpcController) (Mockito.any())), ((MoveRegionRequest) (Mockito.any())));
            }
        });
        // Admin.offline()
        testMasterOperationIsRetried(new TestHBaseAdminNoCluster.MethodCaller() {
            @Override
            public void call(Admin admin) throws Exception {
                admin.offline(new byte[0]);
            }

            @Override
            public void verify(MasterKeepAliveConnection masterAdmin, int count) throws Exception {
                Mockito.verify(masterAdmin, Mockito.atLeast(count)).offlineRegion(((RpcController) (Mockito.any())), ((OfflineRegionRequest) (Mockito.any())));
            }
        });
        // Admin.setBalancerRunning()
        testMasterOperationIsRetried(new TestHBaseAdminNoCluster.MethodCaller() {
            @Override
            public void call(Admin admin) throws Exception {
                admin.setBalancerRunning(true, true);
            }

            @Override
            public void verify(MasterKeepAliveConnection masterAdmin, int count) throws Exception {
                Mockito.verify(masterAdmin, Mockito.atLeast(count)).setBalancerRunning(((RpcController) (Mockito.any())), ((SetBalancerRunningRequest) (Mockito.any())));
            }
        });
        // Admin.balancer()
        testMasterOperationIsRetried(new TestHBaseAdminNoCluster.MethodCaller() {
            @Override
            public void call(Admin admin) throws Exception {
                admin.balancer();
            }

            @Override
            public void verify(MasterKeepAliveConnection masterAdmin, int count) throws Exception {
                Mockito.verify(masterAdmin, Mockito.atLeast(count)).balance(((RpcController) (Mockito.any())), ((BalanceRequest) (Mockito.any())));
            }
        });
        // Admin.enabledCatalogJanitor()
        testMasterOperationIsRetried(new TestHBaseAdminNoCluster.MethodCaller() {
            @Override
            public void call(Admin admin) throws Exception {
                admin.enableCatalogJanitor(true);
            }

            @Override
            public void verify(MasterKeepAliveConnection masterAdmin, int count) throws Exception {
                Mockito.verify(masterAdmin, Mockito.atLeast(count)).enableCatalogJanitor(((RpcController) (Mockito.any())), ((EnableCatalogJanitorRequest) (Mockito.any())));
            }
        });
        // Admin.runCatalogScan()
        testMasterOperationIsRetried(new TestHBaseAdminNoCluster.MethodCaller() {
            @Override
            public void call(Admin admin) throws Exception {
                admin.runCatalogScan();
            }

            @Override
            public void verify(MasterKeepAliveConnection masterAdmin, int count) throws Exception {
                Mockito.verify(masterAdmin, Mockito.atLeast(count)).runCatalogScan(((RpcController) (Mockito.any())), ((RunCatalogScanRequest) (Mockito.any())));
            }
        });
        // Admin.isCatalogJanitorEnabled()
        testMasterOperationIsRetried(new TestHBaseAdminNoCluster.MethodCaller() {
            @Override
            public void call(Admin admin) throws Exception {
                admin.isCatalogJanitorEnabled();
            }

            @Override
            public void verify(MasterKeepAliveConnection masterAdmin, int count) throws Exception {
                Mockito.verify(masterAdmin, Mockito.atLeast(count)).isCatalogJanitorEnabled(((RpcController) (Mockito.any())), ((IsCatalogJanitorEnabledRequest) (Mockito.any())));
            }
        });
    }

    private static interface MethodCaller {
        void call(Admin admin) throws Exception;

        void verify(MasterKeepAliveConnection masterAdmin, int count) throws Exception;
    }
}

