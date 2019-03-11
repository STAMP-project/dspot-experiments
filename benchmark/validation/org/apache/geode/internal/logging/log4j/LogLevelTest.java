/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.geode.internal.logging.log4j;


import Level.ALL;
import Level.DEBUG;
import Level.ERROR;
import Level.FATAL;
import Level.INFO;
import Level.OFF;
import Level.TRACE;
import Level.WARN;
import LogWriterLevel.CONFIG;
import LogWriterLevel.FINE;
import LogWriterLevel.FINER;
import LogWriterLevel.FINEST;
import LogWriterLevel.NONE;
import LogWriterLevel.SEVERE;
import LogWriterLevel.WARNING;
import org.apache.geode.test.junit.categories.LoggingTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;


/**
 * Unit tests for {@link LogLevel}.
 */
@Category(LoggingTest.class)
public class LogLevelTest {
    @Test
    public void getLevel_levelName_returnsLevel() {
        assertThat(LogLevel.getLevel(OFF.name())).isEqualTo(OFF);
        assertThat(LogLevel.getLevel(FATAL.name())).isEqualTo(FATAL);
        assertThat(LogLevel.getLevel(ERROR.name())).isEqualTo(ERROR);
        assertThat(LogLevel.getLevel(WARN.name())).isEqualTo(WARN);
        assertThat(LogLevel.getLevel(INFO.name())).isEqualTo(INFO);
        assertThat(LogLevel.getLevel(DEBUG.name())).isEqualTo(DEBUG);
        assertThat(LogLevel.getLevel(TRACE.name())).isEqualTo(TRACE);
        assertThat(LogLevel.getLevel(ALL.name())).isEqualTo(ALL);
    }

    @Test
    public void getLevel_levelName_toLowerCase_returnsLevel() {
        assertThat(LogLevel.getLevel(OFF.name().toLowerCase())).isEqualTo(OFF);
        assertThat(LogLevel.getLevel(FATAL.name().toLowerCase())).isEqualTo(FATAL);
        assertThat(LogLevel.getLevel(ERROR.name().toLowerCase())).isEqualTo(ERROR);
        assertThat(LogLevel.getLevel(WARN.name().toLowerCase())).isEqualTo(WARN);
        assertThat(LogLevel.getLevel(INFO.name().toLowerCase())).isEqualTo(INFO);
        assertThat(LogLevel.getLevel(DEBUG.name().toLowerCase())).isEqualTo(DEBUG);
        assertThat(LogLevel.getLevel(TRACE.name().toLowerCase())).isEqualTo(TRACE);
        assertThat(LogLevel.getLevel(ALL.name().toLowerCase())).isEqualTo(ALL);
    }

    @Test
    public void getLevel_levelName_toUpperCase_returnsLevel() {
        assertThat(LogLevel.getLevel(OFF.name().toUpperCase())).isEqualTo(OFF);
        assertThat(LogLevel.getLevel(FATAL.name().toUpperCase())).isEqualTo(FATAL);
        assertThat(LogLevel.getLevel(ERROR.name().toUpperCase())).isEqualTo(ERROR);
        assertThat(LogLevel.getLevel(WARN.name().toUpperCase())).isEqualTo(WARN);
        assertThat(LogLevel.getLevel(INFO.name().toUpperCase())).isEqualTo(INFO);
        assertThat(LogLevel.getLevel(DEBUG.name().toUpperCase())).isEqualTo(DEBUG);
        assertThat(LogLevel.getLevel(TRACE.name().toUpperCase())).isEqualTo(TRACE);
        assertThat(LogLevel.getLevel(ALL.name().toUpperCase())).isEqualTo(ALL);
    }

