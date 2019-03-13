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
package com.google.cloud.bigtable.data.v2.stub;


import Code.ABORTED;
import Code.DEADLINE_EXCEEDED;
import Duration.ZERO;
import EnhancedBigtableStubSettings.Builder;
import com.google.api.gax.batching.BatchingSettings;
import com.google.api.gax.core.CredentialsProvider;
import com.google.api.gax.grpc.InstantiatingGrpcChannelProvider;
import com.google.api.gax.retrying.RetrySettings;
import com.google.api.gax.rpc.WatchdogProvider;
import com.google.cloud.bigtable.data.v2.models.ConditionalRowMutation;
import com.google.cloud.bigtable.data.v2.models.KeyOffset;
import com.google.cloud.bigtable.data.v2.models.Query;
import com.google.cloud.bigtable.data.v2.models.Row;
import com.google.cloud.bigtable.data.v2.models.RowMutation;
import com.google.common.collect.Range;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;
import org.threeten.bp.Duration;


@RunWith(JUnit4.class)
public class EnhancedBigtableStubSettingsTest {
    @Test
    public void instanceNameIsRequiredTest() {
        EnhancedBigtableStubSettings.Builder builder = EnhancedBigtableStubSettings.newBuilder();
        Throwable error = null;
        try {
            builder.build();
        } catch (Throwable t) {
            error = t;
        }
        assertThat(error).isInstanceOf(IllegalStateException.class);
    }

    @Test
    public void settingsAreNotLostTest() {
        String projectId = "my-project";
        String instanceId = "my-instance";
        String appProfileId = "my-app-profile-id";
        String endpoint = "some.other.host:123";
        CredentialsProvider credentialsProvider = Mockito.mock(CredentialsProvider.class);
        WatchdogProvider watchdogProvider = Mockito.mock(WatchdogProvider.class);
        Duration watchdogInterval = Duration.ofSeconds(12);
        EnhancedBigtableStubSettings.Builder builder = EnhancedBigtableStubSettings.newBuilder().setProjectId(projectId).setInstanceId(instanceId).setAppProfileId(appProfileId).setEndpoint(endpoint).setCredentialsProvider(credentialsProvider).setStreamWatchdogProvider(watchdogProvider).setStreamWatchdogCheckInterval(watchdogInterval);
        verifyBuilder(builder, projectId, instanceId, appProfileId, endpoint, credentialsProvider, watchdogProvider, watchdogInterval);
        verifySettings(builder.build(), projectId, instanceId, appProfileId, endpoint, credentialsProvider, watchdogProvider, watchdogInterval);
        verifyBuilder(builder.build().toBuilder(), projectId, instanceId, appProfileId, endpoint, credentialsProvider, watchdogProvider, watchdogInterval);
    }

    @Test
    public void multipleChannelsByDefaultTest() {
        String dummyProjectId = "my-project";
        String dummyInstanceId = "my-instance";
        EnhancedBigtableStubSettings.Builder builder = EnhancedBigtableStubSettings.newBuilder().setProjectId(dummyProjectId).setInstanceId(dummyInstanceId);
        InstantiatingGrpcChannelProvider provider = ((InstantiatingGrpcChannelProvider) (builder.getTransportChannelProvider()));
        assertThat(provider.toBuilder().getPoolSize()).isGreaterThan(1);
    }

    @Test
    public void readRowsIsNotLostTest() {
        String dummyProjectId = "my-project";
        String dummyInstanceId = "my-instance";
        EnhancedBigtableStubSettings.Builder builder = EnhancedBigtableStubSettings.newBuilder().setProjectId(dummyProjectId).setInstanceId(dummyInstanceId);
        RetrySettings retrySettings = RetrySettings.newBuilder().setMaxAttempts(10).setTotalTimeout(Duration.ofHours(1)).setInitialRpcTimeout(Duration.ofSeconds(10)).setRpcTimeoutMultiplier(1).setMaxRpcTimeout(Duration.ofSeconds(10)).setJittered(true).build();
        builder.readRowsSettings().setIdleTimeout(Duration.ofMinutes(5)).setRetryableCodes(ABORTED, DEADLINE_EXCEEDED).setRetrySettings(retrySettings).build();
        // Point readRow settings must match streaming settings
        builder.readRowSettings().setRetryableCodes(ABORTED, DEADLINE_EXCEEDED);
        assertThat(builder.readRowsSettings().getIdleTimeout()).isEqualTo(Duration.ofMinutes(5));
        assertThat(builder.readRowsSettings().getRetryableCodes()).containsAllOf(ABORTED, DEADLINE_EXCEEDED);
        assertThat(builder.readRowsSettings().getRetrySettings()).isEqualTo(retrySettings);
        assertThat(builder.build().readRowsSettings().getIdleTimeout()).isEqualTo(Duration.ofMinutes(5));
        assertThat(builder.build().readRowsSettings().getRetryableCodes()).containsAllOf(ABORTED, DEADLINE_EXCEEDED);
        assertThat(builder.build().readRowsSettings().getRetrySettings()).isEqualTo(retrySettings);
        assertThat(builder.build().toBuilder().readRowsSettings().getIdleTimeout()).isEqualTo(Duration.ofMinutes(5));
        assertThat(builder.build().toBuilder().readRowsSettings().getRetryableCodes()).containsAllOf(ABORTED, DEADLINE_EXCEEDED);
        assertThat(builder.build().toBuilder().readRowsSettings().getRetrySettings()).isEqualTo(retrySettings);
    }

