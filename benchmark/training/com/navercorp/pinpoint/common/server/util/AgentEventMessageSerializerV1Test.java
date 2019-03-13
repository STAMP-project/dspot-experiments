/**
 * Copyright 2018 NAVER Corp.
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
package com.navercorp.pinpoint.common.server.util;


import AgentEventType.AGENT_DEADLOCK_DETECTED;
import ThreadState.RUNNABLE;
import com.navercorp.pinpoint.common.server.bo.event.DeadlockBo;
import com.navercorp.pinpoint.common.server.bo.event.MonitorInfoBo;
import com.navercorp.pinpoint.common.server.bo.event.ThreadDumpBo;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author jaehong.kim
 */
public class AgentEventMessageSerializerV1Test {
    @Test
    public void serialize() throws Exception {
        AgentEventMessageSerializerV1 serializer = new AgentEventMessageSerializerV1();
        // Mock
        final DeadlockBo deadlockBo = new DeadlockBo();
        deadlockBo.setDeadlockedThreadCount(1);
        List<ThreadDumpBo> threadDumpBoList = new ArrayList<>();
        ThreadDumpBo threadDumpBo = new ThreadDumpBo();
        threadDumpBo.setThreadName("threadName");
        threadDumpBo.setThreadId(0);
        threadDumpBo.setBlockedTime(1);
        threadDumpBo.setBlockedCount(2);
        threadDumpBo.setWaitedTime(3);
        threadDumpBo.setWaitedCount(4);
        threadDumpBo.setLockName("lockName");
        threadDumpBo.setLockOwnerId(5);
        threadDumpBo.setLockOwnerName("lockOwnerName");
        threadDumpBo.setInNative(Boolean.TRUE);
        threadDumpBo.setSuspended(Boolean.FALSE);
        threadDumpBo.setThreadState(RUNNABLE);
        threadDumpBo.setStackTraceList(Arrays.asList("foo", "bar"));
        List<MonitorInfoBo> monitorInfoBoList = new ArrayList<>();
        MonitorInfoBo monitorInfoBo = new MonitorInfoBo();
        monitorInfoBo.setStackDepth(9);
        monitorInfoBo.setStackFrame("Frame");
        monitorInfoBoList.add(monitorInfoBo);
        threadDumpBo.setLockedMonitorInfoList(monitorInfoBoList);
        threadDumpBo.setLockedSynchronizerList(Arrays.asList("foo", "bar"));
        threadDumpBoList.add(threadDumpBo);
        deadlockBo.setThreadDumpBoList(threadDumpBoList);
        byte[] bytes = serializer.serialize(AGENT_DEADLOCK_DETECTED, deadlockBo);
        // deserialize
        AgentEventMessageDeserializerV1 deserializer = new AgentEventMessageDeserializerV1();
        Object object = deserializer.deserialize(AGENT_DEADLOCK_DETECTED, bytes);
        if (false == (object instanceof DeadlockBo)) {
            Assert.fail("Failed to deserialize, expected object is DeadlockBo");
        }
        DeadlockBo result = ((DeadlockBo) (object));
        Assert.assertEquals(1, result.getDeadlockedThreadCount());
        Assert.assertEquals(1, result.getThreadDumpBoList().size());
        assertThreadDumpBo(threadDumpBo, result.getThreadDumpBoList().get(0));
    }
}

