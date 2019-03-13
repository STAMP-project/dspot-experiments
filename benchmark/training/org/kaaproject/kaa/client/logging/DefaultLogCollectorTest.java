/**
 * Copyright 2014-2016 CyberVision, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kaaproject.kaa.client.logging;


import LogDeliveryErrorCode.NO_APPENDERS_CONFIGURED;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import org.junit.Assert;
import org.junit.Test;
import org.kaaproject.kaa.client.channel.KaaChannelManager;
import org.kaaproject.kaa.client.channel.LogTransport;
import org.kaaproject.kaa.client.channel.failover.FailoverManager;
import org.kaaproject.kaa.client.context.ExecutorContext;
import org.kaaproject.kaa.client.logging.future.RecordFuture;
import org.kaaproject.kaa.common.endpoint.gen.LogDeliveryErrorCode;
import org.kaaproject.kaa.common.endpoint.gen.LogDeliveryStatus;
import org.kaaproject.kaa.common.endpoint.gen.LogSyncRequest;
import org.kaaproject.kaa.common.endpoint.gen.LogSyncResponse;
import org.kaaproject.kaa.common.endpoint.gen.SyncResponseResultType;
import org.kaaproject.kaa.schema.base.Log;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

import static LogUploadStrategyDecision.UPLOAD;


public class DefaultLogCollectorTest {
    private static ExecutorContext executorContext;

    private static ScheduledExecutorService executor;

    @Test
    public void testDefaultUploadConfiguration() {
        KaaChannelManager channelManager = Mockito.mock(KaaChannelManager.class);
        FailoverManager failoverManager = Mockito.mock(FailoverManager.class);
        LogTransport transport = Mockito.mock(LogTransport.class);
        AbstractLogCollector logCollector = new DefaultLogCollector(transport, DefaultLogCollectorTest.executorContext, channelManager, failoverManager);
        DefaultLogUploadStrategy strategy = new DefaultLogUploadStrategy();
        strategy.setCountThreshold(5);
        logCollector.setStrategy(strategy);
        Log record = new Log();
        logCollector.addLogRecord(record);
        logCollector.addLogRecord(record);
        logCollector.addLogRecord(record);
        logCollector.addLogRecord(record);
        Mockito.verify(transport, Mockito.times(0)).sync();
        logCollector.addLogRecord(record);
        Mockito.verify(transport, Mockito.timeout(1000).times(1)).sync();
    }

    @Test
    public void testStorageStatusAffect() {
        KaaChannelManager channelManager = Mockito.mock(KaaChannelManager.class);
        FailoverManager failoverManager = Mockito.mock(FailoverManager.class);
        LogTransport transport = Mockito.mock(LogTransport.class);
        AbstractLogCollector logCollector = new DefaultLogCollector(transport, DefaultLogCollectorTest.executorContext, channelManager, failoverManager);
        LogStorage storage = Mockito.mock(LogStorage.class);
        logCollector.setStorage(storage);
        Log record = new Log();
        Mockito.when(storage.addLogRecord(Mockito.any(LogRecord.class))).thenReturn(new BucketInfo(1, 1));
        Mockito.when(storage.getStatus()).thenReturn(new LogStorageStatus() {
            @Override
            public long getRecordCount() {
                return 1;
            }

            @Override
            public long getConsumedVolume() {
                return 1;
            }
        });
        logCollector.addLogRecord(record);
        Mockito.verify(transport, Mockito.times(0)).sync();
        Mockito.when(storage.getStatus()).thenReturn(new LogStorageStatus() {
            @Override
            public long getRecordCount() {
                return 1;
            }

            @Override
            public long getConsumedVolume() {
                return 1024 * 1024;
            }
        });
        logCollector.addLogRecord(record);
        Mockito.verify(transport, Mockito.timeout(1000).times(1)).sync();
    }

    @Test
    public void testLogUploadRequestAndSuccessResponse() throws Exception {
        KaaChannelManager channelManager = Mockito.mock(KaaChannelManager.class);
        FailoverManager failoverManager = Mockito.mock(FailoverManager.class);
        LogDeliveryListener deliveryListener = Mockito.mock(LogDeliveryListener.class);
        LogTransport transport = Mockito.mock(LogTransport.class);
        AbstractLogCollector logCollector = new DefaultLogCollector(transport, DefaultLogCollectorTest.executorContext, channelManager, failoverManager);
        DefaultLogUploadStrategy strategy = Mockito.spy(new DefaultLogUploadStrategy());
        logCollector.setStrategy(strategy);
        LogStorage storage = Mockito.mock(LogStorage.class);
        logCollector.setStorage(storage);
        Log record = new Log();
        Mockito.when(storage.addLogRecord(Mockito.any(LogRecord.class))).thenReturn(new BucketInfo(1, 1));
        Mockito.when(storage.getStatus()).thenReturn(new LogStorageStatus() {
            @Override
            public long getRecordCount() {
                return 1;
            }

            @Override
            public long getConsumedVolume() {
                return 1;
            }
        });
        logCollector.addLogRecord(record);
        logCollector.addLogRecord(record);
        logCollector.addLogRecord(record);
        logCollector.addLogRecord(record);
        logCollector.addLogRecord(record);
        Mockito.when(storage.getStatus()).thenReturn(new LogStorageStatus() {
            @Override
            public long getRecordCount() {
                return 1;
            }

            @Override
            public long getConsumedVolume() {
                return 1024 * 1024;
            }
        });
        logCollector.addLogRecord(record);
        Mockito.when(storage.getNextBucket()).thenReturn(new LogBucket(1, Arrays.asList(new LogRecord(record), new LogRecord(record), new LogRecord(record))));
        LogSyncRequest request1 = new LogSyncRequest();
        logCollector.fillSyncRequest(request1);
        Assert.assertEquals(3, request1.getLogEntries().size());
        LogSyncResponse uploadResponse = new LogSyncResponse();
        LogDeliveryStatus status = new LogDeliveryStatus(request1.getRequestId(), SyncResponseResultType.SUCCESS, null);
        uploadResponse.setDeliveryStatuses(Collections.singletonList(status));
        logCollector.setLogDeliveryListener(deliveryListener);
        logCollector.onLogResponse(uploadResponse);
        Mockito.verify(deliveryListener, Mockito.timeout(1000)).onLogDeliverySuccess(Mockito.any(BucketInfo.class));
        Mockito.verify(transport, Mockito.timeout(1000).times(2)).sync();
    }

    @Test
    public void testLogUploadAndFailureResponse() throws IOException, InterruptedException {
        KaaChannelManager channelManager = Mockito.mock(KaaChannelManager.class);
        FailoverManager failoverManager = Mockito.mock(FailoverManager.class);
        LogDeliveryListener deliveryListener = Mockito.mock(LogDeliveryListener.class);
        LogTransport transport = Mockito.mock(LogTransport.class);
        AbstractLogCollector logCollector = new DefaultLogCollector(transport, DefaultLogCollectorTest.executorContext, channelManager, failoverManager);
        DefaultLogUploadStrategy strategy = Mockito.spy(new DefaultLogUploadStrategy());
        strategy.setRetryPeriod(0);
        logCollector.setStrategy(strategy);
        LogStorage storage = Mockito.mock(LogStorage.class);
        logCollector.setStorage(storage);
        Log record = new Log();
        Mockito.when(storage.addLogRecord(Mockito.any(LogRecord.class))).thenReturn(new BucketInfo(1, 1));
        Mockito.when(storage.getStatus()).thenReturn(new LogStorageStatus() {
            @Override
            public long getRecordCount() {
                return 1;
            }

            @Override
            public long getConsumedVolume() {
                return 1;
            }
        });
        logCollector.addLogRecord(record);
        logCollector.addLogRecord(record);
        logCollector.addLogRecord(record);
        logCollector.addLogRecord(record);
        logCollector.addLogRecord(record);
        Mockito.when(storage.addLogRecord(Mockito.any(LogRecord.class))).thenReturn(new BucketInfo(1, 1));
        Mockito.when(storage.getStatus()).thenReturn(new LogStorageStatus() {
            @Override
            public long getRecordCount() {
                return 1;
            }

            @Override
            public long getConsumedVolume() {
                return 1024 * 1024;
            }
        });
        logCollector.addLogRecord(record);
        Mockito.when(storage.getNextBucket()).thenReturn(new LogBucket(1, Arrays.asList(new LogRecord(record), new LogRecord(record), new LogRecord(record))));
        LogSyncRequest request1 = new LogSyncRequest();
        logCollector.fillSyncRequest(request1);
        Assert.assertEquals(3, request1.getLogEntries().size());
        LogSyncResponse uploadResponse = new LogSyncResponse();
        LogDeliveryStatus status = new LogDeliveryStatus(request1.getRequestId(), SyncResponseResultType.FAILURE, LogDeliveryErrorCode.NO_APPENDERS_CONFIGURED);
        uploadResponse.setDeliveryStatuses(Collections.singletonList(status));
        logCollector.setLogDeliveryListener(deliveryListener);
        logCollector.onLogResponse(uploadResponse);
        LogFailoverCommand controller = ((LogFailoverCommand) (ReflectionTestUtils.getField(logCollector, "controller")));
        Mockito.verify(deliveryListener, Mockito.timeout(1000)).onLogDeliveryFailure(Mockito.any(BucketInfo.class));
        Mockito.verify(strategy, Mockito.timeout(1000)).onFailure(controller, NO_APPENDERS_CONFIGURED);
        Mockito.verify(transport, Mockito.timeout(1000).times(2)).sync();
        Mockito.reset(transport);
        Thread.sleep(1000);
        Mockito.verify(transport, Mockito.never()).sync();
    }

    @Test
    public void testTimeout() throws Exception {
        int timeout = 2;// in seconds

        KaaChannelManager channelManager = Mockito.mock(KaaChannelManager.class);
        FailoverManager failoverManager = Mockito.mock(FailoverManager.class);
        LogTransport transport = Mockito.mock(LogTransport.class);
        LogDeliveryListener deliveryListener = Mockito.mock(LogDeliveryListener.class);
        AbstractLogCollector logCollector = new DefaultLogCollector(transport, DefaultLogCollectorTest.executorContext, channelManager, failoverManager);
        DefaultLogUploadStrategy tmp = new DefaultLogUploadStrategy();
        tmp.setTimeout(timeout);
        LogUploadStrategy strategy = Mockito.spy(tmp);
        logCollector.setLogDeliveryListener(deliveryListener);
        logCollector.setStrategy(strategy);
        Log record = new Log();
        logCollector.addLogRecord(record);
        logCollector.addLogRecord(record);
        logCollector.addLogRecord(record);
        logCollector.addLogRecord(record);
        logCollector.addLogRecord(record);
        logCollector.addLogRecord(record);
        Mockito.verify(strategy, Mockito.times(0)).onTimeout(Mockito.any(LogFailoverCommand.class));
        LogSyncRequest request1 = Mockito.mock(LogSyncRequest.class);
        logCollector.fillSyncRequest(request1);
        Thread.sleep(((timeout / 2) * 1000));
        Mockito.verify(strategy, Mockito.times(0)).onTimeout(Mockito.any(LogFailoverCommand.class));
        Thread.sleep(((timeout / 2) * 1000));
        logCollector.addLogRecord(record);
        Mockito.verify(deliveryListener, Mockito.timeout(1000)).onLogDeliveryTimeout(Mockito.any(BucketInfo.class));
        Mockito.verify(strategy, Mockito.timeout(1000).times(1)).onTimeout(Mockito.any(LogFailoverCommand.class));
    }

    @Test
    public void testBucketFuture() throws Exception {
        int defaultId = 42;
        int logCount = 5;
        KaaChannelManager channelManager = Mockito.mock(KaaChannelManager.class);
        FailoverManager failoverManager = Mockito.mock(FailoverManager.class);
        LogTransport transport = Mockito.mock(LogTransport.class);
        LogStorage storage = Mockito.mock(LogStorage.class);
        AbstractLogCollector logCollector = new DefaultLogCollector(transport, DefaultLogCollectorTest.executorContext, channelManager, failoverManager);
        logCollector.setStorage(storage);
        Mockito.when(storage.getStatus()).thenReturn(new LogStorageStatus() {
            @Override
            public long getRecordCount() {
                return 1;
            }

            @Override
            public long getConsumedVolume() {
                return 1;
            }
        });
        DefaultLogUploadStrategy strategy = new DefaultLogUploadStrategy() {
            @Override
            public LogUploadStrategyDecision isUploadNeeded(LogStorageStatus status) {
                return UPLOAD;
            }
        };
        logCollector.setStrategy(strategy);
        LogSyncResponse response = new LogSyncResponse();
        List<LogDeliveryStatus> statuses = new ArrayList<>();
        LogDeliveryStatus status = new LogDeliveryStatus(defaultId, SyncResponseResultType.SUCCESS, null);
        statuses.add(status);
        response.setDeliveryStatuses(statuses);
        BucketInfo bucketInfo = new BucketInfo(status.getRequestId(), logCount);
        Mockito.when(storage.addLogRecord(Mockito.any(LogRecord.class))).thenReturn(bucketInfo);
        List<LogRecord> logRecords = new ArrayList<>();
        logRecords.add(new LogRecord());
        LogBucket logBlock = new LogBucket(defaultId, logRecords);
        Mockito.when(storage.getNextBucket()).thenReturn(logBlock);
        List<RecordFuture> deliveryFutures = new LinkedList<RecordFuture>();
        for (int i = 0; i < logCount; ++i) {
            deliveryFutures.add(logCollector.addLogRecord(new Log()));
        }
        LogSyncRequest request = new LogSyncRequest();
        logCollector.fillSyncRequest(request);
        logCollector.onLogResponse(response);
        for (RecordFuture future : deliveryFutures) {
            Assert.assertEquals(defaultId, future.get().getBucketInfo().getBucketId());
        }
    }

    @Test
    public void testMaxParallelUpload() throws Exception {
        testMaxParallelUploadHelper(0);
        testMaxParallelUploadHelper(3);
        testMaxParallelUploadHelper(5);
    }

    @Test
    public void testMaxParallelUploadWithSyncAll() throws Exception {
        testMaxParallelUploadSyncHelper(0);
        testMaxParallelUploadSyncHelper(3);
        testMaxParallelUploadSyncHelper(5);
    }
}

