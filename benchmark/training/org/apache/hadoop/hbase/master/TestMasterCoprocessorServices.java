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
package org.apache.hadoop.hbase.master;


import AccessControlService.Interface;
import com.google.protobuf.RpcCallback;
import com.google.protobuf.RpcController;
import java.util.Arrays;
import java.util.Collections;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.JMXListener;
import org.apache.hadoop.hbase.coprocessor.MasterCoprocessor;
import org.apache.hadoop.hbase.coprocessor.MasterObserver;
import org.apache.hadoop.hbase.coprocessor.RegionCoprocessor;
import org.apache.hadoop.hbase.coprocessor.RegionObserver;
import org.apache.hadoop.hbase.protobuf.generated.AccessControlProtos.AccessControlService;
import org.apache.hadoop.hbase.protobuf.generated.AccessControlProtos.CheckPermissionsRequest;
import org.apache.hadoop.hbase.protobuf.generated.AccessControlProtos.CheckPermissionsResponse;
import org.apache.hadoop.hbase.protobuf.generated.AccessControlProtos.GetUserPermissionsRequest;
import org.apache.hadoop.hbase.protobuf.generated.AccessControlProtos.GetUserPermissionsResponse;
import org.apache.hadoop.hbase.protobuf.generated.AccessControlProtos.GrantRequest;
import org.apache.hadoop.hbase.protobuf.generated.AccessControlProtos.GrantResponse;
import org.apache.hadoop.hbase.protobuf.generated.AccessControlProtos.HasPermissionRequest;
import org.apache.hadoop.hbase.protobuf.generated.AccessControlProtos.HasPermissionResponse;
import org.apache.hadoop.hbase.protobuf.generated.AccessControlProtos.RevokeRequest;
import org.apache.hadoop.hbase.protobuf.generated.AccessControlProtos.RevokeResponse;
import org.apache.hadoop.hbase.protobuf.generated.VisibilityLabelsProtos.GetAuthsRequest;
import org.apache.hadoop.hbase.protobuf.generated.VisibilityLabelsProtos.GetAuthsResponse;
import org.apache.hadoop.hbase.protobuf.generated.VisibilityLabelsProtos.ListLabelsRequest;
import org.apache.hadoop.hbase.protobuf.generated.VisibilityLabelsProtos.ListLabelsResponse;
import org.apache.hadoop.hbase.protobuf.generated.VisibilityLabelsProtos.SetAuthsRequest;
import org.apache.hadoop.hbase.protobuf.generated.VisibilityLabelsProtos.VisibilityLabelsRequest;
import org.apache.hadoop.hbase.protobuf.generated.VisibilityLabelsProtos.VisibilityLabelsResponse;
import org.apache.hadoop.hbase.protobuf.generated.VisibilityLabelsProtos.VisibilityLabelsService;
import org.apache.hadoop.hbase.security.access.AccessController;
import org.apache.hadoop.hbase.security.visibility.VisibilityController;
import org.apache.hadoop.hbase.testclassification.SmallTests;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;


/**
 * Tests that the MasterRpcServices is correctly searching for implementations of the
 * Coprocessor Service and not just the "default" implementations of those services.
 */
@Category({ SmallTests.class })
public class TestMasterCoprocessorServices {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestMasterCoprocessorServices.class);

    private static class MockAccessController implements MasterCoprocessor , MasterObserver , RegionCoprocessor , RegionObserver , AccessControlService.Interface {
        @Override
        public void grant(RpcController controller, GrantRequest request, RpcCallback<GrantResponse> done) {
        }

        @Override
        public void revoke(RpcController controller, RevokeRequest request, RpcCallback<RevokeResponse> done) {
        }

        @Override
        public void getUserPermissions(RpcController controller, GetUserPermissionsRequest request, RpcCallback<GetUserPermissionsResponse> done) {
        }

        @Override
        public void checkPermissions(RpcController controller, CheckPermissionsRequest request, RpcCallback<CheckPermissionsResponse> done) {
        }

        @Override
        public void hasPermission(RpcController controller, HasPermissionRequest request, RpcCallback<HasPermissionResponse> done) {
        }
    }

    private static class MockVisibilityController implements MasterCoprocessor , MasterObserver , RegionCoprocessor , RegionObserver , VisibilityLabelsService.Interface {
        @Override
        public void addLabels(RpcController controller, VisibilityLabelsRequest request, RpcCallback<VisibilityLabelsResponse> done) {
        }

        @Override
        public void setAuths(RpcController controller, SetAuthsRequest request, RpcCallback<VisibilityLabelsResponse> done) {
        }

        @Override
        public void clearAuths(RpcController controller, SetAuthsRequest request, RpcCallback<VisibilityLabelsResponse> done) {
        }

        @Override
        public void getAuths(RpcController controller, GetAuthsRequest request, RpcCallback<GetAuthsResponse> done) {
        }

        @Override
        public void listLabels(RpcController controller, ListLabelsRequest request, RpcCallback<ListLabelsResponse> done) {
        }
    }

    private MasterRpcServices masterServices;

    @Test
    public void testAccessControlServices() {
        MasterCoprocessor defaultImpl = new AccessController();
        MasterCoprocessor customImpl = new TestMasterCoprocessorServices.MockAccessController();
        MasterCoprocessor unrelatedImpl = new JMXListener();
        Assert.assertTrue(masterServices.checkCoprocessorWithService(Collections.singletonList(defaultImpl), Interface.class));
        Assert.assertTrue(masterServices.checkCoprocessorWithService(Collections.singletonList(customImpl), Interface.class));
        Assert.assertFalse(masterServices.checkCoprocessorWithService(Collections.emptyList(), Interface.class));
        Assert.assertFalse(masterServices.checkCoprocessorWithService(null, Interface.class));
        Assert.assertFalse(masterServices.checkCoprocessorWithService(Collections.singletonList(unrelatedImpl), Interface.class));
        Assert.assertTrue(masterServices.checkCoprocessorWithService(Arrays.asList(unrelatedImpl, customImpl), Interface.class));
        Assert.assertTrue(masterServices.checkCoprocessorWithService(Arrays.asList(unrelatedImpl, defaultImpl), Interface.class));
    }

    @Test
    public void testVisibilityLabelServices() {
        MasterCoprocessor defaultImpl = new VisibilityController();
        MasterCoprocessor customImpl = new TestMasterCoprocessorServices.MockVisibilityController();
        MasterCoprocessor unrelatedImpl = new JMXListener();
        Assert.assertTrue(masterServices.checkCoprocessorWithService(Collections.singletonList(defaultImpl), VisibilityLabelsService.Interface.class));
        Assert.assertTrue(masterServices.checkCoprocessorWithService(Collections.singletonList(customImpl), VisibilityLabelsService.Interface.class));
        Assert.assertFalse(masterServices.checkCoprocessorWithService(Collections.emptyList(), VisibilityLabelsService.Interface.class));
        Assert.assertFalse(masterServices.checkCoprocessorWithService(null, VisibilityLabelsService.Interface.class));
        Assert.assertFalse(masterServices.checkCoprocessorWithService(Collections.singletonList(unrelatedImpl), VisibilityLabelsService.Interface.class));
        Assert.assertTrue(masterServices.checkCoprocessorWithService(Arrays.asList(unrelatedImpl, customImpl), VisibilityLabelsService.Interface.class));
        Assert.assertTrue(masterServices.checkCoprocessorWithService(Arrays.asList(unrelatedImpl, defaultImpl), VisibilityLabelsService.Interface.class));
    }
}

