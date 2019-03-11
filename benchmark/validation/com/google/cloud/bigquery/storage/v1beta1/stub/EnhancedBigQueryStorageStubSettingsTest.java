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
package com.google.cloud.bigquery.storage.v1beta1.stub;


import Code.DEADLINE_EXCEEDED;
import Code.UNAVAILABLE;
import Duration.ZERO;
import EnhancedBigQueryStorageStubSettings.Builder;
import com.google.api.gax.core.CredentialsProvider;
import com.google.api.gax.retrying.RetrySettings;
import com.google.api.gax.rpc.WatchdogProvider;
import com.google.cloud.bigquery.storage.v1beta1.Storage.BatchCreateReadSessionStreamsRequest;
import com.google.cloud.bigquery.storage.v1beta1.Storage.BatchCreateReadSessionStreamsResponse;
import com.google.cloud.bigquery.storage.v1beta1.Storage.CreateReadSessionRequest;
import com.google.cloud.bigquery.storage.v1beta1.Storage.FinalizeStreamRequest;
import com.google.cloud.bigquery.storage.v1beta1.Storage.ReadRowsRequest;
import com.google.cloud.bigquery.storage.v1beta1.Storage.ReadRowsResponse;
import com.google.cloud.bigquery.storage.v1beta1.Storage.ReadSession;
import com.google.cloud.bigquery.storage.v1beta1.Storage.SplitReadStreamRequest;
import com.google.cloud.bigquery.storage.v1beta1.Storage.SplitReadStreamResponse;
import com.google.protobuf.Empty;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;
import org.threeten.bp.Duration;


@RunWith(JUnit4.class)
public class EnhancedBigQueryStorageStubSettingsTest {
    private static final int MAX_INBOUND_MESSAGE_SIZE = (1024 * 1024) * 11;

    @Test
    public void testSettingsArePreserved() {
        String endpoint = "some.other.host:123";
        CredentialsProvider credentialsProvider = Mockito.mock(CredentialsProvider.class);
        Duration watchdogInterval = Duration.ofSeconds(12);
        WatchdogProvider watchdogProvider = Mockito.mock(WatchdogProvider.class);
        EnhancedBigQueryStorageStubSettings.Builder builder = EnhancedBigQueryStorageStubSettings.newBuilder().setEndpoint(endpoint).setCredentialsProvider(credentialsProvider).setStreamWatchdogCheckInterval(watchdogInterval).setStreamWatchdogProvider(watchdogProvider);
        verifyBuilder(builder, endpoint, credentialsProvider, watchdogInterval, watchdogProvider);
        verifySettings(builder.build(), endpoint, credentialsProvider, watchdogInterval, watchdogProvider);
        verifyBuilder(builder.build().toBuilder(), endpoint, credentialsProvider, watchdogInterval, watchdogProvider);
    }

    @Test
    public void testCreateReadSessionSettings() {
        UnaryCallSettings.Builder<CreateReadSessionRequest, ReadSession> builder = EnhancedBigQueryStorageStubSettings.newBuilder().createReadSessionSettings();
        verifyRetrySettings(builder.getRetryableCodes(), builder.getRetrySettings());
    }

    @Test
    public void testReadRowsSettings() {
        ServerStreamingCallSettings.Builder<ReadRowsRequest, ReadRowsResponse> builder = EnhancedBigQueryStorageStubSettings.newBuilder().readRowsSettings();
        assertThat(builder.getRetryableCodes()).containsAllOf(DEADLINE_EXCEEDED, UNAVAILABLE);
        RetrySettings retrySettings = builder.getRetrySettings();
        assertThat(retrySettings.getInitialRetryDelay()).isEqualTo(Duration.ofMillis(100L));
        assertThat(retrySettings.getRetryDelayMultiplier()).isWithin(1.0E-6).of(1.3);
        assertThat(retrySettings.getMaxRetryDelay()).isEqualTo(Duration.ofMinutes(1L));
        assertThat(retrySettings.getInitialRpcTimeout()).isEqualTo(Duration.ofDays(1L));
        assertThat(retrySettings.getRpcTimeoutMultiplier()).isWithin(1.0E-6).of(1.0);
        assertThat(retrySettings.getMaxRpcTimeout()).isEqualTo(Duration.ofDays(1L));
        assertThat(retrySettings.getTotalTimeout()).isEqualTo(Duration.ofDays(1L));
        assertThat(builder.getIdleTimeout()).isEqualTo(ZERO);
    }

    @Test
    public void testBatchCreateReadSessionStreamsSettings() {
        UnaryCallSettings.Builder<BatchCreateReadSessionStreamsRequest, BatchCreateReadSessionStreamsResponse> builder = EnhancedBigQueryStorageStubSettings.newBuilder().batchCreateReadSessionStreamsSettings();
        verifyRetrySettings(builder.getRetryableCodes(), builder.getRetrySettings());
    }

    @Test
    public void testFinalizeStreamSettings() {
        UnaryCallSettings.Builder<FinalizeStreamRequest, Empty> builder = EnhancedBigQueryStorageStubSettings.newBuilder().finalizeStreamSettings();
        verifyRetrySettings(builder.getRetryableCodes(), builder.getRetrySettings());
    }

    @Test
    public void testSplitReadStreamSettings() {
        UnaryCallSettings.Builder<SplitReadStreamRequest, SplitReadStreamResponse> builder = EnhancedBigQueryStorageStubSettings.newBuilder().splitReadStreamSettings();
        verifyRetrySettings(builder.getRetryableCodes(), builder.getRetrySettings());
    }
}