    @Test
    public void readRowsHasSaneDefaultsTest() {
        ServerStreamingCallSettings.Builder<Query, Row> builder = EnhancedBigtableStubSettings.newBuilder().readRowsSettings();
        verifyRetrySettingAreSane(builder.getRetryableCodes(), builder.getRetrySettings());
    }

    @Test
    public void readRowIsNotLostTest() {
        EnhancedBigtableStubSettings.Builder builder = EnhancedBigtableStubSettings.newBuilder().setProjectId("my-project").setInstanceId("my-instance");
        RetrySettings retrySettings = RetrySettings.newBuilder().setMaxAttempts(10).setTotalTimeout(Duration.ofHours(1)).setInitialRpcTimeout(Duration.ofSeconds(10)).setRpcTimeoutMultiplier(1).setMaxRpcTimeout(Duration.ofSeconds(10)).setJittered(true).build();
        builder.readRowSettings().setRetryableCodes(ABORTED, DEADLINE_EXCEEDED).setRetrySettings(retrySettings).build();
        // Streaming readRows settings must match point lookup settings.
        builder.readRowsSettings().setRetryableCodes(ABORTED, DEADLINE_EXCEEDED);
        assertThat(builder.readRowSettings().getRetryableCodes()).containsAllOf(ABORTED, DEADLINE_EXCEEDED);
        assertThat(builder.readRowSettings().getRetrySettings()).isEqualTo(retrySettings);
        assertThat(builder.build().readRowSettings().getRetryableCodes()).containsAllOf(ABORTED, DEADLINE_EXCEEDED);
        assertThat(builder.build().readRowSettings().getRetrySettings()).isEqualTo(retrySettings);
        assertThat(builder.build().toBuilder().readRowSettings().getRetryableCodes()).containsAllOf(ABORTED, DEADLINE_EXCEEDED);
        assertThat(builder.build().toBuilder().readRowSettings().getRetrySettings()).isEqualTo(retrySettings);
    }

    @Test
    public void readRowHasSaneDefaultsTest() {
        UnaryCallSettings.Builder<Query, Row> builder = EnhancedBigtableStubSettings.newBuilder().readRowSettings();
        verifyRetrySettingAreSane(builder.getRetryableCodes(), builder.getRetrySettings());
    }

    @Test
    public void readRowRetryCodesMustMatch() {
        EnhancedBigtableStubSettings.Builder builder = EnhancedBigtableStubSettings.newBuilder().setProjectId("my-project").setInstanceId("my-instance");
        builder.readRowsSettings().setRetryableCodes(DEADLINE_EXCEEDED);
        builder.readRowSettings().setRetryableCodes(ABORTED);
        Exception actualError = null;
        try {
            builder.build();
        } catch (Exception e) {
            actualError = e;
        }
        assertThat(actualError).isNotNull();
        builder.readRowSettings().setRetryableCodes(DEADLINE_EXCEEDED);
        actualError = null;
        try {
            builder.build();
        } catch (Exception e) {
            actualError = e;
        }
        assertThat(actualError).isNull();
    }

