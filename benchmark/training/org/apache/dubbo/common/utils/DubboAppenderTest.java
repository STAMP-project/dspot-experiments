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
import org.apache.log4j.spi.LoggingEvent;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;


public class DubboAppenderTest {
    private LoggingEvent event;

    @Test
    public void testAvailable() throws Exception {
        MatcherAssert.assertThat(available, Matchers.is(false));
        DubboAppender.doStart();
        MatcherAssert.assertThat(available, Matchers.is(true));
        DubboAppender.doStop();
        MatcherAssert.assertThat(available, Matchers.is(false));
    }

    @Test
    public void testAppend() throws Exception {
        DubboAppender appender = new DubboAppender();
        appender.append(event);
        MatcherAssert.assertThat(logList, Matchers.hasSize(0));
        DubboAppender.doStart();
        appender.append(event);
        MatcherAssert.assertThat(logList, Matchers.hasSize(1));
        Log log = logList.get(0);
        MatcherAssert.assertThat(log.getLogThread(), Matchers.equalTo("thread-name"));
    }

    @Test
    public void testClear() throws Exception {
        DubboAppender.doStart();
        DubboAppender appender = new DubboAppender();
        appender.append(event);
        MatcherAssert.assertThat(logList, Matchers.hasSize(1));
        DubboAppender.clear();
        MatcherAssert.assertThat(logList, Matchers.hasSize(0));
    }
}

