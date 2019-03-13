/**
 * Copyright 2016 NAVER Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.navercorp.pinpoint.profiler.receiver.service;


import com.navercorp.pinpoint.common.util.PinpointThreadFactory;
import com.navercorp.pinpoint.profiler.context.active.ActiveTraceSnapshot;
import com.navercorp.pinpoint.thrift.dto.command.TActiveThreadLightDump;
import com.navercorp.pinpoint.thrift.dto.command.TCmdActiveThreadLightDump;
import com.navercorp.pinpoint.thrift.dto.command.TCmdActiveThreadLightDumpRes;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 *
 * @author Taejin Koo
 */
public class ActiveThreadLightDumpServiceTest {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private static final int CREATE_SIZE = 10;

    private static final long DEFAULT_TIME_MILLIS = (System.currentTimeMillis()) - 1000000;

    private static final long TIME_DIFF_INTERVAL = 100;

    private static final long JOB_TIMEOUT = 1000 * 10;

    private final AtomicInteger idGenerator = new AtomicInteger();

    private final PinpointThreadFactory pinpointThreadFactory = new PinpointThreadFactory(this.getClass().getSimpleName());

    private final WaitingJobListFactory waitingJobListFactory = new WaitingJobListFactory();

    @Test
    public void basicFunctionTest1() throws Exception {
        List<WaitingJob> waitingJobList = this.waitingJobListFactory.createList(ActiveThreadLightDumpServiceTest.CREATE_SIZE, ActiveThreadLightDumpServiceTest.JOB_TIMEOUT);
        List<ActiveTraceSnapshot> activeTraceInfoList = createMockActiveTraceInfoList(ActiveThreadLightDumpServiceTest.CREATE_SIZE, ActiveThreadLightDumpServiceTest.DEFAULT_TIME_MILLIS, ActiveThreadLightDumpServiceTest.TIME_DIFF_INTERVAL, waitingJobList);
        ActiveThreadLightDumpService service = createService(activeTraceInfoList);
        TCmdActiveThreadLightDumpRes response = ((TCmdActiveThreadLightDumpRes) (service.requestCommandService(createRequest(0, null, null))));
        Assert.assertEquals(ActiveThreadLightDumpServiceTest.CREATE_SIZE, response.getThreadDumpsSize());
    }

    @Test
    public void basicFunctionTest2() throws Exception {
        List<WaitingJob> waitingJobList = this.waitingJobListFactory.createList(ActiveThreadLightDumpServiceTest.CREATE_SIZE, ActiveThreadLightDumpServiceTest.JOB_TIMEOUT);
        List<ActiveTraceSnapshot> activeTraceInfoList = createMockActiveTraceInfoList(ActiveThreadLightDumpServiceTest.CREATE_SIZE, ActiveThreadLightDumpServiceTest.DEFAULT_TIME_MILLIS, ActiveThreadLightDumpServiceTest.TIME_DIFF_INTERVAL, waitingJobList);
        TCmdActiveThreadLightDump tCmdActiveThreadDump = createRequest(0, null, Arrays.asList(1L));
        ActiveThreadLightDumpService service = createService(activeTraceInfoList);
        TCmdActiveThreadLightDumpRes response = ((TCmdActiveThreadLightDumpRes) (service.requestCommandService(tCmdActiveThreadDump)));
        Assert.assertEquals(1, response.getThreadDumpsSize());
    }

    @Test
    public void basicFunctionTest3() throws Exception {
        List<WaitingJob> waitingJobList = this.waitingJobListFactory.createList(ActiveThreadLightDumpServiceTest.CREATE_SIZE, ActiveThreadLightDumpServiceTest.JOB_TIMEOUT);
        int targetThreadNameSize = 3;
        List<ActiveTraceSnapshot> activeTraceInfoList = createMockActiveTraceInfoList(ActiveThreadLightDumpServiceTest.CREATE_SIZE, ActiveThreadLightDumpServiceTest.DEFAULT_TIME_MILLIS, ActiveThreadLightDumpServiceTest.TIME_DIFF_INTERVAL, waitingJobList);
        List<String> threadNameList = extractThreadNameList(activeTraceInfoList, targetThreadNameSize);
        TCmdActiveThreadLightDump tCmdActiveThreadDump = createRequest(0, threadNameList, null);
        ActiveThreadLightDumpService service = createService(activeTraceInfoList);
        TCmdActiveThreadLightDumpRes response = ((TCmdActiveThreadLightDumpRes) (service.requestCommandService(tCmdActiveThreadDump)));
        Assert.assertEquals(3, response.getThreadDumpsSize());
    }

    @Test
    public void basicFunctionTest4() throws Exception {
        List<WaitingJob> waitingJobList = this.waitingJobListFactory.createList(ActiveThreadLightDumpServiceTest.CREATE_SIZE, ActiveThreadLightDumpServiceTest.JOB_TIMEOUT);
        List<ActiveTraceSnapshot> activeTraceInfoList = createMockActiveTraceInfoList(ActiveThreadLightDumpServiceTest.CREATE_SIZE, ActiveThreadLightDumpServiceTest.DEFAULT_TIME_MILLIS, ActiveThreadLightDumpServiceTest.TIME_DIFF_INTERVAL, waitingJobList);
        List<ActiveTraceSnapshot> activeTraceSnapshotList = shuffle(activeTraceInfoList);
        int targetThreadNameSize = 3;
        List<String> threadNameList = extractThreadNameList(activeTraceSnapshotList.subList(0, targetThreadNameSize), targetThreadNameSize);
        int targetTraceIdSize = 3;
        List<Long> localTraceIdList = extractLocalTraceIdList(activeTraceSnapshotList.subList(targetThreadNameSize, ActiveThreadLightDumpServiceTest.CREATE_SIZE), targetTraceIdSize);
        TCmdActiveThreadLightDump tCmdActiveThreadDump = createRequest(0, threadNameList, localTraceIdList);
        ActiveThreadLightDumpService service = createService(activeTraceInfoList);
        TCmdActiveThreadLightDumpRes response = ((TCmdActiveThreadLightDumpRes) (service.requestCommandService(tCmdActiveThreadDump)));
        Assert.assertEquals((targetThreadNameSize + targetTraceIdSize), response.getThreadDumpsSize());
    }

    @Test
    public void basicFunctionTest5() throws Exception {
        List<WaitingJob> waitingJobList = this.waitingJobListFactory.createList(ActiveThreadLightDumpServiceTest.CREATE_SIZE, ActiveThreadLightDumpServiceTest.JOB_TIMEOUT);
        List<ActiveTraceSnapshot> activeTraceInfoList = createMockActiveTraceInfoList(ActiveThreadLightDumpServiceTest.CREATE_SIZE, ActiveThreadLightDumpServiceTest.DEFAULT_TIME_MILLIS, ActiveThreadLightDumpServiceTest.TIME_DIFF_INTERVAL, waitingJobList);
        int limit = 3;
        List<Long> oldTimeList = getOldTimeList(limit);
        TCmdActiveThreadLightDump tCmdActiveThreadDump = createRequest(limit, null, null);
        ActiveThreadLightDumpService service = createService(activeTraceInfoList);
        TCmdActiveThreadLightDumpRes response = ((TCmdActiveThreadLightDumpRes) (service.requestCommandService(tCmdActiveThreadDump)));
        Assert.assertEquals(limit, response.getThreadDumpsSize());
        for (TActiveThreadLightDump dump : response.getThreadDumps()) {
            Assert.assertTrue(oldTimeList.contains(dump.getStartTime()));
        }
    }
}