    @Test
    public void sampleRowKeysSettingsAreNotLostTest() {
        String dummyProjectId = "my-project";
        String dummyInstanceId = "my-instance";
        EnhancedBigtableStubSettings.Builder builder = EnhancedBigtableStubSettings.newBuilder().setProjectId(dummyProjectId).setInstanceId(dummyInstanceId);
        RetrySettings retrySettings = RetrySettings.newBuilder().setMaxAttempts(10).setTotalTimeout(Duration.ofHours(1)).setInitialRpcTimeout(Duration.ofSeconds(10)).setRpcTimeoutMultiplier(1).setMaxRpcTimeout(Duration.ofSeconds(10)).setJittered(true).build();
        builder.sampleRowKeysSettings().setRetryableCodes(ABORTED, DEADLINE_EXCEEDED).setRetrySettings(retrySettings).build();
        assertThat(builder.sampleRowKeysSettings().getRetryableCodes()).containsAllOf(ABORTED, DEADLINE_EXCEEDED);
        assertThat(builder.sampleRowKeysSettings().getRetrySettings()).isEqualTo(retrySettings);
        assertThat(builder.build().sampleRowKeysSettings().getRetryableCodes()).containsAllOf(ABORTED, DEADLINE_EXCEEDED);
        assertThat(builder.build().sampleRowKeysSettings().getRetrySettings()).isEqualTo(retrySettings);
        assertThat(builder.build().toBuilder().sampleRowKeysSettings().getRetryableCodes()).containsAllOf(ABORTED, DEADLINE_EXCEEDED);
        assertThat(builder.build().toBuilder().sampleRowKeysSettings().getRetrySettings()).isEqualTo(retrySettings);
    }

    @Test
    public void sampleRowKeysHasSaneDefaultsTest() {
        UnaryCallSettings.Builder<String, List<KeyOffset>> builder = EnhancedBigtableStubSettings.newBuilder().sampleRowKeysSettings();
        verifyRetrySettingAreSane(builder.getRetryableCodes(), builder.getRetrySettings());
    }

    @Test
    public void mutateRowSettingsAreNotLostTest() {
        String dummyProjectId = "my-project";
        String dummyInstanceId = "my-instance";
        EnhancedBigtableStubSettings.Builder builder = EnhancedBigtableStubSettings.newBuilder().setProjectId(dummyProjectId).setInstanceId(dummyInstanceId);
        RetrySettings retrySettings = RetrySettings.newBuilder().setMaxAttempts(10).setTotalTimeout(Duration.ofHours(1)).setInitialRpcTimeout(Duration.ofSeconds(10)).setRpcTimeoutMultiplier(1).setMaxRpcTimeout(Duration.ofSeconds(10)).setJittered(true).build();
        builder.mutateRowSettings().setRetryableCodes(ABORTED, DEADLINE_EXCEEDED).setRetrySettings(retrySettings).build();
        assertThat(builder.mutateRowSettings().getRetryableCodes()).containsAllOf(ABORTED, DEADLINE_EXCEEDED);
        assertThat(builder.mutateRowSettings().getRetrySettings()).isEqualTo(retrySettings);
        assertThat(builder.build().mutateRowSettings().getRetryableCodes()).containsAllOf(ABORTED, DEADLINE_EXCEEDED);
        assertThat(builder.build().mutateRowSettings().getRetrySettings()).isEqualTo(retrySettings);
        assertThat(builder.build().toBuilder().mutateRowSettings().getRetryableCodes()).containsAllOf(ABORTED, DEADLINE_EXCEEDED);
        assertThat(builder.build().toBuilder().mutateRowSettings().getRetrySettings()).isEqualTo(retrySettings);
    }

    @Test
    public void mutateRowHasSaneDefaultsTest() {
        UnaryCallSettings.Builder<RowMutation, Void> builder = EnhancedBigtableStubSettings.newBuilder().mutateRowSettings();
        verifyRetrySettingAreSane(builder.getRetryableCodes(), builder.getRetrySettings());
    }

