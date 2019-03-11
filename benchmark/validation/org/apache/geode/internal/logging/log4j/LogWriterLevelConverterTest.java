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


import Level.DEBUG;
import Level.FATAL;
import Level.OFF;
import Level.TRACE;
import Level.WARN;
import LogWriterLevel.ALL;
import LogWriterLevel.CONFIG;
import LogWriterLevel.ERROR;
import LogWriterLevel.FINE;
import LogWriterLevel.FINER;
import LogWriterLevel.FINEST;
import LogWriterLevel.INFO;
import LogWriterLevel.NONE;
import LogWriterLevel.SEVERE;
import LogWriterLevel.WARNING;
import org.apache.geode.internal.logging.LogWriterLevel;
import org.apache.geode.test.junit.categories.LoggingTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;


/**
 * Unit tests for {@link LogWriterLevelConverter}.
 */
@Category(LoggingTest.class)
public class LogWriterLevelConverterTest {
    @Test
    public void toLevel_LogWriterLevelAll_returnsLevelAll() {
        assertThat(LogWriterLevelConverter.toLevel(ALL)).isEqualTo(Level.ALL);
    }

    @Test
    public void toLevel_LogWriterLevelSevere_returnsLevelFatal() {
        assertThat(LogWriterLevelConverter.toLevel(SEVERE)).isEqualTo(FATAL);
    }

    @Test
    public void toLevel_LogWriterLevelError_returnsLevelFatal() {
        assertThat(LogWriterLevelConverter.toLevel(ERROR)).isEqualTo(Level.ERROR);
    }

    @Test
    public void toLevel_LogWriterLevelWarning_returnsLevelFatal() {
        assertThat(LogWriterLevelConverter.toLevel(WARNING)).isEqualTo(WARN);
    }

    @Test
    public void toLevel_LogWriterLevelInfo_returnsLevelInfo() {
        assertThat(LogWriterLevelConverter.toLevel(INFO)).isEqualTo(Level.INFO);
    }

    @Test
    public void toLevel_LogWriterLevelConfig_returnsLevelInfo() {
        assertThat(LogWriterLevelConverter.toLevel(CONFIG)).isEqualTo(Level.INFO);
    }

    @Test
    public void toLevel_LogWriterLevelFine_returnsLevelInfo() {
        assertThat(LogWriterLevelConverter.toLevel(FINE)).isEqualTo(DEBUG);
    }

    @Test
    public void toLevel_LogWriterLevelFiner_returnsLevelInfo() {
        assertThat(LogWriterLevelConverter.toLevel(FINER)).isEqualTo(TRACE);
    }

    @Test
    public void toLevel_LogWriterLevelFinest_returnsLevelInfo() {
        assertThat(LogWriterLevelConverter.toLevel(FINEST)).isEqualTo(TRACE);
    }

    @Test
    public void toLevel_LogWriterLevelOff_returnsLevelFatal() {
        assertThat(LogWriterLevelConverter.toLevel(NONE)).isEqualTo(OFF);
    }

    @Test
    public void toLogWriterLevel_LevelAll_returnsLogWriterLevelAll() {
        assertThat(LogWriterLevelConverter.fromLevel(Level.ALL)).isEqualTo(ALL);
    }

    @Test
    public void toLogWriterLevel_LevelFatal_returnsLogWriterLevelSevere() {
        assertThat(LogWriterLevelConverter.fromLevel(FATAL)).isEqualTo(SEVERE);
    }

    @Test
    public void toLogWriterLevel_LevelError_returnsLogWriterLevelError() {
        assertThat(LogWriterLevelConverter.fromLevel(Level.ERROR)).isEqualTo(ERROR);
    }

    @Test
    public void toLogWriterLevel_LevelWarn_returnsLogWriterLevelWarning() {
        assertThat(LogWriterLevelConverter.fromLevel(WARN)).isEqualTo(WARNING);
    }

    @Test
    public void toLogWriterLevel_LevelInfo_returnsLogWriterLevelInfo() {
        assertThat(LogWriterLevelConverter.fromLevel(Level.INFO)).isEqualTo(INFO);
    }

    @Test
    public void toLogWriterLevel_LevelDebug_returnsLogWriterLevelFine() {
        assertThat(LogWriterLevelConverter.fromLevel(DEBUG)).isEqualTo(FINE);
    }

    @Test
    public void toLogWriterLevel_LevelTrace_returnsLogWriterLevelFinest() {
        assertThat(LogWriterLevelConverter.fromLevel(TRACE)).isEqualTo(FINEST);
    }

    @Test
    public void toLogWriterLevel_LevelOff_returnsLogWriterLevelNone() {
        assertThat(LogWriterLevelConverter.fromLevel(OFF)).isEqualTo(NONE);
    }

    @Test
    public void getLog4jLevel_nonLevel_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> toLevel(LogWriterLevel.find(123123123))).isInstanceOf(IllegalArgumentException.class).hasMessageContaining("No LogWriterLevel found for intLevel 123123123");
    }
}

