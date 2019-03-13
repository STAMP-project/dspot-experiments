/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.rocketmq.store;


import DefaultMessageStore.CleanCommitLogService;
import DefaultMessageStore.CleanConsumeQueueService;
import java.lang.reflect.Field;
import java.net.SocketAddress;
import java.util.Calendar;
import java.util.Map;
import org.apache.rocketmq.store.config.MessageStoreConfig;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test case for DefaultMessageStore.CleanCommitLogService and DefaultMessageStore.CleanConsumeQueueService
 */
public class DefaultMessageStoreCleanFilesTest {
    private DefaultMessageStore messageStore;

    private CleanCommitLogService cleanCommitLogService;

    private CleanConsumeQueueService cleanConsumeQueueService;

    private SocketAddress bornHost;

    private SocketAddress storeHost;

    private String topic = "test";

    private int queueId = 0;

    private int fileCountCommitLog = 55;

    // exactly one message per CommitLog file.
    private int msgCount = fileCountCommitLog;

    private int mappedFileSize = 128;

    private int fileReservedTime = 1;

    @Test
    public void testDeleteExpiredFilesByTimeUp() throws Exception {
        String deleteWhen = (Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) + "";
        // the max value of diskMaxUsedSpaceRatio
        int diskMaxUsedSpaceRatio = 99;
        // used to ensure that automatic file deletion is not triggered
        double diskSpaceCleanForciblyRatio = 0.999;
        initMessageStore(deleteWhen, diskMaxUsedSpaceRatio, diskSpaceCleanForciblyRatio);
        // build and put 55 messages, exactly one message per CommitLog file.
        buildAndPutMessagesToMessageStore(msgCount);
        // undo comment out the code below, if want to debug this case rather than just run it.
        // Thread.sleep(1000 * 60 + 100);
        MappedFileQueue commitLogQueue = getMappedFileQueueCommitLog();
        Assert.assertEquals(fileCountCommitLog, commitLogQueue.getMappedFiles().size());
        int fileCountConsumeQueue = getFileCountConsumeQueue();
        MappedFileQueue consumeQueue = getMappedFileQueueConsumeQueue();
        Assert.assertEquals(fileCountConsumeQueue, consumeQueue.getMappedFiles().size());
        int expireFileCount = 15;
        expireFiles(commitLogQueue, expireFileCount);
        // magic code 10 reference to MappedFileQueue#DELETE_FILES_BATCH_MAX
        for (int a = 1, fileCount = expireFileCount; a <= ((int) (Math.ceil((((double) (expireFileCount)) / 10)))); a++ , fileCount -= 10) {
            cleanCommitLogService.run();
            cleanConsumeQueueService.run();
            int expectDeletedCount = (fileCount >= 10) ? a * 10 : ((a - 1) * 10) + fileCount;
            Assert.assertEquals(((fileCountCommitLog) - expectDeletedCount), commitLogQueue.getMappedFiles().size());
            int msgCountPerFile = getMsgCountPerConsumeQueueMappedFile();
            int expectDeleteCountConsumeQueue = ((int) (Math.floor((((double) (expectDeletedCount)) / msgCountPerFile))));
            Assert.assertEquals((fileCountConsumeQueue - expectDeleteCountConsumeQueue), consumeQueue.getMappedFiles().size());
        }
    }

    @Test
    public void testDeleteExpiredFilesBySpaceFull() throws Exception {
        String deleteWhen = "04";
        // the min value of diskMaxUsedSpaceRatio.
        int diskMaxUsedSpaceRatio = 1;
        // used to ensure that automatic file deletion is not triggered
        double diskSpaceCleanForciblyRatio = 0.999;
        initMessageStore(deleteWhen, diskMaxUsedSpaceRatio, diskSpaceCleanForciblyRatio);
        // build and put 55 messages, exactly one message per CommitLog file.
        buildAndPutMessagesToMessageStore(msgCount);
        // undo comment out the code below, if want to debug this case rather than just run it.
        // Thread.sleep(1000 * 60 + 100);
        MappedFileQueue commitLogQueue = getMappedFileQueueCommitLog();
        Assert.assertEquals(fileCountCommitLog, commitLogQueue.getMappedFiles().size());
        int fileCountConsumeQueue = getFileCountConsumeQueue();
        MappedFileQueue consumeQueue = getMappedFileQueueConsumeQueue();
        Assert.assertEquals(fileCountConsumeQueue, consumeQueue.getMappedFiles().size());
        int expireFileCount = 15;
        expireFiles(commitLogQueue, expireFileCount);
        // magic code 10 reference to MappedFileQueue#DELETE_FILES_BATCH_MAX
        for (int a = 1, fileCount = expireFileCount; a <= ((int) (Math.ceil((((double) (expireFileCount)) / 10)))); a++ , fileCount -= 10) {
            cleanCommitLogService.run();
            cleanConsumeQueueService.run();
            int expectDeletedCount = (fileCount >= 10) ? a * 10 : ((a - 1) * 10) + fileCount;
            Assert.assertEquals(((fileCountCommitLog) - expectDeletedCount), commitLogQueue.getMappedFiles().size());
            int msgCountPerFile = getMsgCountPerConsumeQueueMappedFile();
            int expectDeleteCountConsumeQueue = ((int) (Math.floor((((double) (expectDeletedCount)) / msgCountPerFile))));
            Assert.assertEquals((fileCountConsumeQueue - expectDeleteCountConsumeQueue), consumeQueue.getMappedFiles().size());
        }
    }