    @Test
    public void getLevel_logWriterLevelName_returnsLevel() {
        assertThat(LogLevel.getLevel(NONE.name())).isEqualTo(OFF);
        assertThat(LogLevel.getLevel(SEVERE.name())).isEqualTo(FATAL);
        assertThat(LogLevel.getLevel(LogWriterLevel.ERROR.name())).isEqualTo(ERROR);
        assertThat(LogLevel.getLevel(WARNING.name())).isEqualTo(WARN);
        assertThat(LogLevel.getLevel(LogWriterLevel.INFO.name())).isEqualTo(INFO);
        assertThat(LogLevel.getLevel(CONFIG.name())).isEqualTo(INFO);
        assertThat(LogLevel.getLevel(FINE.name())).isEqualTo(DEBUG);
        assertThat(LogLevel.getLevel(FINER.name())).isEqualTo(TRACE);
        assertThat(LogLevel.getLevel(FINEST.name())).isEqualTo(TRACE);
        assertThat(LogLevel.getLevel(LogWriterLevel.ALL.name())).isEqualTo(ALL);
    }

    @Test
    public void getLevel_logWriterLevelName_toLowerCase_returnsLevel() {
        assertThat(LogLevel.getLevel(NONE.name().toLowerCase())).isEqualTo(OFF);
        assertThat(LogLevel.getLevel(SEVERE.name().toLowerCase())).isEqualTo(FATAL);
        assertThat(LogLevel.getLevel(LogWriterLevel.ERROR.name().toLowerCase())).isEqualTo(ERROR);
        assertThat(LogLevel.getLevel(WARNING.name().toLowerCase())).isEqualTo(WARN);
        assertThat(LogLevel.getLevel(LogWriterLevel.INFO.name().toLowerCase())).isEqualTo(INFO);
        assertThat(LogLevel.getLevel(CONFIG.name().toLowerCase())).isEqualTo(INFO);
        assertThat(LogLevel.getLevel(FINE.name().toLowerCase())).isEqualTo(DEBUG);
        assertThat(LogLevel.getLevel(FINER.name().toLowerCase())).isEqualTo(TRACE);
        assertThat(LogLevel.getLevel(FINEST.name().toLowerCase())).isEqualTo(TRACE);
        assertThat(LogLevel.getLevel(LogWriterLevel.ALL.name().toLowerCase())).isEqualTo(ALL);
    }

    @Test
    public void getLevel_logWriterLevelName_toUpperCase_returnsLevel() {
        assertThat(LogLevel.getLevel(NONE.name().toUpperCase())).isEqualTo(OFF);
        assertThat(LogLevel.getLevel(SEVERE.name().toUpperCase())).isEqualTo(FATAL);
        assertThat(LogLevel.getLevel(LogWriterLevel.ERROR.name().toUpperCase())).isEqualTo(ERROR);
        assertThat(LogLevel.getLevel(WARNING.name().toUpperCase())).isEqualTo(WARN);
        assertThat(LogLevel.getLevel(LogWriterLevel.INFO.name().toUpperCase())).isEqualTo(INFO);
        assertThat(LogLevel.getLevel(CONFIG.name().toUpperCase())).isEqualTo(INFO);
        assertThat(LogLevel.getLevel(FINE.name().toUpperCase())).isEqualTo(DEBUG);
        assertThat(LogLevel.getLevel(FINER.name().toUpperCase())).isEqualTo(TRACE);
        assertThat(LogLevel.getLevel(FINEST.name().toUpperCase())).isEqualTo(TRACE);
        assertThat(LogLevel.getLevel(LogWriterLevel.ALL.name().toUpperCase())).isEqualTo(ALL);
    }

    @Test
    public void getLevel_nonLevel_returnsNull() {
        assertThat(LogLevel.getLevel("notrecognizable")).isNull();
    }

    @Test
    public void resolveLevel_levelName_returnsLevel() {
        assertThat(LogLevel.resolveLevel(OFF.name())).isEqualTo(OFF);
        assertThat(LogLevel.resolveLevel(FATAL.name())).isEqualTo(FATAL);
        assertThat(LogLevel.resolveLevel(ERROR.name())).isEqualTo(ERROR);
        assertThat(LogLevel.resolveLevel(WARN.name())).isEqualTo(WARN);
        assertThat(LogLevel.resolveLevel(INFO.name())).isEqualTo(INFO);
        assertThat(LogLevel.resolveLevel(DEBUG.name())).isEqualTo(DEBUG);
        assertThat(LogLevel.resolveLevel(TRACE.name())).isEqualTo(TRACE);
        assertThat(LogLevel.resolveLevel(ALL.name())).isEqualTo(ALL);
    }

