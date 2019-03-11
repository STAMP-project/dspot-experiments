/**
 * SonarQube
 * Copyright (C) 2009-2019 SonarSource SA
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonar.ce.task.projectanalysis.source;


import DbFileSources.Line.Builder;
import LineReader.Data;
import SourceLineReadersFactory.LineReaders;
import SourceLineReadersFactory.LineReadersImpl;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.sonar.ce.task.projectanalysis.scm.Changeset;
import org.sonar.ce.task.projectanalysis.source.linereader.LineReader;
import org.sonar.ce.task.projectanalysis.source.linereader.LineReader.ReadError;
import org.sonar.ce.task.projectanalysis.source.linereader.ScmLineReader;
import org.sonar.core.util.CloseableIterator;
import org.sonar.db.protobuf.DbFileSources;


public class LineReadersImplTest {
    private static final Consumer<ReadError> NOOP_READ_ERROR_CONSUMER = ( readError) -> {
    };

    @Test
    public void close_closes_all_closeables() {
        LineReader r1 = Mockito.mock(LineReader.class);
        LineReader r2 = Mockito.mock(LineReader.class);
        CloseableIterator<Object> c1 = LineReadersImplTest.newCloseableIteratorMock();
        CloseableIterator<Object> c2 = LineReadersImplTest.newCloseableIteratorMock();
        SourceLineReadersFactory.LineReaders lineReaders = new SourceLineReadersFactory.LineReadersImpl(Arrays.asList(r1, r2), null, Arrays.asList(c1, c2));
        lineReaders.close();
        Mockito.verify(c1).close();
        Mockito.verify(c2).close();
        Mockito.verifyNoMoreInteractions(c1, c2);
        Mockito.verifyZeroInteractions(r1, r2);
    }

    @Test
    public void read_calls_all_readers() {
        LineReader r1 = Mockito.mock(LineReader.class);
        LineReader r2 = Mockito.mock(LineReader.class);
        CloseableIterator<Object> c1 = LineReadersImplTest.newCloseableIteratorMock();
        CloseableIterator<Object> c2 = LineReadersImplTest.newCloseableIteratorMock();
        SourceLineReadersFactory.LineReadersImpl lineReaders = new SourceLineReadersFactory.LineReadersImpl(Arrays.asList(r1, r2), null, Arrays.asList(c1, c2));
        DbFileSources.Line.Builder builder = DbFileSources.Line.newBuilder();
        lineReaders.read(builder, LineReadersImplTest.NOOP_READ_ERROR_CONSUMER);
        Mockito.verify(r1).read(builder);
        Mockito.verify(r2).read(builder);
        Mockito.verifyNoMoreInteractions(r1, r2);
        Mockito.verifyZeroInteractions(c1, c2);
    }

    @Test
    public void read_calls_ReaderError_consumer_with_each_read_error_returned_by_all_readers() {
        int readErrorCount = 2 + (2 * (new Random().nextInt(10)));
        int halfCount = readErrorCount / 2;
        ReadError[] expectedReadErrors = IntStream.range(0, readErrorCount).mapToObj(( i) -> new ReadError(Data.SYMBOLS, i)).toArray(ReadError[]::new);
        LineReader[] lineReaders = IntStream.range(0, halfCount).mapToObj(( i) -> {
            LineReader lineReader = Mockito.mock(LineReader.class);
            Mockito.when(lineReader.read(ArgumentMatchers.any())).thenReturn(Optional.of(expectedReadErrors[i])).thenReturn(Optional.of(expectedReadErrors[(i + halfCount)])).thenReturn(Optional.empty());
            return lineReader;
        }).toArray(LineReader[]::new);
        DbFileSources.Line.Builder builder = DbFileSources.Line.newBuilder();
        SourceLineReadersFactory.LineReadersImpl underTest = new SourceLineReadersFactory.LineReadersImpl(Arrays.stream(lineReaders).collect(Collectors.toList()), null, Collections.emptyList());
        List<ReadError> readErrors = new ArrayList<>();
        // calls first mocked result of each Reader mock => we get first half read errors
        underTest.read(builder, readErrors::add);
        assertThat(readErrors).contains(Arrays.stream(expectedReadErrors).limit(halfCount).toArray(ReadError[]::new));
        // calls first mocked result of each Reader mock => we get second half read errors
        readErrors.clear();
        underTest.read(builder, readErrors::add);
        assertThat(readErrors).contains(Arrays.stream(expectedReadErrors).skip(halfCount).toArray(ReadError[]::new));
        // calls third mocked result of each Reader mock (empty) => consumer is never called => empty list
        readErrors.clear();
        underTest.read(builder, readErrors::add);
        assertThat(readErrors).isEmpty();
    }

    @Test
    public void getLatestChangeWithRevision_delegates_to_ScmLineReader_if_non_null() {
        ScmLineReader scmLineReader = Mockito.mock(ScmLineReader.class);
        Changeset changeset = Changeset.newChangesetBuilder().setDate(0L).build();
        Mockito.when(scmLineReader.getLatestChangeWithRevision()).thenReturn(changeset);
        SourceLineReadersFactory.LineReaders lineReaders = new SourceLineReadersFactory.LineReadersImpl(Collections.emptyList(), scmLineReader, Collections.emptyList());
        assertThat(lineReaders.getLatestChangeWithRevision()).isEqualTo(changeset);
        Mockito.verify(scmLineReader).getLatestChangeWithRevision();
        Mockito.verifyNoMoreInteractions(scmLineReader);
    }

    @Test
    public void getLatestChangeWithRevision_returns_null_if_ScmLineReader_is_null() {
        SourceLineReadersFactory.LineReaders lineReaders = new SourceLineReadersFactory.LineReadersImpl(Collections.emptyList(), null, Collections.emptyList());
        assertThat(lineReaders.getLatestChangeWithRevision()).isNull();
    }
}