    @Test
    public void bulkMutateRowsSettingsAreNotLostTest() {
        String dummyProjectId = "my-project";
        String dummyInstanceId = "my-instance";
        EnhancedBigtableStubSettings.Builder builder = EnhancedBigtableStubSettings.newBuilder().setProjectId(dummyProjectId).setInstanceId(dummyInstanceId);
        RetrySettings retrySettings = RetrySettings.newBuilder().setMaxAttempts(10).setTotalTimeout(Duration.ofHours(1)).setInitialRpcTimeout(Duration.ofSeconds(10)).setRpcTimeoutMultiplier(1).setMaxRpcTimeout(Duration.ofSeconds(10)).setJittered(true).build();
        BatchingSettings batchingSettings = BatchingSettings.newBuilder().build();
        builder.bulkMutateRowsSettings().setRetryableCodes(ABORTED, DEADLINE_EXCEEDED).setRetrySettings(retrySettings).setBatchingSettings(batchingSettings).build();
        assertThat(builder.bulkMutateRowsSettings().getRetryableCodes()).containsAllOf(ABORTED, DEADLINE_EXCEEDED);
        assertThat(builder.bulkMutateRowsSettings().getRetrySettings()).isEqualTo(retrySettings);
        assertThat(builder.bulkMutateRowsSettings().getBatchingSettings()).isSameAs(batchingSettings);
        assertThat(builder.build().bulkMutateRowsSettings().getRetryableCodes()).containsAllOf(ABORTED, DEADLINE_EXCEEDED);
        assertThat(builder.build().bulkMutateRowsSettings().getRetrySettings()).isEqualTo(retrySettings);
        assertThat(builder.build().bulkMutateRowsSettings().getBatchingSettings()).isSameAs(batchingSettings);
        assertThat(builder.build().toBuilder().bulkMutateRowsSettings().getRetryableCodes()).containsAllOf(ABORTED, DEADLINE_EXCEEDED);
        assertThat(builder.build().toBuilder().bulkMutateRowsSettings().getRetrySettings()).isEqualTo(retrySettings);
        assertThat(builder.build().toBuilder().bulkMutateRowsSettings().getBatchingSettings()).isSameAs(batchingSettings);
    }

    @Test
    public void mutateRowsHasSaneDefaultsTest() {
        BatchingCallSettings.Builder<RowMutation, Void> builder = EnhancedBigtableStubSettings.newBuilder().bulkMutateRowsSettings();
        verifyRetrySettingAreSane(builder.getRetryableCodes(), builder.getRetrySettings());
        assertThat(builder.getBatchingSettings().getDelayThreshold()).isIn(Range.open(ZERO, Duration.ofMinutes(1)));
        assertThat(builder.getBatchingSettings().getElementCountThreshold()).isIn(Range.open(0L, 1000L));
        assertThat(builder.getBatchingSettings().getIsEnabled()).isTrue();
        assertThat(builder.getBatchingSettings().getRequestByteThreshold()).isLessThan(((256L * 1024) * 1024));
        assertThat(builder.getBatchingSettings().getFlowControlSettings().getMaxOutstandingElementCount()).isLessThan(10000L);
        assertThat(builder.getBatchingSettings().getFlowControlSettings().getMaxOutstandingRequestBytes()).isLessThan(((512L * 1024) * 1024));
    }

    @Test
    public void checkAndMutateRowSettingsAreNotLostTest() {
        String dummyProjectId = "my-project";
        String dummyInstanceId = "my-instance";
        EnhancedBigtableStubSettings.Builder builder = EnhancedBigtableStubSettings.newBuilder().setProjectId(dummyProjectId).setInstanceId(dummyInstanceId);
        RetrySettings retrySettings = RetrySettings.newBuilder().build();
        builder.checkAndMutateRowSettings().setRetryableCodes(ABORTED, DEADLINE_EXCEEDED).setRetrySettings(retrySettings).build();
        assertThat(builder.checkAndMutateRowSettings().getRetryableCodes()).containsAllOf(ABORTED, DEADLINE_EXCEEDED);
        assertThat(builder.checkAndMutateRowSettings().getRetrySettings()).isSameAs(retrySettings);
        assertThat(builder.build().checkAndMutateRowSettings().getRetryableCodes()).containsAllOf(ABORTED, DEADLINE_EXCEEDED);
        assertThat(builder.build().checkAndMutateRowSettings().getRetrySettings()).isSameAs(retrySettings);
        assertThat(builder.build().toBuilder().checkAndMutateRowSettings().getRetryableCodes()).containsAllOf(ABORTED, DEADLINE_EXCEEDED);
        assertThat(builder.build().toBuilder().checkAndMutateRowSettings().getRetrySettings()).isSameAs(retrySettings);
    }

    @Test
    public void checkAndMutateRowSettingsAreSane() {
        UnaryCallSettings.Builder<ConditionalRowMutation, Boolean> builder = EnhancedBigtableStubSettings.newBuilder().checkAndMutateRowSettings();
        // CheckAndMutateRow is not retryable in the case of toggle mutations. So it's disabled by
        // default.
        assertThat(builder.getRetrySettings().getMaxAttempts()).isAtMost(1);
        assertThat(builder.getRetryableCodes()).isEmpty();
    }
}

