/**
 * Copyright 2017 NAVER Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.navercorp.pinpoint.profiler.util;


import TThreadState.BLOCKED;
import TThreadState.NEW;
import TThreadState.RUNNABLE;
import TThreadState.TERMINATED;
import TThreadState.TIMED_WAITING;
import TThreadState.WAITING;
import com.navercorp.pinpoint.thrift.dto.command.TThreadState;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.lang.Thread.State.BLOCKED;
import static java.lang.Thread.State.NEW;
import static java.lang.Thread.State.RUNNABLE;
import static java.lang.Thread.State.TERMINATED;
import static java.lang.Thread.State.TIMED_WAITING;
import static java.lang.Thread.State.WAITING;


/**
 *
 *
 * @author Woonduk Kang(emeroad)
 */
public class ThreadDumpUtilsTest {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Test
    public void toTThreadState() throws Exception {
        TThreadState newState = ThreadDumpUtils.toTThreadState(NEW);
        Assert.assertEquals(newState, NEW);
        TThreadState runnable = ThreadDumpUtils.toTThreadState(RUNNABLE);
        Assert.assertEquals(runnable, RUNNABLE);
        TThreadState blocked = ThreadDumpUtils.toTThreadState(BLOCKED);
        Assert.assertEquals(blocked, BLOCKED);
        TThreadState waiting = ThreadDumpUtils.toTThreadState(WAITING);
        Assert.assertEquals(waiting, WAITING);
        TThreadState timedWaiting = ThreadDumpUtils.toTThreadState(TIMED_WAITING);
        Assert.assertEquals(timedWaiting, TIMED_WAITING);
        TThreadState terminated = ThreadDumpUtils.toTThreadState(TERMINATED);
        Assert.assertEquals(terminated, TERMINATED);
    }
}

