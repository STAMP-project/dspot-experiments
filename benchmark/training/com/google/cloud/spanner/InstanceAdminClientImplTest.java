/**
 * Copyright 2017 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.cloud.spanner;


import InstanceInfo.InstanceField.NODE_COUNT;
import SpannerImpl.InstanceAdminClientImpl;
import com.google.api.gax.longrunning.OperationFuture;
import com.google.cloud.spanner.spi.v1.SpannerRpc;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.protobuf.FieldMask;
import com.google.spanner.admin.instance.v1.CreateInstanceMetadata;
import com.google.spanner.admin.instance.v1.Instance;
import com.google.spanner.admin.instance.v1.InstanceConfig;
import com.google.spanner.admin.instance.v1.UpdateInstanceMetadata;
import com.google.spanner.admin.instance.v1.com.google.cloud.spanner.InstanceConfig;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.Mockito;


/**
 * Unit tests for {@link com.google.cloud.spanner.SpannerImpl.InstanceAdminClientImpl}.
 */
@RunWith(JUnit4.class)
public class InstanceAdminClientImplTest {
    private static final String PROJECT_ID = "my-project";

    private static final String INSTANCE_ID = "my-instance";

    private static final String INSTANCE_NAME = "projects/my-project/instances/my-instance";

    private static final String INSTANCE_NAME2 = "projects/my-project/instances/my-instance2";

    private static final String CONFIG_ID = "my-config";

    private static final String CONFIG_NAME = "projects/my-project/instanceConfigs/my-config";

    private static final String CONFIG_NAME2 = "projects/my-project/instanceConfigs/my-config2";

    @Mock
    SpannerRpc rpc;

    @Mock
    DatabaseAdminClient dbClient;

    InstanceAdminClientImpl client;

    @Test
    public void getInstanceConfig() {
        Mockito.when(rpc.getInstanceConfig(InstanceAdminClientImplTest.CONFIG_NAME)).thenReturn(InstanceConfig.newBuilder().setName(InstanceAdminClientImplTest.CONFIG_NAME).build());
        assertThat(client.getInstanceConfig(InstanceAdminClientImplTest.CONFIG_ID).getId().getName()).isEqualTo(InstanceAdminClientImplTest.CONFIG_NAME);
    }

    @Test
    public void listInstanceConfigs() {
        String nextToken = "token";
        Mockito.when(rpc.listInstanceConfigs(1, null)).thenReturn(new com.google.cloud.spanner.spi.v1.SpannerRpc.Paginated<InstanceConfig>(ImmutableList.of(InstanceConfig.newBuilder().setName(InstanceAdminClientImplTest.CONFIG_NAME).build()), nextToken));
        Mockito.when(rpc.listInstanceConfigs(1, nextToken)).thenReturn(new com.google.cloud.spanner.spi.v1.SpannerRpc.Paginated<InstanceConfig>(ImmutableList.of(InstanceConfig.newBuilder().setName(InstanceAdminClientImplTest.CONFIG_NAME2).build()), ""));
        List<com.google.cloud.spanner.InstanceConfig> configs = Lists.newArrayList(client.listInstanceConfigs(Options.pageSize(1)).iterateAll());
        assertThat(configs.get(0).getId().getName()).isEqualTo(InstanceAdminClientImplTest.CONFIG_NAME);
        assertThat(configs.get(1).getId().getName()).isEqualTo(InstanceAdminClientImplTest.CONFIG_NAME2);
        assertThat(configs.size()).isEqualTo(2);
    }

    @Test
    public void createInstance() throws Exception {
        OperationFuture<Instance, CreateInstanceMetadata> rawOperationFuture = OperationFutureUtil.immediateOperationFuture("createInstance", getInstanceProto(), CreateInstanceMetadata.getDefaultInstance());
        Mockito.when(rpc.createInstance(("projects/" + (InstanceAdminClientImplTest.PROJECT_ID)), InstanceAdminClientImplTest.INSTANCE_ID, getInstanceProto())).thenReturn(rawOperationFuture);
        OperationFuture<Instance, CreateInstanceMetadata> op = client.createInstance(InstanceInfo.newBuilder(InstanceId.of(InstanceAdminClientImplTest.PROJECT_ID, InstanceAdminClientImplTest.INSTANCE_ID)).setInstanceConfigId(InstanceConfigId.of(InstanceAdminClientImplTest.PROJECT_ID, InstanceAdminClientImplTest.CONFIG_ID)).setNodeCount(1).build());
        assertThat(op.isDone()).isTrue();
        assertThat(op.get().getId().getName()).isEqualTo(InstanceAdminClientImplTest.INSTANCE_NAME);
    }

    @Test
    public void getInstance() {
        Mockito.when(rpc.getInstance(InstanceAdminClientImplTest.INSTANCE_NAME)).thenReturn(getInstanceProto());
        assertThat(client.getInstance(InstanceAdminClientImplTest.INSTANCE_ID).getId().getName()).isEqualTo(InstanceAdminClientImplTest.INSTANCE_NAME);
    }

    @Test
    public void dropInstance() {
        client.deleteInstance(InstanceAdminClientImplTest.INSTANCE_ID);
        Mockito.verify(rpc).deleteInstance(InstanceAdminClientImplTest.INSTANCE_NAME);
    }

    @Test
    public void updateInstanceMetadata() throws Exception {
        Instance instance = com.google.spanner.admin.instance.v1.Instance.newBuilder().setName(InstanceAdminClientImplTest.INSTANCE_NAME).setConfig(InstanceAdminClientImplTest.CONFIG_NAME).setNodeCount(2).build();
        OperationFuture<Instance, UpdateInstanceMetadata> rawOperationFuture = OperationFutureUtil.immediateOperationFuture("updateInstance", getInstanceProto(), UpdateInstanceMetadata.getDefaultInstance());
        Mockito.when(rpc.updateInstance(instance, FieldMask.newBuilder().addPaths("node_count").build())).thenReturn(rawOperationFuture);
        InstanceInfo instanceInfo = InstanceInfo.newBuilder(InstanceId.of(InstanceAdminClientImplTest.INSTANCE_NAME)).setInstanceConfigId(InstanceConfigId.of(InstanceAdminClientImplTest.CONFIG_NAME)).setNodeCount(2).build();
        OperationFuture<Instance, UpdateInstanceMetadata> op = client.updateInstance(instanceInfo, NODE_COUNT);
        assertThat(op.isDone()).isTrue();
        assertThat(op.get().getId().getName()).isEqualTo(InstanceAdminClientImplTest.INSTANCE_NAME);
    }

    @Test
    public void listInstances() {
        String nextToken = "token";
        String filter = "env:dev";
        Mockito.when(rpc.listInstances(1, null, filter)).thenReturn(new com.google.cloud.spanner.spi.v1.SpannerRpc.Paginated<Instance>(ImmutableList.of(getInstanceProto()), nextToken));
        Mockito.when(rpc.listInstances(1, nextToken, filter)).thenReturn(new com.google.cloud.spanner.spi.v1.SpannerRpc.Paginated<Instance>(ImmutableList.of(getAnotherInstanceProto()), ""));
        List<Instance> instances = Lists.newArrayList(client.listInstances(Options.pageSize(1), Options.filter(filter)).iterateAll());
        assertThat(instances.get(0).getId().getName()).isEqualTo(InstanceAdminClientImplTest.INSTANCE_NAME);
        assertThat(instances.get(1).getId().getName()).isEqualTo(InstanceAdminClientImplTest.INSTANCE_NAME2);
        assertThat(instances.size()).isEqualTo(2);
    }
}

