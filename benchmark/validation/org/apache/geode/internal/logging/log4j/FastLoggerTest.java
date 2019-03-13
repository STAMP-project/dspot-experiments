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
import Level.INFO;
import Level.TRACE;
import org.apache.geode.test.junit.categories.LoggingTest;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.spi.ExtendedLogger;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 * Unit tests for {@link FastLogger} which wraps and delegates to an actual Logger with
 * optimizations for isDebugEnabled and isTraceEnabled.
 */
@Category(LoggingTest.class)
public class FastLoggerTest {
    private static final String LOGGER_NAME = "LOGGER";

    private static final String MARKER_NAME = "MARKER";

    private FastLogger fastLogger;

    private ExtendedLogger mockedLogger;

    private Marker mockedMarker;

    /**
     * FastLogger should return isDelegating after setDelegating
     */
    @Test
    public void isDelegatingIsTrueAfterSetDelegating() {
        assertThat(fastLogger.isDelegating()).isTrue();
        FastLogger.setDelegating(false);
        assertThat(fastLogger.isDelegating()).isFalse();
    }

    /**
     * FastLogger should delegate getLevel
     */
    @Test
    public void delegateGetLevel() {
        Mockito.when(mockedLogger.getLevel()).thenReturn(DEBUG);
        assertThat(fastLogger.getLevel()).isEqualTo(DEBUG);
    }

    /**
     * FastLogger should delegate isDebugEnabled when isDelegating
     */
    @Test
    public void delegateIsDebugEnabledWhenIsDelegating() {
        Mockito.when(mockedLogger.getLevel()).thenReturn(DEBUG);
        Mockito.when(mockedLogger.isEnabled(ArgumentMatchers.eq(DEBUG), ArgumentMatchers.isNull(), ArgumentMatchers.isNull())).thenReturn(true);
        Mockito.when(mockedLogger.isEnabled(ArgumentMatchers.eq(DEBUG), ArgumentMatchers.eq(mockedMarker), ((Object) (ArgumentMatchers.isNull())), ArgumentMatchers.isNull())).thenReturn(true);
        assertThat(fastLogger.isDebugEnabled()).isTrue();
        assertThat(fastLogger.isDebugEnabled(mockedMarker)).isTrue();
        Mockito.verify(mockedLogger).isEnabled(ArgumentMatchers.eq(DEBUG), ArgumentMatchers.isNull(), ArgumentMatchers.isNull());
        Mockito.verify(mockedLogger).isEnabled(ArgumentMatchers.eq(DEBUG), ArgumentMatchers.eq(mockedMarker), ((Object) (ArgumentMatchers.isNull())), ArgumentMatchers.isNull());
    }

    /**
     * FastLogger should delegate isTraceEnabled when isDelegating
     */
    @Test
    public void delegateIsTraceEnabledWhenIsDelegating() {
        Mockito.when(mockedLogger.getLevel()).thenReturn(TRACE);
        Mockito.when(mockedLogger.isEnabled(ArgumentMatchers.eq(TRACE), ArgumentMatchers.isNull(), ((Object) (ArgumentMatchers.isNull())), ArgumentMatchers.isNull())).thenReturn(true);
        Mockito.when(mockedLogger.isEnabled(ArgumentMatchers.eq(TRACE), ArgumentMatchers.eq(mockedMarker), ((Object) (ArgumentMatchers.isNull())), ArgumentMatchers.isNull())).thenReturn(true);
        assertThat(fastLogger.isTraceEnabled()).isTrue();
        assertThat(fastLogger.isTraceEnabled(mockedMarker)).isTrue();
        Mockito.verify(mockedLogger).isEnabled(ArgumentMatchers.eq(TRACE), ArgumentMatchers.isNull(), ((Object) (ArgumentMatchers.isNull())), ArgumentMatchers.isNull());
        Mockito.verify(mockedLogger).isEnabled(ArgumentMatchers.eq(TRACE), ArgumentMatchers.eq(mockedMarker), ((Object) (ArgumentMatchers.isNull())), ArgumentMatchers.isNull());
    }

    /**
     * FastLogger should not delegate isDebugEnabled when not isDelegating
     */
    @Test
    public void notDelegateIsDebugEnabledWhenNotIsDelegating() {
        FastLogger.setDelegating(false);
        Mockito.when(mockedLogger.getLevel()).thenReturn(INFO);
        assertThat(fastLogger.getLevel()).isEqualTo(INFO);
        assertThat(fastLogger.isDebugEnabled()).isFalse();
        assertThat(fastLogger.isDebugEnabled(mockedMarker)).isFalse();
        Mockito.verify(mockedLogger, Mockito.never()).isEnabled(ArgumentMatchers.eq(DEBUG), ArgumentMatchers.isNull(), ArgumentMatchers.isNull());
        Mockito.verify(mockedLogger, Mockito.never()).isEnabled(ArgumentMatchers.eq(DEBUG), ArgumentMatchers.eq(mockedMarker), ((Object) (ArgumentMatchers.isNull())), ArgumentMatchers.isNull());
    }

    /**
     * FastLogger should not delegate isTraceEnabled when not isDelegating
     */
    @Test
    public void notDelegateIsTraceEnabledWhenNotIsDelegating() {
        FastLogger.setDelegating(false);
        assertThat(fastLogger.getLevel()).isEqualTo(INFO);
        assertThat(fastLogger.isTraceEnabled()).isFalse();
        Mockito.verify(mockedLogger, Mockito.never()).isEnabled(ArgumentMatchers.eq(TRACE), ArgumentMatchers.isNull(), ArgumentMatchers.isNull());
        assertThat(fastLogger.isTraceEnabled(mockedMarker)).isFalse();
        Mockito.verify(mockedLogger, Mockito.never()).isEnabled(ArgumentMatchers.eq(TRACE), ArgumentMatchers.eq(mockedMarker), ((Object) (ArgumentMatchers.isNull())), ArgumentMatchers.isNull());
    }

    /**
     * FastLogger should wrap delegate and return from getExtendedLogger
     */
    @Test
    public void wrapDelegateAndReturnFromGetExtendedLogger() {
        assertThat(fastLogger.getExtendedLogger()).isSameAs(mockedLogger);
    }

    /**
     * FastLogger should delegate getName
     */
    @Test
    public void delegateGetName() {
        assertThat(fastLogger.getName()).isEqualTo(FastLoggerTest.LOGGER_NAME);
        Mockito.verify(mockedLogger, Mockito.never()).getName();
    }
}

