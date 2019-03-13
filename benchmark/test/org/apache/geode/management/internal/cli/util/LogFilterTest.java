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
package org.apache.geode.management.internal.cli.util;


import Level.DEBUG;
import Level.ERROR;
import Level.INFO;
import Level.TRACE;
import Level.WARN;
import java.io.File;
import java.nio.file.FileSystem;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.spi.FileSystemProvider;
import java.time.LocalDateTime;
import org.apache.geode.test.junit.categories.GfshTest;
import org.apache.geode.test.junit.categories.LoggingTest;
import org.apache.logging.log4j.Level;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.Mockito;


@Category({ GfshTest.class, LoggingTest.class })
public class LogFilterTest {
    @Test
    public void permittedLogLevelsCanFilterLines() {
        LogFilter logFilter = new LogFilter(Level.INFO, null, null);
        LocalDateTime now = LocalDateTime.now();
        assertThat(logFilter.acceptsLogEntry(INFO, now)).isEqualTo(LineFilterResult.LINE_ACCEPTED);
        assertThat(logFilter.acceptsLogEntry(WARN, now)).isEqualTo(LineFilterResult.LINE_ACCEPTED);
        assertThat(logFilter.acceptsLogEntry(DEBUG, now)).isEqualTo(LineFilterResult.LINE_REJECTED);
        assertThat(logFilter.acceptsLogEntry(TRACE, now)).isEqualTo(LineFilterResult.LINE_REJECTED);
    }

    @Test
    public void permittedOnlyLogLevels() {
        LogFilter logFilter = new LogFilter(Level.INFO, true, null, null);
        LocalDateTime now = LocalDateTime.now();
        assertThat(logFilter.acceptsLogEntry(INFO, now)).isEqualTo(LineFilterResult.LINE_ACCEPTED);
        assertThat(logFilter.acceptsLogEntry(WARN, now)).isEqualTo(LineFilterResult.LINE_REJECTED);
        assertThat(logFilter.acceptsLogEntry(DEBUG, now)).isEqualTo(LineFilterResult.LINE_REJECTED);
        assertThat(logFilter.acceptsLogEntry(TRACE, now)).isEqualTo(LineFilterResult.LINE_REJECTED);
    }

    @Test
    public void permittedLogLevelsALL() {
        LogFilter logFilter = new LogFilter(Level.ALL, null, null);
        LocalDateTime now = LocalDateTime.now();
        assertThat(logFilter.acceptsLogEntry(INFO, now)).isEqualTo(LineFilterResult.LINE_ACCEPTED);
        assertThat(logFilter.acceptsLogEntry(WARN, now)).isEqualTo(LineFilterResult.LINE_ACCEPTED);
        assertThat(logFilter.acceptsLogEntry(DEBUG, now)).isEqualTo(LineFilterResult.LINE_ACCEPTED);
        assertThat(logFilter.acceptsLogEntry(TRACE, now)).isEqualTo(LineFilterResult.LINE_ACCEPTED);
    }

    @Test
    public void startDateCanFilterLines() {
        LocalDateTime startDate = LocalDateTime.now().minusDays(2);
        LogFilter logFilter = new LogFilter(Level.ALL, startDate, null);
        assertThat(logFilter.acceptsLogEntry(INFO, LocalDateTime.now())).isEqualTo(LineFilterResult.LINE_ACCEPTED);
        assertThat(logFilter.acceptsLogEntry(INFO, startDate)).isEqualTo(LineFilterResult.LINE_ACCEPTED);
        assertThat(logFilter.acceptsLogEntry(DEBUG, startDate)).isEqualTo(LineFilterResult.LINE_ACCEPTED);
        assertThat(logFilter.acceptsLogEntry(INFO, LocalDateTime.now().minusDays(3))).isEqualTo(LineFilterResult.LINE_REJECTED);
    }

    @Test
    public void endDateCanFilterLines() {
        LocalDateTime endDate = LocalDateTime.now().minusDays(2);
        LogFilter logFilter = new LogFilter(Level.ALL, null, endDate);
        assertThat(logFilter.acceptsLogEntry(INFO, LocalDateTime.now().minusDays(3))).isEqualTo(LineFilterResult.LINE_ACCEPTED);
        assertThat(logFilter.acceptsLogEntry(INFO, endDate)).isEqualTo(LineFilterResult.LINE_ACCEPTED);
        assertThat(logFilter.acceptsLogEntry(DEBUG, endDate)).isEqualTo(LineFilterResult.LINE_ACCEPTED);
        assertThat(logFilter.acceptsLogEntry(INFO, LocalDateTime.now())).isEqualTo(LineFilterResult.REMAINDER_OF_FILE_REJECTED);
    }