    @Test
    public void resolveLevel_levelName_toLowerCase_returnsLevel() {
        assertThat(LogLevel.resolveLevel(OFF.name().toLowerCase())).isEqualTo(OFF);
        assertThat(LogLevel.resolveLevel(FATAL.name().toLowerCase())).isEqualTo(FATAL);
        assertThat(LogLevel.resolveLevel(ERROR.name().toLowerCase())).isEqualTo(ERROR);
        assertThat(LogLevel.resolveLevel(WARN.name().toLowerCase())).isEqualTo(WARN);
        assertThat(LogLevel.resolveLevel(INFO.name().toLowerCase())).isEqualTo(INFO);
        assertThat(LogLevel.resolveLevel(DEBUG.name().toLowerCase())).isEqualTo(DEBUG);
        assertThat(LogLevel.resolveLevel(TRACE.name().toLowerCase())).isEqualTo(TRACE);
        assertThat(LogLevel.resolveLevel(ALL.name().toLowerCase())).isEqualTo(ALL);
    }

    @Test
    public void resolveLevel_levelName_toUpperCase_returnsLevel() {
        assertThat(LogLevel.resolveLevel(OFF.name().toUpperCase())).isEqualTo(OFF);
        assertThat(LogLevel.resolveLevel(FATAL.name().toUpperCase())).isEqualTo(FATAL);
        assertThat(LogLevel.resolveLevel(ERROR.name().toUpperCase())).isEqualTo(ERROR);
        assertThat(LogLevel.resolveLevel(WARN.name().toUpperCase())).isEqualTo(WARN);
        assertThat(LogLevel.resolveLevel(INFO.name().toUpperCase())).isEqualTo(INFO);
        assertThat(LogLevel.resolveLevel(DEBUG.name().toUpperCase())).isEqualTo(DEBUG);
        assertThat(LogLevel.resolveLevel(TRACE.name().toUpperCase())).isEqualTo(TRACE);
        assertThat(LogLevel.resolveLevel(ALL.name().toUpperCase())).isEqualTo(ALL);
    }

    @Test
    public void resolveLevel_logWriterLevel_returnsLevel() {
        assertThat(LogLevel.resolveLevel(NONE.name())).isEqualTo(OFF);
        assertThat(LogLevel.resolveLevel(SEVERE.name())).isEqualTo(FATAL);
        assertThat(LogLevel.resolveLevel(LogWriterLevel.ERROR.name())).isEqualTo(ERROR);
        assertThat(LogLevel.resolveLevel(WARNING.name())).isEqualTo(WARN);
        assertThat(LogLevel.resolveLevel(LogWriterLevel.INFO.name())).isEqualTo(INFO);
        assertThat(LogLevel.resolveLevel(CONFIG.name())).isEqualTo(INFO);
        assertThat(LogLevel.resolveLevel(FINE.name())).isEqualTo(DEBUG);
        assertThat(LogLevel.resolveLevel(FINER.name())).isEqualTo(TRACE);
        assertThat(LogLevel.resolveLevel(FINEST.name())).isEqualTo(TRACE);
        assertThat(LogLevel.resolveLevel(LogWriterLevel.ALL.name())).isEqualTo(ALL);
    }

