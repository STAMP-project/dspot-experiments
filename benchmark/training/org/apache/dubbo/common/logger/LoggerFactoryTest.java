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
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.dubbo.common.logger;


import Level.INFO;
import java.io.File;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;


public class LoggerFactoryTest {
    @Test
    public void testLoggerLevel() {
        LoggerFactory.setLevel(INFO);
        Level level = LoggerFactory.getLevel();
        MatcherAssert.assertThat(level, CoreMatchers.is(INFO));
    }

    @Test
    public void testGetLogFile() {
        LoggerFactory.setLoggerAdapter("slf4j");
        File file = LoggerFactory.getFile();
        MatcherAssert.assertThat(file, CoreMatchers.is(CoreMatchers.nullValue()));
    }

    @Test
    public void testAllLogLevel() {
        for (Level targetLevel : Level.values()) {
            LoggerFactory.setLevel(targetLevel);
            Level level = LoggerFactory.getLevel();
            MatcherAssert.assertThat(level, CoreMatchers.is(targetLevel));
        }
    }

    @Test
    public void testGetLogger() {
        Logger logger1 = LoggerFactory.getLogger(this.getClass());
        Logger logger2 = LoggerFactory.getLogger(this.getClass());
        MatcherAssert.assertThat(logger1, CoreMatchers.is(logger2));
    }

    @Test
    public void shouldReturnSameLogger() {
        Logger logger1 = LoggerFactory.getLogger(this.getClass().getName());
        Logger logger2 = LoggerFactory.getLogger(this.getClass().getName());
        MatcherAssert.assertThat(logger1, CoreMatchers.is(logger2));
    }
}

