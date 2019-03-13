/**
 * Copyright 2018 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.cloud.bigtable.admin.v2;


import Instance.Type.DEVELOPMENT;
import Status.Code.NOT_FOUND;
import com.google.api.core.ApiFuture;
import com.google.api.core.ApiFutures;
import com.google.api.gax.grpc.GrpcStatusCode;
import com.google.api.gax.rpc.NotFoundException;
import com.google.api.gax.rpc.OperationCallable;
import com.google.api.gax.rpc.UnaryCallable;
import com.google.bigtable.admin.v2.AppProfile;
import com.google.bigtable.admin.v2.AppProfile.MultiClusterRoutingUseAny;
import com.google.bigtable.admin.v2.Cluster;
import com.google.bigtable.admin.v2.CreateAppProfileRequest;
import com.google.bigtable.admin.v2.CreateClusterMetadata;
import com.google.bigtable.admin.v2.CreateClusterRequest;
import com.google.bigtable.admin.v2.CreateInstanceMetadata;
import com.google.bigtable.admin.v2.CreateInstanceRequest;
import com.google.bigtable.admin.v2.DeleteAppProfileRequest;
import com.google.bigtable.admin.v2.DeleteClusterRequest;
import com.google.bigtable.admin.v2.DeleteInstanceRequest;
import com.google.bigtable.admin.v2.GetAppProfileRequest;
import com.google.bigtable.admin.v2.GetClusterRequest;
import com.google.bigtable.admin.v2.GetInstanceRequest;
import com.google.bigtable.admin.v2.Instance;
import com.google.bigtable.admin.v2.ListAppProfilesRequest;
import com.google.bigtable.admin.v2.ListClustersRequest;
import com.google.bigtable.admin.v2.ListClustersResponse;
import com.google.bigtable.admin.v2.ListInstancesRequest;
import com.google.bigtable.admin.v2.ListInstancesResponse;
import com.google.bigtable.admin.v2.PartialUpdateInstanceRequest;
import com.google.bigtable.admin.v2.StorageType.SSD;
import com.google.bigtable.admin.v2.UpdateAppProfileMetadata;
import com.google.bigtable.admin.v2.UpdateAppProfileRequest;
import com.google.bigtable.admin.v2.UpdateClusterMetadata;
import com.google.bigtable.admin.v2.UpdateInstanceMetadata;
import com.google.bigtable.admin.v2.com.google.bigtable.admin.v2.GetInstanceRequest;
import com.google.cloud.Identity;
import com.google.cloud.Role;
import com.google.cloud.bigtable.admin.v2.BaseBigtableInstanceAdminClient.ListAppProfilesPage;
import com.google.cloud.bigtable.admin.v2.BaseBigtableInstanceAdminClient.ListAppProfilesPagedResponse;
import com.google.cloud.bigtable.admin.v2.internal.NameUtil;
import com.google.cloud.bigtable.admin.v2.models.AppProfile.MultiClusterRoutingPolicy;
import com.google.cloud.bigtable.admin.v2.models.PartialListClustersException;
import com.google.cloud.bigtable.admin.v2.models.PartialListInstancesException;
import com.google.cloud.bigtable.admin.v2.models.UpdateInstanceRequest;
import com.google.cloud.bigtable.admin.v2.models.com.google.bigtable.admin.v2.AppProfile;
import com.google.cloud.bigtable.admin.v2.models.com.google.bigtable.admin.v2.Cluster;
import com.google.cloud.bigtable.admin.v2.models.com.google.bigtable.admin.v2.CreateAppProfileRequest;
import com.google.cloud.bigtable.admin.v2.models.com.google.bigtable.admin.v2.CreateClusterRequest;
import com.google.cloud.bigtable.admin.v2.models.com.google.bigtable.admin.v2.CreateInstanceRequest;
import com.google.cloud.bigtable.admin.v2.models.com.google.bigtable.admin.v2.Instance;
import com.google.cloud.bigtable.admin.v2.models.com.google.bigtable.admin.v2.UpdateAppProfileRequest;
import com.google.cloud.bigtable.admin.v2.stub.BigtableInstanceAdminStub;
import com.google.cloud.com.google.iam.v1.Policy;
import com.google.common.collect.Lists;
import com.google.common.io.BaseEncoding;
import com.google.iam.v1.GetIamPolicyRequest;
import com.google.iam.v1.Policy;
import com.google.iam.v1.SetIamPolicyRequest;
import com.google.iam.v1.TestIamPermissionsRequest;
import com.google.iam.v1.TestIamPermissionsResponse;
import com.google.protobuf.ByteString;
import com.google.protobuf.Empty;
import com.google.protobuf.FieldMask;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;


@RunWith(MockitoJUnitRunner.class)
public class BigtableInstanceAdminClientTest {
    private static final String PROJECT_ID = "my-project";

    private static final String INSTANCE_ID = "my-instance";

    private static final String CLUSTER_ID = "my-cluster";

    private static final String APP_PROFILE_ID = "my-app-profile";

    private static final String PROJECT_NAME = NameUtil.formatProjectName(BigtableInstanceAdminClientTest.PROJECT_ID);

    private static final String INSTANCE_NAME = NameUtil.formatInstanceName(BigtableInstanceAdminClientTest.PROJECT_ID, BigtableInstanceAdminClientTest.INSTANCE_ID);

    private static final String CLUSTER_NAME = NameUtil.formatClusterName(BigtableInstanceAdminClientTest.PROJECT_ID, BigtableInstanceAdminClientTest.INSTANCE_ID, BigtableInstanceAdminClientTest.CLUSTER_ID);

    private static final String APP_PROFILE_NAME = NameUtil.formatAppProfileName(BigtableInstanceAdminClientTest.PROJECT_ID, BigtableInstanceAdminClientTest.INSTANCE_ID, BigtableInstanceAdminClientTest.APP_PROFILE_ID);

    private BigtableInstanceAdminClient adminClient;

    @Mock
    private BigtableInstanceAdminStub mockStub;

    @Mock
    private OperationCallable<CreateInstanceRequest, Instance, CreateInstanceMetadata> mockCreateInstanceCallable;

    @Mock
    private OperationCallable<PartialUpdateInstanceRequest, Instance, UpdateInstanceMetadata> mockUpdateInstanceCallable;

    @Mock
    private UnaryCallable<GetInstanceRequest, Instance> mockGetInstanceCallable;

    @Mock
    private UnaryCallable<ListInstancesRequest, ListInstancesResponse> mockListInstancesCallable;

    @Mock
    private UnaryCallable<DeleteInstanceRequest, Empty> mockDeleteInstanceCallable;

    @Mock
    private OperationCallable<CreateClusterRequest, Cluster, CreateClusterMetadata> mockCreateClusterCallable;

    @Mock
    private UnaryCallable<GetClusterRequest, Cluster> mockGetClusterCallable;

    @Mock
    private UnaryCallable<ListClustersRequest, ListClustersResponse> mockListClustersCallable;

    @Mock
    private OperationCallable<Cluster, Cluster, UpdateClusterMetadata> mockUpdateClusterCallable;

    @Mock
    private UnaryCallable<DeleteClusterRequest, Empty> mockDeleteClusterCallable;

    @Mock
    private UnaryCallable<CreateAppProfileRequest, AppProfile> mockCreateAppProfileCallable;

    @Mock
    private UnaryCallable<GetAppProfileRequest, AppProfile> mockGetAppProfileCallable;

    @Mock
    private UnaryCallable<ListAppProfilesRequest, ListAppProfilesPagedResponse> mockListAppProfilesCallable;

    @Mock
    private OperationCallable<UpdateAppProfileRequest, AppProfile, UpdateAppProfileMetadata> mockUpdateAppProfileCallable;

    @Mock
    private UnaryCallable<DeleteAppProfileRequest, Empty> mockDeleteAppProfileCallable;

    @Mock
    private UnaryCallable<GetIamPolicyRequest, Policy> mockGetIamPolicyCallable;

    @Mock
    private UnaryCallable<SetIamPolicyRequest, Policy> mockSetIamPolicyCallable;

    @Mock
    private UnaryCallable<TestIamPermissionsRequest, TestIamPermissionsResponse> mockTestIamPermissionsCallable;

    @Test
    public void testProjectName() {
        assertThat(adminClient.getProjectId()).isEqualTo(BigtableInstanceAdminClientTest.PROJECT_ID);
    }

    @Test
    public void testClose() {
        adminClient.close();
        Mockito.verify(mockStub).close();
    }

    @Test
    public void testCreateInstance() {
        // Setup
        CreateInstanceRequest expectedRequest = com.google.bigtable.admin.v2.CreateInstanceRequest.newBuilder().setParent(BigtableInstanceAdminClientTest.PROJECT_NAME).setInstanceId(BigtableInstanceAdminClientTest.INSTANCE_ID).setInstance(com.google.bigtable.admin.v2.Instance.newBuilder().setType(com.google.bigtable.admin.v2.Instance.Type).setDisplayName(BigtableInstanceAdminClientTest.INSTANCE_ID)).putClusters("cluster1", com.google.bigtable.admin.v2.Cluster.newBuilder().setLocation("projects/my-project/locations/us-east1-c").setServeNodes(1).setDefaultStorageType(SSD).build()).build();
        Instance expectedResponse = com.google.bigtable.admin.v2.Instance.newBuilder().setName(BigtableInstanceAdminClientTest.INSTANCE_NAME).build();
        mockOperationResult(mockCreateInstanceCallable, expectedRequest, expectedResponse);
        // Execute
        com.google.cloud.bigtable.admin.v2.models.Instance actualResult = adminClient.createInstance(com.google.cloud.bigtable.admin.v2.models.CreateInstanceRequest.of(BigtableInstanceAdminClientTest.INSTANCE_ID).setType(DEVELOPMENT).addCluster("cluster1", "us-east1-c", 1, StorageType.SSD));
        // Verify
        assertThat(actualResult).isEqualTo(com.google.cloud.bigtable.admin.v2.models.Instance.fromProto(expectedResponse));
    }

    @Test
    public void testUpdateInstance() {
        // Setup
        PartialUpdateInstanceRequest expectedRequest = com.google.bigtable.admin.v2.PartialUpdateInstanceRequest.newBuilder().setUpdateMask(FieldMask.newBuilder().addPaths("display_name")).setInstance(com.google.bigtable.admin.v2.Instance.newBuilder().setName(BigtableInstanceAdminClientTest.INSTANCE_NAME).setDisplayName("new display name")).build();
        Instance expectedResponse = com.google.bigtable.admin.v2.Instance.newBuilder().setName(BigtableInstanceAdminClientTest.INSTANCE_NAME).build();
        mockOperationResult(mockUpdateInstanceCallable, expectedRequest, expectedResponse);
        // Execute
        com.google.cloud.bigtable.admin.v2.models.Instance actualResult = adminClient.updateInstance(UpdateInstanceRequest.of(BigtableInstanceAdminClientTest.INSTANCE_ID).setDisplayName("new display name"));
        // Verify
        assertThat(actualResult).isEqualTo(com.google.cloud.bigtable.admin.v2.models.Instance.fromProto(expectedResponse));
    }

    @Test
    public void testGetInstance() {
        // Setup
        GetInstanceRequest expectedRequest = com.google.bigtable.admin.v2.GetInstanceRequest.newBuilder().setName(BigtableInstanceAdminClientTest.INSTANCE_NAME).build();
        Instance expectedResponse = com.google.bigtable.admin.v2.Instance.newBuilder().setName(BigtableInstanceAdminClientTest.INSTANCE_NAME).build();
        Mockito.when(mockGetInstanceCallable.futureCall(expectedRequest)).thenReturn(ApiFutures.immediateFuture(expectedResponse));
        // Execute
        com.google.cloud.bigtable.admin.v2.models.Instance actualResult = adminClient.getInstance(BigtableInstanceAdminClientTest.INSTANCE_ID);
        // Verify
        assertThat(actualResult).isEqualTo(com.google.cloud.bigtable.admin.v2.models.Instance.fromProto(expectedResponse));
    }

    @Test
    public void testListInstances() {
        // Setup
        ListInstancesRequest expectedRequest = com.google.bigtable.admin.v2.ListInstancesRequest.newBuilder().setParent(BigtableInstanceAdminClientTest.PROJECT_NAME).build();
        ListInstancesResponse expectedResponse = com.google.bigtable.admin.v2.ListInstancesResponse.newBuilder().addInstances(com.google.bigtable.admin.v2.Instance.newBuilder().setName(((BigtableInstanceAdminClientTest.INSTANCE_NAME) + "1")).build()).addInstances(com.google.bigtable.admin.v2.Instance.newBuilder().setName(((BigtableInstanceAdminClientTest.INSTANCE_NAME) + "2")).build()).build();
        Mockito.when(mockListInstancesCallable.futureCall(expectedRequest)).thenReturn(ApiFutures.immediateFuture(expectedResponse));
        // Execute
        List<com.google.cloud.bigtable.admin.v2.models.Instance> actualResult = adminClient.listInstances();
        // Verify
        assertThat(actualResult).containsExactly(com.google.cloud.bigtable.admin.v2.models.Instance.fromProto(expectedResponse.getInstances(0)), com.google.cloud.bigtable.admin.v2.models.Instance.fromProto(expectedResponse.getInstances(1)));
    }

    @Test
    public void testListInstancesFailedZone() {
        // Setup
        ListInstancesRequest expectedRequest = com.google.bigtable.admin.v2.ListInstancesRequest.newBuilder().setParent(BigtableInstanceAdminClientTest.PROJECT_NAME).build();
        ListInstancesResponse expectedResponse = com.google.bigtable.admin.v2.ListInstancesResponse.newBuilder().addInstances(com.google.bigtable.admin.v2.Instance.newBuilder().setName(((BigtableInstanceAdminClientTest.INSTANCE_NAME) + "1")).build()).addFailedLocations(NameUtil.formatLocationName(BigtableInstanceAdminClientTest.PROJECT_ID, "us-east1-d")).build();
        Mockito.when(mockListInstancesCallable.futureCall(expectedRequest)).thenReturn(ApiFutures.immediateFuture(expectedResponse));
        // Execute
        Exception actualError = null;
        try {
            adminClient.listInstances();
        } catch (Exception e) {
            actualError = e;
        }
        // Verify
        assertThat(actualError).isInstanceOf(PartialListInstancesException.class);
        assert actualError != null;
        PartialListInstancesException partialListError = ((PartialListInstancesException) (actualError));
        assertThat(partialListError.getInstances()).containsExactly(com.google.cloud.bigtable.admin.v2.models.Instance.fromProto(expectedResponse.getInstances(0)));
        assertThat(partialListError.getUnavailableZones()).containsExactly("us-east1-d");
    }

    @Test
    public void testDeleteInstance() {
        // Setup
        DeleteInstanceRequest expectedRequest = com.google.bigtable.admin.v2.DeleteInstanceRequest.newBuilder().setName(BigtableInstanceAdminClientTest.INSTANCE_NAME).build();
        final AtomicBoolean wasCalled = new AtomicBoolean(false);
        Mockito.when(mockDeleteInstanceCallable.futureCall(expectedRequest)).thenAnswer(new Answer<ApiFuture<Empty>>() {
            @Override
            public ApiFuture<Empty> answer(InvocationOnMock invocationOnMock) {
                wasCalled.set(true);
                return ApiFutures.immediateFuture(Empty.getDefaultInstance());
            }
        });
        // Execute
        adminClient.deleteInstance(BigtableInstanceAdminClientTest.INSTANCE_ID);
        // Verify
        assertThat(wasCalled.get()).isTrue();
    }

    @Test
    public void testCreateCluster() {
        // Setup
        CreateClusterRequest expectedRequest = com.google.bigtable.admin.v2.CreateClusterRequest.newBuilder().setParent(BigtableInstanceAdminClientTest.INSTANCE_NAME).setClusterId(BigtableInstanceAdminClientTest.CLUSTER_ID).setCluster(com.google.bigtable.admin.v2.Cluster.newBuilder().setLocation("projects/my-project/locations/us-east1-c").setServeNodes(3).setDefaultStorageType(SSD)).build();
        Cluster expectedResponse = com.google.bigtable.admin.v2.Cluster.newBuilder().setName(BigtableInstanceAdminClientTest.CLUSTER_NAME).build();
        mockOperationResult(mockCreateClusterCallable, expectedRequest, expectedResponse);
        // Execute
        com.google.cloud.bigtable.admin.v2.models.Cluster actualResult = adminClient.createCluster(com.google.cloud.bigtable.admin.v2.models.CreateClusterRequest.of(BigtableInstanceAdminClientTest.INSTANCE_ID, BigtableInstanceAdminClientTest.CLUSTER_ID).setZone("us-east1-c").setServeNodes(3).setStorageType(StorageType.SSD));
        // Verify
        assertThat(actualResult).isEqualTo(com.google.cloud.bigtable.admin.v2.models.Cluster.fromProto(expectedResponse));
    }

    @Test
    public void testGetCluster() {
        // Setup
        GetClusterRequest expectedRequest = com.google.bigtable.admin.v2.GetClusterRequest.newBuilder().setName(BigtableInstanceAdminClientTest.CLUSTER_NAME).build();
        Cluster expectedResponse = com.google.bigtable.admin.v2.Cluster.newBuilder().setName(BigtableInstanceAdminClientTest.CLUSTER_NAME).build();
        Mockito.when(mockGetClusterCallable.futureCall(expectedRequest)).thenReturn(ApiFutures.immediateFuture(expectedResponse));
        // Execute
        com.google.cloud.bigtable.admin.v2.models.Cluster actualResult = adminClient.getCluster(BigtableInstanceAdminClientTest.INSTANCE_ID, BigtableInstanceAdminClientTest.CLUSTER_ID);
        // Verify
        assertThat(actualResult).isEqualTo(com.google.cloud.bigtable.admin.v2.models.Cluster.fromProto(expectedResponse));
    }

    @Test
    public void testListClusters() {
        // Setup
        ListClustersRequest expectedRequest = com.google.bigtable.admin.v2.ListClustersRequest.newBuilder().setParent(BigtableInstanceAdminClientTest.INSTANCE_NAME).build();
        ListClustersResponse expectedResponse = com.google.bigtable.admin.v2.ListClustersResponse.newBuilder().addClusters(com.google.bigtable.admin.v2.Cluster.newBuilder().setName(((BigtableInstanceAdminClientTest.CLUSTER_NAME) + "1"))).addClusters(com.google.bigtable.admin.v2.Cluster.newBuilder().setName(((BigtableInstanceAdminClientTest.CLUSTER_NAME) + "2"))).build();
        Mockito.when(mockListClustersCallable.futureCall(expectedRequest)).thenReturn(ApiFutures.immediateFuture(expectedResponse));
        // Execute
        List<com.google.cloud.bigtable.admin.v2.models.Cluster> actualResult = adminClient.listClusters(BigtableInstanceAdminClientTest.INSTANCE_ID);
        // Verify
        assertThat(actualResult).containsExactly(com.google.cloud.bigtable.admin.v2.models.Cluster.fromProto(expectedResponse.getClusters(0)), com.google.cloud.bigtable.admin.v2.models.Cluster.fromProto(expectedResponse.getClusters(1)));
    }

    @Test
    public void testListClustersFailedZone() {
        // Setup
        ListClustersRequest expectedRequest = com.google.bigtable.admin.v2.ListClustersRequest.newBuilder().setParent(BigtableInstanceAdminClientTest.INSTANCE_NAME).build();
        ListClustersResponse expectedResponse = com.google.bigtable.admin.v2.ListClustersResponse.newBuilder().addClusters(com.google.bigtable.admin.v2.Cluster.newBuilder().setName(BigtableInstanceAdminClientTest.CLUSTER_NAME)).addFailedLocations(NameUtil.formatLocationName(BigtableInstanceAdminClientTest.PROJECT_ID, "us-east1-c")).build();
        Mockito.when(mockListClustersCallable.futureCall(expectedRequest)).thenReturn(ApiFutures.immediateFuture(expectedResponse));
        // Execute
        Exception actualError = null;
        try {
            adminClient.listClusters(BigtableInstanceAdminClientTest.INSTANCE_ID);
        } catch (Exception e) {
            actualError = e;
        }
        // Verify
        assertThat(actualError).isInstanceOf(PartialListClustersException.class);
        assert actualError != null;
        PartialListClustersException partialListError = ((PartialListClustersException) (actualError));
        assertThat(partialListError.getClusters()).containsExactly(com.google.cloud.bigtable.admin.v2.models.Cluster.fromProto(expectedResponse.getClusters(0)));
        assertThat(partialListError.getUnavailableZones()).containsExactly("us-east1-c");
    }

    @Test
    public void testResizeCluster() {
        // Setup
        Cluster expectedRequest = com.google.bigtable.admin.v2.Cluster.newBuilder().setName(BigtableInstanceAdminClientTest.CLUSTER_NAME).setServeNodes(30).build();
        Cluster expectedResponse = com.google.bigtable.admin.v2.Cluster.newBuilder().setName(BigtableInstanceAdminClientTest.CLUSTER_NAME).setLocation(NameUtil.formatLocationName(BigtableInstanceAdminClientTest.PROJECT_ID, "us-east1-c")).setServeNodes(30).build();
        mockOperationResult(mockUpdateClusterCallable, expectedRequest, expectedResponse);
        // Execute
        com.google.cloud.bigtable.admin.v2.models.Cluster actualResult = adminClient.resizeCluster(BigtableInstanceAdminClientTest.INSTANCE_ID, BigtableInstanceAdminClientTest.CLUSTER_ID, 30);
        // Verify
        assertThat(actualResult).isEqualTo(com.google.cloud.bigtable.admin.v2.models.Cluster.fromProto(expectedResponse));
    }

    @Test
    public void testDeleteCluster() {
        // Setup
        DeleteClusterRequest expectedRequest = com.google.bigtable.admin.v2.DeleteClusterRequest.newBuilder().setName(BigtableInstanceAdminClientTest.CLUSTER_NAME).build();
        final AtomicBoolean wasCalled = new AtomicBoolean(false);
        Mockito.when(mockDeleteClusterCallable.futureCall(expectedRequest)).thenAnswer(new Answer<ApiFuture<Empty>>() {
            @Override
            public ApiFuture<Empty> answer(InvocationOnMock invocationOnMock) {
                wasCalled.set(true);
                return ApiFutures.immediateFuture(Empty.getDefaultInstance());
            }
        });
        // Execute
        adminClient.deleteCluster(BigtableInstanceAdminClientTest.INSTANCE_ID, BigtableInstanceAdminClientTest.CLUSTER_ID);
        // Verify
        assertThat(wasCalled.get()).isTrue();
    }

    @Test
    public void testCreateAppProfile() {
        // Setup
        CreateAppProfileRequest expectedRequest = com.google.bigtable.admin.v2.CreateAppProfileRequest.newBuilder().setParent(NameUtil.formatInstanceName(BigtableInstanceAdminClientTest.PROJECT_ID, BigtableInstanceAdminClientTest.INSTANCE_ID)).setAppProfileId(BigtableInstanceAdminClientTest.APP_PROFILE_ID).setAppProfile(com.google.bigtable.admin.v2.AppProfile.newBuilder().setDescription("my description").setMultiClusterRoutingUseAny(MultiClusterRoutingUseAny.getDefaultInstance())).build();
        AppProfile expectedResponse = com.google.bigtable.admin.v2.AppProfile.newBuilder().setName(BigtableInstanceAdminClientTest.APP_PROFILE_NAME).setDescription("my description").setMultiClusterRoutingUseAny(MultiClusterRoutingUseAny.getDefaultInstance()).build();
        Mockito.when(mockCreateAppProfileCallable.futureCall(expectedRequest)).thenReturn(ApiFutures.immediateFuture(expectedResponse));
        // Execute
        com.google.cloud.bigtable.admin.v2.models.AppProfile actualResult = adminClient.createAppProfile(com.google.cloud.bigtable.admin.v2.models.CreateAppProfileRequest.of(BigtableInstanceAdminClientTest.INSTANCE_ID, BigtableInstanceAdminClientTest.APP_PROFILE_ID).setDescription("my description").setRoutingPolicy(MultiClusterRoutingPolicy.of()));
        // Verify
        assertThat(actualResult).isEqualTo(com.google.cloud.bigtable.admin.v2.models.AppProfile.fromProto(expectedResponse));
    }

    @Test
    public void testGetAppProfile() {
        // Setup
        GetAppProfileRequest expectedRequest = com.google.bigtable.admin.v2.GetAppProfileRequest.newBuilder().setName(BigtableInstanceAdminClientTest.APP_PROFILE_NAME).build();
        AppProfile expectedResponse = com.google.bigtable.admin.v2.AppProfile.newBuilder().setName(BigtableInstanceAdminClientTest.APP_PROFILE_NAME).setDescription("my description").setMultiClusterRoutingUseAny(MultiClusterRoutingUseAny.getDefaultInstance()).build();
        Mockito.when(mockGetAppProfileCallable.futureCall(expectedRequest)).thenReturn(ApiFutures.immediateFuture(expectedResponse));
        // Execute
        com.google.cloud.bigtable.admin.v2.models.AppProfile actualResult = adminClient.getAppProfile(BigtableInstanceAdminClientTest.INSTANCE_ID, BigtableInstanceAdminClientTest.APP_PROFILE_ID);
        // Verify
        assertThat(actualResult).isEqualTo(com.google.cloud.bigtable.admin.v2.models.AppProfile.fromProto(expectedResponse));
    }

    @Test
    public void testListAppProfiles() {
        // Setup
        ListAppProfilesRequest expectedRequest = com.google.bigtable.admin.v2.ListAppProfilesRequest.newBuilder().setParent(NameUtil.formatInstanceName(BigtableInstanceAdminClientTest.PROJECT_ID, BigtableInstanceAdminClientTest.INSTANCE_ID)).build();
        // 3 AppProfiles spread across 2 pages
        List<AppProfile> expectedProtos = Lists.newArrayList();
        for (int i = 0; i < 3; i++) {
            expectedProtos.add(com.google.bigtable.admin.v2.AppProfile.newBuilder().setName(((BigtableInstanceAdminClientTest.APP_PROFILE_NAME) + i)).setDescription(("profile" + i)).setMultiClusterRoutingUseAny(MultiClusterRoutingUseAny.getDefaultInstance()).build());
        }
        // 2 on the first page
        ListAppProfilesPage page0 = Mockito.mock(ListAppProfilesPage.class);
        Mockito.when(page0.getValues()).thenReturn(expectedProtos.subList(0, 2));
        Mockito.when(page0.getNextPageToken()).thenReturn("next-page");
        Mockito.when(page0.hasNextPage()).thenReturn(true);
        // 1 on the last page
        ListAppProfilesPage page1 = Mockito.mock(ListAppProfilesPage.class);
        Mockito.when(page1.getValues()).thenReturn(expectedProtos.subList(2, 3));
        // Link page0 to page1
        Mockito.when(page0.getNextPageAsync()).thenReturn(ApiFutures.immediateFuture(page1));
        // Link page to the response
        ListAppProfilesPagedResponse response0 = Mockito.mock(ListAppProfilesPagedResponse.class);
        Mockito.when(response0.getPage()).thenReturn(page0);
        Mockito.when(mockListAppProfilesCallable.futureCall(expectedRequest)).thenReturn(ApiFutures.immediateFuture(response0));
        // Execute
        List<com.google.cloud.bigtable.admin.v2.models.AppProfile> actualResults = adminClient.listAppProfiles(BigtableInstanceAdminClientTest.INSTANCE_ID);
        // Verify
        List<com.google.cloud.bigtable.admin.v2.models.AppProfile> expectedResults = Lists.newArrayList();
        for (AppProfile expectedProto : expectedProtos) {
            expectedResults.add(com.google.cloud.bigtable.admin.v2.models.AppProfile.fromProto(expectedProto));
        }
        assertThat(actualResults).containsExactlyElementsIn(expectedResults);
    }

    @Test
    public void testUpdateAppProfile() {
        // Setup
        UpdateAppProfileRequest expectedRequest = com.google.bigtable.admin.v2.UpdateAppProfileRequest.newBuilder().setAppProfile(com.google.bigtable.admin.v2.AppProfile.newBuilder().setName(BigtableInstanceAdminClientTest.APP_PROFILE_NAME).setDescription("updated description")).setUpdateMask(FieldMask.newBuilder().addPaths("description")).build();
        AppProfile expectedResponse = com.google.bigtable.admin.v2.AppProfile.newBuilder().setName(BigtableInstanceAdminClientTest.APP_PROFILE_NAME).setDescription("updated description").setMultiClusterRoutingUseAny(MultiClusterRoutingUseAny.getDefaultInstance()).build();
        mockOperationResult(mockUpdateAppProfileCallable, expectedRequest, expectedResponse);
        // Execute
        com.google.cloud.bigtable.admin.v2.models.AppProfile actualResult = adminClient.updateAppProfile(com.google.cloud.bigtable.admin.v2.models.UpdateAppProfileRequest.of(BigtableInstanceAdminClientTest.INSTANCE_ID, BigtableInstanceAdminClientTest.APP_PROFILE_ID).setDescription("updated description"));
        // Verify
        assertThat(actualResult).isEqualTo(com.google.cloud.bigtable.admin.v2.models.AppProfile.fromProto(expectedResponse));
    }

    @Test
    public void testDeleteAppProfile() {
        // Setup
        DeleteAppProfileRequest expectedRequest = com.google.bigtable.admin.v2.DeleteAppProfileRequest.newBuilder().setName(BigtableInstanceAdminClientTest.APP_PROFILE_NAME).build();
        final AtomicBoolean wasCalled = new AtomicBoolean(false);
        Mockito.when(mockDeleteAppProfileCallable.futureCall(expectedRequest)).thenAnswer(new Answer<ApiFuture<Empty>>() {
            @Override
            public ApiFuture<Empty> answer(InvocationOnMock invocationOnMock) {
                wasCalled.set(true);
                return ApiFutures.immediateFuture(Empty.getDefaultInstance());
            }
        });
        // Execute
        adminClient.deleteAppProfile(BigtableInstanceAdminClientTest.INSTANCE_ID, BigtableInstanceAdminClientTest.APP_PROFILE_ID);
        // Verify
        assertThat(wasCalled.get()).isTrue();
    }

    @Test
    public void testGetIamPolicy() {
        // Setup
        GetIamPolicyRequest expectedRequest = com.google.iam.v1.GetIamPolicyRequest.newBuilder().setResource(NameUtil.formatInstanceName(BigtableInstanceAdminClientTest.PROJECT_ID, BigtableInstanceAdminClientTest.INSTANCE_ID)).build();
        Policy expectedResponse = com.google.iam.v1.Policy.newBuilder().addBindings(com.google.iam.v1.Binding.newBuilder().setRole("roles/bigtable.user").addMembers("user:someone@example.com")).setEtag(ByteString.copyFromUtf8("my-etag")).build();
        Mockito.when(mockGetIamPolicyCallable.futureCall(expectedRequest)).thenReturn(ApiFutures.immediateFuture(expectedResponse));
        // Execute
        com.google.cloud.Policy actualResult = adminClient.getIamPolicy(BigtableInstanceAdminClientTest.INSTANCE_ID);
        // Verify
        assertThat(actualResult).isEqualTo(com.google.cloud.Policy.newBuilder().addIdentity(Role.of("bigtable.user"), Identity.user("someone@example.com")).setEtag(BaseEncoding.base64().encode("my-etag".getBytes())).build());
    }

    @Test
    public void testSetIamPolicy() {
        // Setup
        SetIamPolicyRequest expectedRequest = com.google.iam.v1.SetIamPolicyRequest.newBuilder().setResource(NameUtil.formatInstanceName(BigtableInstanceAdminClientTest.PROJECT_ID, BigtableInstanceAdminClientTest.INSTANCE_ID)).setPolicy(com.google.iam.v1.Policy.newBuilder().addBindings(com.google.iam.v1.Binding.newBuilder().setRole("roles/bigtable.user").addMembers("user:someone@example.com"))).build();
        Policy expectedResponse = com.google.iam.v1.Policy.newBuilder().addBindings(com.google.iam.v1.Binding.newBuilder().setRole("roles/bigtable.user").addMembers("user:someone@example.com")).setEtag(ByteString.copyFromUtf8("my-etag")).build();
        Mockito.when(mockSetIamPolicyCallable.futureCall(expectedRequest)).thenReturn(ApiFutures.immediateFuture(expectedResponse));
        // Execute
        com.google.cloud.Policy actualResult = adminClient.setIamPolicy(BigtableInstanceAdminClientTest.INSTANCE_ID, com.google.cloud.Policy.newBuilder().addIdentity(Role.of("bigtable.user"), Identity.user("someone@example.com")).build());
        // Verify
        assertThat(actualResult).isEqualTo(com.google.cloud.Policy.newBuilder().addIdentity(Role.of("bigtable.user"), Identity.user("someone@example.com")).setEtag(BaseEncoding.base64().encode("my-etag".getBytes())).build());
    }

    @Test
    public void testTestIamPermissions() {
        // Setup
        TestIamPermissionsRequest expectedRequest = com.google.iam.v1.TestIamPermissionsRequest.newBuilder().setResource(NameUtil.formatInstanceName(BigtableInstanceAdminClientTest.PROJECT_ID, BigtableInstanceAdminClientTest.INSTANCE_ID)).addPermissions("bigtable.tables.readRows").build();
        TestIamPermissionsResponse expectedResponse = com.google.iam.v1.TestIamPermissionsResponse.newBuilder().addPermissions("bigtable.tables.readRows").build();
        Mockito.when(mockTestIamPermissionsCallable.futureCall(expectedRequest)).thenReturn(ApiFutures.immediateFuture(expectedResponse));
        // Execute
        List<String> actualResult = adminClient.testIamPermission(BigtableInstanceAdminClientTest.INSTANCE_ID, "bigtable.tables.readRows");
        // Verify
        assertThat(actualResult).containsExactly("bigtable.tables.readRows");
    }

    @Test
    public void testExistsTrue() {
        // Setup
        Instance expectedResponse = com.google.bigtable.admin.v2.Instance.newBuilder().setName(NameUtil.formatInstanceName(BigtableInstanceAdminClientTest.PROJECT_ID, BigtableInstanceAdminClientTest.INSTANCE_ID)).build();
        Mockito.when(mockGetInstanceCallable.futureCall(Matchers.any(GetInstanceRequest.class))).thenReturn(ApiFutures.immediateFuture(expectedResponse));
        // Execute
        boolean found = adminClient.exists(BigtableInstanceAdminClientTest.INSTANCE_ID);
        // Verify
        assertThat(found).isTrue();
    }

    @Test
    public void testExistsFalse() {
        // Setup
        NotFoundException exception = new NotFoundException("fake-error", null, GrpcStatusCode.of(NOT_FOUND), false);
        Mockito.when(mockGetInstanceCallable.futureCall(Matchers.any(GetInstanceRequest.class))).thenReturn(ApiFutures.<Instance>immediateFailedFuture(exception));
        // Execute
        boolean found = adminClient.exists(BigtableInstanceAdminClientTest.INSTANCE_ID);
        // Verify
        assertThat(found).isFalse();
    }
}