    @Test
    public void resolveLevel_logWriterLevel_toLowerCase_returnsLevel() {
        assertThat(LogLevel.resolveLevel(NONE.name().toLowerCase())).isEqualTo(OFF);
        assertThat(LogLevel.resolveLevel(SEVERE.name().toLowerCase())).isEqualTo(FATAL);
        assertThat(LogLevel.resolveLevel(LogWriterLevel.ERROR.name().toLowerCase())).isEqualTo(ERROR);
        assertThat(LogLevel.resolveLevel(WARNING.name().toLowerCase())).isEqualTo(WARN);
        assertThat(LogLevel.resolveLevel(LogWriterLevel.INFO.name().toLowerCase())).isEqualTo(INFO);
        assertThat(LogLevel.resolveLevel(CONFIG.name().toLowerCase())).isEqualTo(INFO);
        assertThat(LogLevel.resolveLevel(FINE.name().toLowerCase())).isEqualTo(DEBUG);
        assertThat(LogLevel.resolveLevel(FINER.name().toLowerCase())).isEqualTo(TRACE);
        assertThat(LogLevel.resolveLevel(FINEST.name().toLowerCase())).isEqualTo(TRACE);
        assertThat(LogLevel.resolveLevel(LogWriterLevel.ALL.name().toLowerCase())).isEqualTo(ALL);
    }

    @Test
    public void resolveLevel_logWriterLevel_toUpperCase_returnsLevel() {
        assertThat(LogLevel.resolveLevel(NONE.name().toUpperCase())).isEqualTo(OFF);
        assertThat(LogLevel.resolveLevel(SEVERE.name().toUpperCase())).isEqualTo(FATAL);
        assertThat(LogLevel.resolveLevel(LogWriterLevel.ERROR.name().toUpperCase())).isEqualTo(ERROR);
        assertThat(LogLevel.resolveLevel(WARNING.name().toUpperCase())).isEqualTo(WARN);
        assertThat(LogLevel.resolveLevel(LogWriterLevel.INFO.name().toUpperCase())).isEqualTo(INFO);
        assertThat(LogLevel.resolveLevel(CONFIG.name().toUpperCase())).isEqualTo(INFO);
        assertThat(LogLevel.resolveLevel(FINE.name().toUpperCase())).isEqualTo(DEBUG);
        assertThat(LogLevel.resolveLevel(FINER.name().toUpperCase())).isEqualTo(TRACE);
        assertThat(LogLevel.resolveLevel(FINEST.name().toUpperCase())).isEqualTo(TRACE);
        assertThat(LogLevel.resolveLevel(LogWriterLevel.ALL.name().toUpperCase())).isEqualTo(ALL);
    }

    @Test
    public void resolveLevel_nonLevel_returnsOff() {
        assertThat(LogLevel.resolveLevel("notrecognizable")).isEqualTo(OFF);
    }

    @Test
    public void getLogWriterLevel_levelName_returnsLogWriterLevelValue() {
        assertThat(LogLevel.getLogWriterLevel(OFF.name())).isEqualTo(NONE.intLevel());
        assertThat(LogLevel.getLogWriterLevel(FATAL.name())).isEqualTo(SEVERE.intLevel());
        assertThat(LogLevel.getLogWriterLevel(ERROR.name())).isEqualTo(LogWriterLevel.ERROR.intLevel());
        assertThat(LogLevel.getLogWriterLevel(WARN.name())).isEqualTo(WARNING.intLevel());
        assertThat(LogLevel.getLogWriterLevel(INFO.name())).isEqualTo(LogWriterLevel.INFO.intLevel());
        assertThat(LogLevel.getLogWriterLevel(DEBUG.name())).isEqualTo(FINE.intLevel());
        assertThat(LogLevel.getLogWriterLevel(TRACE.name())).isEqualTo(FINEST.intLevel());
        assertThat(LogLevel.getLogWriterLevel(ALL.name())).isEqualTo(LogWriterLevel.ALL.intLevel());
    }

