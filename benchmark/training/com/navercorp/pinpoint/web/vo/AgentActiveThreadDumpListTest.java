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
package com.navercorp.pinpoint.web.vo;


import com.navercorp.pinpoint.common.util.PinpointThreadFactory;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Taejin Koo
 */
public class AgentActiveThreadDumpListTest {
    private static final int CREATE_DUMP_SIZE = 10;

    private final PinpointThreadFactory pinpointThreadFactory = new PinpointThreadFactory(this.getClass().getSimpleName());

    @Test
    public void basicFunctionTest1() throws Exception {
        List<AgentActiveThreadDumpListTest.WaitingJob> waitingJobList = createWaitingJobList(AgentActiveThreadDumpListTest.CREATE_DUMP_SIZE);
        try {
            Thread[] threads = createThread(waitingJobList);
            AgentActiveThreadDumpList activeThreadDumpList = createThreadDumpList(threads);
            Assert.assertEquals(AgentActiveThreadDumpListTest.CREATE_DUMP_SIZE, activeThreadDumpList.getAgentActiveThreadDumpRepository().size());
        } finally {
            clearResource(waitingJobList);
        }
    }

    @Test
    public void basicFunctionTest2() throws Exception {
        List<AgentActiveThreadDumpListTest.WaitingJob> waitingJobList = createWaitingJobList(AgentActiveThreadDumpListTest.CREATE_DUMP_SIZE);
        try {
            Thread[] threads = createThread(waitingJobList);
            AgentActiveThreadDumpList activeThreadDumpList = createThreadLightDumpList(threads);
            List<AgentActiveThreadDump> sortOldestAgentActiveThreadDumpRepository = activeThreadDumpList.getSortOldestAgentActiveThreadDumpRepository();
            long before = 0;
            for (AgentActiveThreadDump dump : sortOldestAgentActiveThreadDumpRepository) {
                long startTime = dump.getStartTime();
                if (before > startTime) {
                    Assert.fail();
                }
                before = startTime;
            }
        } finally {
            clearResource(waitingJobList);
        }
    }

    @Test(expected = UnsupportedOperationException.class)
    public void checkUnmodifiableList() throws Exception {
        List<AgentActiveThreadDumpListTest.WaitingJob> waitingJobList = createWaitingJobList(AgentActiveThreadDumpListTest.CREATE_DUMP_SIZE);
        try {
            Thread[] threads = createThread(waitingJobList);
            AgentActiveThreadDumpList activeThreadDumpList = createThreadDumpList(threads);
            List<AgentActiveThreadDump> agentActiveThreadDumpRepository = activeThreadDumpList.getAgentActiveThreadDumpRepository();
            agentActiveThreadDumpRepository.remove(0);
        } finally {
            clearResource(waitingJobList);
        }
    }

    private static class WaitingJob implements Runnable {
        private final long timeIntervalMillis;

        private boolean close = false;

        public WaitingJob(long timeIntervalMillis) {
            this.timeIntervalMillis = timeIntervalMillis;
        }

        @Override
        public void run() {
            while (!(close)) {
                try {
                    Thread.sleep(timeIntervalMillis);
                } catch (InterruptedException e) {
                    close = true;
                }
            } 
        }

        public void close() {
            this.close = true;
        }
    }
}

