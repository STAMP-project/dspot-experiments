/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.dubbo.common.utils;


import DubboAppender.available;
import DubboAppender.logList;
import org.apache.log4j.Level;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


public class LogUtilTest {
    @Test
    public void testStartStop() throws Exception {
        LogUtil.start();
        MatcherAssert.assertThat(available, Matchers.is(true));
        LogUtil.stop();
        MatcherAssert.assertThat(available, Matchers.is(false));
    }

    @Test
    public void testCheckNoError() throws Exception {
        Log log = Mockito.mock(Log.class);
        logList.add(log);
        Mockito.when(log.getLogLevel()).thenReturn(Level.ERROR);
        MatcherAssert.assertThat(LogUtil.checkNoError(), Matchers.is(false));
        Mockito.when(log.getLogLevel()).thenReturn(Level.INFO);
        MatcherAssert.assertThat(LogUtil.checkNoError(), Matchers.is(true));
    }

    @Test
    public void testFindName() throws Exception {
        Log log = Mockito.mock(Log.class);
        logList.add(log);
        Mockito.when(log.getLogName()).thenReturn("a");
        MatcherAssert.assertThat(LogUtil.findName("a"), CoreMatchers.equalTo(1));
    }

    @Test
    public void testFindLevel() throws Exception {
        Log log = Mockito.mock(Log.class);
        logList.add(log);
        Mockito.when(log.getLogLevel()).thenReturn(Level.ERROR);
        MatcherAssert.assertThat(LogUtil.findLevel(Level.ERROR), CoreMatchers.equalTo(1));
        MatcherAssert.assertThat(LogUtil.findLevel(Level.INFO), CoreMatchers.equalTo(0));
    }

    @Test
    public void testFindLevelWithThreadName() throws Exception {
        Log log = Mockito.mock(Log.class);
        logList.add(log);
        Mockito.when(log.getLogLevel()).thenReturn(Level.ERROR);
        Mockito.when(log.getLogThread()).thenReturn("thread-1");
        log = Mockito.mock(Log.class);
        logList.add(log);
        Mockito.when(log.getLogLevel()).thenReturn(Level.ERROR);
        Mockito.when(log.getLogThread()).thenReturn("thread-2");
        MatcherAssert.assertThat(LogUtil.findLevelWithThreadName(Level.ERROR, "thread-2"), CoreMatchers.equalTo(1));
    }

    @Test
    public void testFindThread() throws Exception {
        Log log = Mockito.mock(Log.class);
        logList.add(log);
        Mockito.when(log.getLogThread()).thenReturn("thread-1");
        MatcherAssert.assertThat(LogUtil.findThread("thread-1"), CoreMatchers.equalTo(1));
    }

    @Test
    public void testFindMessage1() throws Exception {
        Log log = Mockito.mock(Log.class);
        logList.add(log);
        Mockito.when(log.getLogMessage()).thenReturn("message");
        MatcherAssert.assertThat(LogUtil.findMessage("message"), CoreMatchers.equalTo(1));
    }

    @Test
    public void testFindMessage2() throws Exception {
        Log log = Mockito.mock(Log.class);
        logList.add(log);
        Mockito.when(log.getLogMessage()).thenReturn("message");
        Mockito.when(log.getLogLevel()).thenReturn(Level.ERROR);
        log = Mockito.mock(Log.class);
        logList.add(log);
        Mockito.when(log.getLogMessage()).thenReturn("message");
        Mockito.when(log.getLogLevel()).thenReturn(Level.INFO);
        MatcherAssert.assertThat(LogUtil.findMessage(Level.ERROR, "message"), CoreMatchers.equalTo(1));
    }
}