    @Test
    public void getLogWriterLevel_levelName_toLowerCase_returnsLogWriterLevelValue() {
        assertThat(LogLevel.getLogWriterLevel(OFF.name().toLowerCase())).isEqualTo(NONE.intLevel());
        assertThat(LogLevel.getLogWriterLevel(FATAL.name().toLowerCase())).isEqualTo(SEVERE.intLevel());
        assertThat(LogLevel.getLogWriterLevel(ERROR.name().toLowerCase())).isEqualTo(LogWriterLevel.ERROR.intLevel());
        assertThat(LogLevel.getLogWriterLevel(WARN.name().toLowerCase())).isEqualTo(WARNING.intLevel());
        assertThat(LogLevel.getLogWriterLevel(INFO.name().toLowerCase())).isEqualTo(LogWriterLevel.INFO.intLevel());
        assertThat(LogLevel.getLogWriterLevel(DEBUG.name().toLowerCase())).isEqualTo(FINE.intLevel());
        assertThat(LogLevel.getLogWriterLevel(TRACE.name().toLowerCase())).isEqualTo(FINEST.intLevel());
        assertThat(LogLevel.getLogWriterLevel(ALL.name().toLowerCase())).isEqualTo(LogWriterLevel.ALL.intLevel());
    }

    @Test
    public void getLogWriterLevel_levelName_toUpperCase_returnsLogWriterLevelValue() {
        assertThat(LogLevel.getLogWriterLevel(OFF.name().toUpperCase())).isEqualTo(NONE.intLevel());
        assertThat(LogLevel.getLogWriterLevel(FATAL.name().toUpperCase())).isEqualTo(SEVERE.intLevel());
        assertThat(LogLevel.getLogWriterLevel(ERROR.name().toUpperCase())).isEqualTo(LogWriterLevel.ERROR.intLevel());
        assertThat(LogLevel.getLogWriterLevel(WARN.name().toUpperCase())).isEqualTo(WARNING.intLevel());
        assertThat(LogLevel.getLogWriterLevel(INFO.name().toUpperCase())).isEqualTo(LogWriterLevel.INFO.intLevel());
        assertThat(LogLevel.getLogWriterLevel(DEBUG.name().toUpperCase())).isEqualTo(FINE.intLevel());
        assertThat(LogLevel.getLogWriterLevel(TRACE.name().toUpperCase())).isEqualTo(FINEST.intLevel());
        assertThat(LogLevel.getLogWriterLevel(ALL.name().toUpperCase())).isEqualTo(LogWriterLevel.ALL.intLevel());
    }

    @Test
    public void getLogWriterLevel_logWriterLevelName_returnsLogWriterLevelValue() {
        assertThat(LogLevel.getLogWriterLevel(NONE.name())).isEqualTo(NONE.intLevel());
        assertThat(LogLevel.getLogWriterLevel(SEVERE.name())).isEqualTo(SEVERE.intLevel());
        assertThat(LogLevel.getLogWriterLevel(LogWriterLevel.ERROR.name())).isEqualTo(LogWriterLevel.ERROR.intLevel());
        assertThat(LogLevel.getLogWriterLevel(WARNING.name())).isEqualTo(WARNING.intLevel());
        assertThat(LogLevel.getLogWriterLevel(CONFIG.name())).isEqualTo(CONFIG.intLevel());
        assertThat(LogLevel.getLogWriterLevel(LogWriterLevel.INFO.name())).isEqualTo(LogWriterLevel.INFO.intLevel());
        assertThat(LogLevel.getLogWriterLevel(FINE.name())).isEqualTo(FINE.intLevel());
        assertThat(LogLevel.getLogWriterLevel(FINER.name())).isEqualTo(FINER.intLevel());
        assertThat(LogLevel.getLogWriterLevel(FINEST.name())).isEqualTo(FINEST.intLevel());
        assertThat(LogLevel.getLogWriterLevel(LogWriterLevel.ALL.name())).isEqualTo(LogWriterLevel.ALL.intLevel());
    }

