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


import org.apache.log4j.Level;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;


public class LogTest {
    @Test
    public void testLogName() throws Exception {
        Log log = new Log();
        log.setLogName("log-name");
        MatcherAssert.assertThat(log.getLogName(), Matchers.equalTo("log-name"));
    }

    @Test
    public void testLogLevel() throws Exception {
        Log log = new Log();
        log.setLogLevel(Level.ALL);
        MatcherAssert.assertThat(log.getLogLevel(), Matchers.is(Level.ALL));
    }

    @Test
    public void testLogMessage() throws Exception {
        Log log = new Log();
        log.setLogMessage("log-message");
        MatcherAssert.assertThat(log.getLogMessage(), Matchers.equalTo("log-message"));
    }

    @Test
    public void testLogThread() throws Exception {
        Log log = new Log();
        log.setLogThread("log-thread");
        MatcherAssert.assertThat(log.getLogThread(), Matchers.equalTo("log-thread"));
    }
}