    @Test
    public void filterWorksWithLevelBasedAndTimeBasedFiltering() {
        LocalDateTime startDate = LocalDateTime.now().minusDays(5);
        LocalDateTime endDate = LocalDateTime.now().minusDays(2);
        LogFilter logFilter = new LogFilter(Level.INFO, startDate, endDate);
        assertThat(logFilter.acceptsLogEntry(ERROR, LocalDateTime.now().minusDays(6))).isEqualTo(LineFilterResult.LINE_REJECTED);
        assertThat(logFilter.acceptsLogEntry(INFO, LocalDateTime.now().minusDays(6))).isEqualTo(LineFilterResult.LINE_REJECTED);
        assertThat(logFilter.acceptsLogEntry(null)).isEqualTo(LineFilterResult.LINE_REJECTED);
        assertThat(logFilter.acceptsLogEntry(ERROR, LocalDateTime.now().minusDays(6))).isEqualTo(LineFilterResult.LINE_REJECTED);
        assertThat(logFilter.acceptsLogEntry(null)).isEqualTo(LineFilterResult.LINE_REJECTED);
        assertThat(logFilter.acceptsLogEntry(INFO, LocalDateTime.now().minusDays(4))).isEqualTo(LineFilterResult.LINE_ACCEPTED);
        assertThat(logFilter.acceptsLogEntry(null)).isEqualTo(LineFilterResult.LINE_ACCEPTED);
        assertThat(logFilter.acceptsLogEntry(ERROR, LocalDateTime.now().minusDays(1))).isEqualTo(LineFilterResult.REMAINDER_OF_FILE_REJECTED);
        assertThat(logFilter.acceptsLogEntry(null)).isEqualTo(LineFilterResult.REMAINDER_OF_FILE_REJECTED);
        assertThat(logFilter.acceptsLogEntry(INFO, LocalDateTime.now().minusDays(1))).isEqualTo(LineFilterResult.REMAINDER_OF_FILE_REJECTED);
    }

    @Test
    public void firstLinesAreAcceptedIfParsableLineHasNotBeenSeenYet() {
        LogFilter logFilter = new LogFilter(Level.INFO, null, null);
        assertThat(logFilter.acceptsLogEntry(null)).isEqualTo(LineFilterResult.LINE_ACCEPTED);
        assertThat(logFilter.acceptsLogEntry(null)).isEqualTo(LineFilterResult.LINE_ACCEPTED);
        assertThat(logFilter.acceptsLogEntry(DEBUG, LocalDateTime.now())).isEqualTo(LineFilterResult.LINE_REJECTED);
        assertThat(logFilter.acceptsLogEntry(null)).isEqualTo(LineFilterResult.LINE_REJECTED);
    }

    @Test
    public void testAcceptFileWithCreateTimeNotAvailale() {
        Path path = Mockito.mock(Path.class);
        Mockito.when(path.toFile()).thenReturn(Mockito.mock(File.class));
        Mockito.when(path.toFile().lastModified()).thenReturn(System.currentTimeMillis());
        Mockito.when(path.getFileSystem()).thenThrow(SecurityException.class);
        // a filter with no start/end date should accept this file
        LogFilter filter = new LogFilter(Level.INFO, null, null);
        assertThat(filter.acceptsFile(path)).isTrue();
        // a filter with a start date of now should not accept the file
        filter = new LogFilter(Level.INFO, LocalDateTime.now(), null);
        assertThat(filter.acceptsFile(path)).isFalse();
        // a filter with a start date of now minus an hour should not accept the file
        filter = new LogFilter(Level.INFO, LocalDateTime.now().minusHours(1), null);
        assertThat(filter.acceptsFile(path)).isTrue();
        // a filter with an end date of now should accept the file
        filter = new LogFilter(Level.INFO, null, LocalDateTime.now());
        assertThat(filter.acceptsFile(path)).isTrue();
        // a filter with an end date of an hour ago should also accept the file, because we only
        // know the last modified time of the file, when don't know what time this file is created, it
        // may still be created more than an hour ago.
        filter = new LogFilter(Level.INFO, null, LocalDateTime.now().minusHours(1));
        assertThat(filter.acceptsFile(path)).isTrue();
    }

    @Test
    public void testAcceptFileWithCreateTimeAvailable() throws Exception {
        Path path = Mockito.mock(Path.class);
        Mockito.when(path.toFile()).thenReturn(Mockito.mock(File.class));
        Mockito.when(path.toFile().lastModified()).thenReturn(System.currentTimeMillis());
        BasicFileAttributes attributes = Mockito.mock(BasicFileAttributes.class);
        Mockito.when(path.getFileSystem()).thenReturn(Mockito.mock(FileSystem.class));
        Mockito.when(path.getFileSystem().provider()).thenReturn(Mockito.mock(FileSystemProvider.class));
        Mockito.when(path.getFileSystem().provider().readAttributes(path, BasicFileAttributes.class)).thenReturn(attributes);
        // a filter with no start/end date should accept this file
        LogFilter filter = new LogFilter(Level.INFO, null, null);
        assertThat(filter.acceptsFile(path)).isTrue();
        // a filter with a start date of now should not accept the file
        filter = new LogFilter(Level.INFO, LocalDateTime.now(), null);
        assertThat(filter.acceptsFile(path)).isFalse();
        // a filter with a start date of now minus an hour should not accept the file
        filter = new LogFilter(Level.INFO, LocalDateTime.now().minusHours(1), null);
        assertThat(filter.acceptsFile(path)).isTrue();
        // a filter with an end date of now should accept the file
        filter = new LogFilter(Level.INFO, null, LocalDateTime.now());
        assertThat(filter.acceptsFile(path)).isTrue();
        // a filter with an end date of an hour ago should accept the file
        filter = new LogFilter(Level.INFO, null, LocalDateTime.now().minusHours(1));
        assertThat(filter.acceptsFile(path)).isTrue();
    }
}