    @Test
    public void getLogWriterLevel_logWriterLevelName_toLowerCase_returnsLogWriterLevelValue() {
        assertThat(LogLevel.getLogWriterLevel(NONE.name().toLowerCase())).isEqualTo(NONE.intLevel());
        assertThat(LogLevel.getLogWriterLevel(SEVERE.name().toLowerCase())).isEqualTo(SEVERE.intLevel());
        assertThat(LogLevel.getLogWriterLevel(LogWriterLevel.ERROR.name().toLowerCase())).isEqualTo(LogWriterLevel.ERROR.intLevel());
        assertThat(LogLevel.getLogWriterLevel(WARNING.name().toLowerCase())).isEqualTo(WARNING.intLevel());
        assertThat(LogLevel.getLogWriterLevel(LogWriterLevel.INFO.name().toLowerCase())).isEqualTo(LogWriterLevel.INFO.intLevel());
        assertThat(LogLevel.getLogWriterLevel(CONFIG.name().toLowerCase())).isEqualTo(CONFIG.intLevel());
        assertThat(LogLevel.getLogWriterLevel(FINE.name().toLowerCase())).isEqualTo(FINE.intLevel());
        assertThat(LogLevel.getLogWriterLevel(FINER.name().toLowerCase())).isEqualTo(FINER.intLevel());
        assertThat(LogLevel.getLogWriterLevel(FINEST.name().toLowerCase())).isEqualTo(FINEST.intLevel());
        assertThat(LogLevel.getLogWriterLevel(LogWriterLevel.ALL.name().toLowerCase())).isEqualTo(LogWriterLevel.ALL.intLevel());
    }

    @Test
    public void getLogWriterLevel_logWriterLevelName_toUpperCase_returnsLogWriterLevelValue() {
        assertThat(LogLevel.getLogWriterLevel(NONE.name().toUpperCase())).isEqualTo(NONE.intLevel());
        assertThat(LogLevel.getLogWriterLevel(SEVERE.name().toUpperCase())).isEqualTo(SEVERE.intLevel());
        assertThat(LogLevel.getLogWriterLevel(LogWriterLevel.ERROR.name().toUpperCase())).isEqualTo(LogWriterLevel.ERROR.intLevel());
        assertThat(LogLevel.getLogWriterLevel(WARNING.name().toUpperCase())).isEqualTo(WARNING.intLevel());
        assertThat(LogLevel.getLogWriterLevel(LogWriterLevel.INFO.name().toUpperCase())).isEqualTo(LogWriterLevel.INFO.intLevel());
        assertThat(LogLevel.getLogWriterLevel(CONFIG.name().toUpperCase())).isEqualTo(CONFIG.intLevel());
        assertThat(LogLevel.getLogWriterLevel(FINE.name().toUpperCase())).isEqualTo(FINE.intLevel());
        assertThat(LogLevel.getLogWriterLevel(FINER.name().toUpperCase())).isEqualTo(FINER.intLevel());
        assertThat(LogLevel.getLogWriterLevel(FINEST.name().toUpperCase())).isEqualTo(FINEST.intLevel());
        assertThat(LogLevel.getLogWriterLevel(LogWriterLevel.ALL.name().toUpperCase())).isEqualTo(LogWriterLevel.ALL.intLevel());
    }

    @Test
    public void getLogWriterLevel_levelWithNumber_returnsLogWriterLevelValue() {
        assertThat(LogLevel.getLogWriterLevel(("level-" + (Integer.MIN_VALUE)))).isEqualTo(Integer.MIN_VALUE);
        assertThat(LogLevel.getLogWriterLevel("level-0")).isEqualTo(0);
        assertThat(LogLevel.getLogWriterLevel("level-1")).isEqualTo(1);
        assertThat(LogLevel.getLogWriterLevel("level-1234")).isEqualTo(1234);
        assertThat(LogLevel.getLogWriterLevel("level-2345")).isEqualTo(2345);
        assertThat(LogLevel.getLogWriterLevel(("level-" + (Integer.MAX_VALUE)))).isEqualTo(Integer.MAX_VALUE);
    }

    @Test
    public void getLogWriterLevel_null_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> LogLevel.getLogWriterLevel(((String) (null)))).isInstanceOf(IllegalArgumentException.class).hasMessage("LevelName cannot be null");
    }

    @Test
    public void getLogWriterLevel_test_returns() {
        assertThatThrownBy(() -> LogLevel.getLogWriterLevel("test")).isInstanceOf(IllegalArgumentException.class);
    }
}