    @Test
    public void testDeleteFilesImmediatelyBySpaceFull() throws Exception {
        String deleteWhen = "04";
        // the min value of diskMaxUsedSpaceRatio.
        int diskMaxUsedSpaceRatio = 1;
        // make sure to trigger the automatic file deletion feature
        double diskSpaceCleanForciblyRatio = 0.01;
        initMessageStore(deleteWhen, diskMaxUsedSpaceRatio, diskSpaceCleanForciblyRatio);
        // build and put 55 messages, exactly one message per CommitLog file.
        buildAndPutMessagesToMessageStore(msgCount);
        // undo comment out the code below, if want to debug this case rather than just run it.
        // Thread.sleep(1000 * 60 + 100);
        MappedFileQueue commitLogQueue = getMappedFileQueueCommitLog();
        Assert.assertEquals(fileCountCommitLog, commitLogQueue.getMappedFiles().size());
        int fileCountConsumeQueue = getFileCountConsumeQueue();
        MappedFileQueue consumeQueue = getMappedFileQueueConsumeQueue();
        Assert.assertEquals(fileCountConsumeQueue, consumeQueue.getMappedFiles().size());
        // In this case, there is no need to expire the files.
        // int expireFileCount = 15;
        // expireFiles(commitLogQueue, expireFileCount);
        // magic code 10 reference to MappedFileQueue#DELETE_FILES_BATCH_MAX
        for (int a = 1, fileCount = fileCountCommitLog; (a <= ((int) (Math.ceil((((double) (fileCountCommitLog)) / 10))))) && (fileCount >= 10); a++ , fileCount -= 10) {
            cleanCommitLogService.run();
            cleanConsumeQueueService.run();
            Assert.assertEquals(((fileCountCommitLog) - (10 * a)), commitLogQueue.getMappedFiles().size());
            int msgCountPerFile = getMsgCountPerConsumeQueueMappedFile();
            int expectDeleteCountConsumeQueue = ((int) (Math.floor((((double) (a * 10)) / msgCountPerFile))));
            Assert.assertEquals((fileCountConsumeQueue - expectDeleteCountConsumeQueue), consumeQueue.getMappedFiles().size());
        }
    }

    @Test
    public void testDeleteExpiredFilesManually() throws Exception {
        String deleteWhen = "04";
        // the max value of diskMaxUsedSpaceRatio
        int diskMaxUsedSpaceRatio = 99;
        // used to ensure that automatic file deletion is not triggered
        double diskSpaceCleanForciblyRatio = 0.999;
        initMessageStore(deleteWhen, diskMaxUsedSpaceRatio, diskSpaceCleanForciblyRatio);
        messageStore.executeDeleteFilesManually();
        // build and put 55 messages, exactly one message per CommitLog file.
        buildAndPutMessagesToMessageStore(msgCount);
        // undo comment out the code below, if want to debug this case rather than just run it.
        // Thread.sleep(1000 * 60 + 100);
        MappedFileQueue commitLogQueue = getMappedFileQueueCommitLog();
        Assert.assertEquals(fileCountCommitLog, commitLogQueue.getMappedFiles().size());
        int fileCountConsumeQueue = getFileCountConsumeQueue();
        MappedFileQueue consumeQueue = getMappedFileQueueConsumeQueue();
        Assert.assertEquals(fileCountConsumeQueue, consumeQueue.getMappedFiles().size());
        int expireFileCount = 15;
        expireFiles(commitLogQueue, expireFileCount);
        // magic code 10 reference to MappedFileQueue#DELETE_FILES_BATCH_MAX
        for (int a = 1, fileCount = expireFileCount; a <= ((int) (Math.ceil((((double) (expireFileCount)) / 10)))); a++ , fileCount -= 10) {
            cleanCommitLogService.run();
            cleanConsumeQueueService.run();
            int expectDeletedCount = (fileCount >= 10) ? a * 10 : ((a - 1) * 10) + fileCount;
            Assert.assertEquals(((fileCountCommitLog) - expectDeletedCount), commitLogQueue.getMappedFiles().size());
            int msgCountPerFile = getMsgCountPerConsumeQueueMappedFile();
            int expectDeleteCountConsumeQueue = ((int) (Math.floor((((double) (expectDeletedCount)) / msgCountPerFile))));
            Assert.assertEquals((fileCountConsumeQueue - expectDeleteCountConsumeQueue), consumeQueue.getMappedFiles().size());
        }
    }

    private class MyMessageArrivingListener implements MessageArrivingListener {
        @Override
        public void arriving(String topic, int queueId, long logicOffset, long tagsCode, long msgStoreTime, byte[] filterBitMap, Map<String, String> properties) {
        }
    }

    private class MessageStoreConfigForTest extends MessageStoreConfig {
        @Override
        public int getDiskMaxUsedSpaceRatio() {
            try {
                Field diskMaxUsedSpaceRatioField = this.getClass().getSuperclass().getDeclaredField("diskMaxUsedSpaceRatio");
                diskMaxUsedSpaceRatioField.setAccessible(true);
                int ratio = ((int) (diskMaxUsedSpaceRatioField.get(this)));
                diskMaxUsedSpaceRatioField.setAccessible(false);
                return ratio;
            } catch (Exception ignored) {
            }
            return super.getDiskMaxUsedSpaceRatio();
        }
    }
}

