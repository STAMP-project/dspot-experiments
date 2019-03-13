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


import AlertLevel.ERROR;
import AlertLevel.NONE;
import AlertLevel.SEVERE;
import AlertLevel.WARNING;
import Level.ALL;
import Level.DEBUG;
import Level.FATAL;
import Level.INFO;
import Level.OFF;
import Level.TRACE;
import Level.WARN;
import org.apache.geode.test.junit.categories.AlertingTest;
import org.apache.geode.test.junit.categories.LoggingTest;
import org.apache.logging.log4j.Level;
import org.junit.Test;
import org.junit.experimental.categories.Category;


/**
 * Unit tests for {@link AlertLevelConverter}.
 */
@Category({ AlertingTest.class, LoggingTest.class })
public class AlertLevelConverterTest {
    @Test
    public void toLevel_AlertSevere_returnsLevelFatal() {
        assertThat(AlertLevelConverter.toLevel(SEVERE)).isEqualTo(FATAL);
    }

    @Test
    public void toLevel_AlertError_returnsLevelFatal() {
        assertThat(AlertLevelConverter.toLevel(ERROR)).isEqualTo(Level.ERROR);
    }

    @Test
    public void toLevel_AlertWarning_returnsLevelFatal() {
        assertThat(AlertLevelConverter.toLevel(WARNING)).isEqualTo(WARN);
    }

    @Test
    public void toLevel_AlertOff_returnsLevelFatal() {
        assertThat(AlertLevelConverter.toLevel(NONE)).isEqualTo(OFF);
    }

    @Test
    public void toAlertLevel_LevelFatal_returnsAlertSevere() {
        assertThat(AlertLevelConverter.fromLevel(FATAL)).isEqualTo(SEVERE);
    }

    @Test
    public void toAlertLevel_LevelError_returnsAlertError() {
        assertThat(AlertLevelConverter.fromLevel(Level.ERROR)).isEqualTo(ERROR);
    }

    @Test
    public void toAlertLevel_LevelWarn_returnsAlertWarning() {
        assertThat(AlertLevelConverter.fromLevel(WARN)).isEqualTo(WARNING);
    }

    @Test
    public void toAlertLevel_LevelOff_returnsAlertNone() {
        assertThat(AlertLevelConverter.fromLevel(OFF)).isEqualTo(NONE);
    }

    @Test
    public void toAlertLevel_LevelAll_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> fromLevel(Level.ALL)).isInstanceOf(IllegalArgumentException.class).hasMessage((("No matching AlertLevel for Log4J2 Level " + (Level.ALL)) + "."));
    }

    @Test
    public void toAlertLevel_LevelTrace_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> fromLevel(Level.TRACE)).isInstanceOf(IllegalArgumentException.class).hasMessage((("No matching AlertLevel for Log4J2 Level " + (Level.TRACE)) + "."));
    }

    @Test
    public void toAlertLevel_LevelDebug_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> fromLevel(Level.DEBUG)).isInstanceOf(IllegalArgumentException.class).hasMessage((("No matching AlertLevel for Log4J2 Level " + (Level.DEBUG)) + "."));
    }

    @Test
    public void toAlertLevel_LevelInfo_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> fromLevel(Level.INFO)).isInstanceOf(IllegalArgumentException.class).hasMessage((("No matching AlertLevel for Log4J2 Level " + (Level.INFO)) + "."));
    }

    @Test
    public void hasAlertLevel_LevelFatal_returnsTrue() {
        assertThat(AlertLevelConverter.hasAlertLevel(FATAL)).isTrue();
    }

    @Test
    public void hasAlertLevel_LevelError_returnsTrue() {
        assertThat(AlertLevelConverter.hasAlertLevel(Level.ERROR)).isTrue();
    }

    @Test
    public void hasAlertLevel_LevelWarn_returnsTrue() {
        assertThat(AlertLevelConverter.hasAlertLevel(WARN)).isTrue();
    }

    @Test
    public void hasAlertLevel_LevelOff_returnsTrue() {
        assertThat(AlertLevelConverter.hasAlertLevel(OFF)).isTrue();
    }

    @Test
    public void hasAlertLevel_LevelAll_returnsFalse() {
        assertThat(AlertLevelConverter.hasAlertLevel(ALL)).isFalse();
    }

    @Test
    public void hasAlertLevel_LevelTrace_returnsFalse() {
        assertThat(AlertLevelConverter.hasAlertLevel(TRACE)).isFalse();
    }

    @Test
    public void hasAlertLevel_LevelDebug_returnsFalse() {
        assertThat(AlertLevelConverter.hasAlertLevel(DEBUG)).isFalse();
    }

    @Test
    public void hasAlertLevel_LevelInfo_returnsFalse() {
        assertThat(AlertLevelConverter.hasAlertLevel(INFO)).isFalse();
    }
}

