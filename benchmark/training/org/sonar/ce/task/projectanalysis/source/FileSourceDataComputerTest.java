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


import Component.Type.FILE;
import DbFileSources.Line.Builder;
import LineReader.Data;
import LineReader.ReadError;
import SourceLineReadersFactory.LineReaders;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.sonar.ce.task.projectanalysis.component.Component;
import org.sonar.ce.task.projectanalysis.component.ReportComponent;
import org.sonar.ce.task.projectanalysis.scm.Changeset;
import org.sonar.ce.task.projectanalysis.source.SourceLinesHashRepositoryImpl.LineHashesComputer;
import org.sonar.ce.task.projectanalysis.source.linereader.LineReader;
import org.sonar.core.util.CloseableIterator;
import org.sonar.db.protobuf.DbFileSources;

import static DbFileSources.Line.Builder;


public class FileSourceDataComputerTest {
    private static final Component FILE = ReportComponent.builder(Component.Type.FILE, 1).build();

    private SourceLinesRepository sourceLinesRepository = Mockito.mock(SourceLinesRepository.class);

    private LineHashesComputer lineHashesComputer = Mockito.mock(LineHashesComputer.class);

    private SourceLineReadersFactory sourceLineReadersFactory = Mockito.mock(SourceLineReadersFactory.class);

    private SourceLinesHashRepository sourceLinesHashRepository = Mockito.mock(SourceLinesHashRepository.class);

    private FileSourceDataComputer underTest = new FileSourceDataComputer(sourceLinesRepository, sourceLineReadersFactory, sourceLinesHashRepository);

    private FileSourceDataWarnings fileSourceDataWarnings = Mockito.mock(FileSourceDataWarnings.class);

    private LineReaders lineReaders = Mockito.mock(LineReaders.class);

    @Test
    public void compute_calls_read_for_each_line_and_passe_read_error_to_fileSourceDataWarnings() {
        int lineCount = 1 + (new Random().nextInt(10));
        List<String> lines = IntStream.range(0, lineCount).mapToObj(( i) -> "line" + i).collect(Collectors.toList());
        Mockito.when(sourceLinesRepository.readLines(FileSourceDataComputerTest.FILE)).thenReturn(CloseableIterator.from(lines.iterator()));
        Mockito.when(sourceLineReadersFactory.getLineReaders(FileSourceDataComputerTest.FILE)).thenReturn(lineReaders);
        Mockito.when(sourceLinesHashRepository.getLineHashesComputerToPersist(FileSourceDataComputerTest.FILE)).thenReturn(lineHashesComputer);
        // mock an implementation that will call the ReadErrorConsumer in order to verify that the provided consumer is
        // doing what we expect: pass readError to fileSourceDataWarnings
        int randomStartPoint = new Random().nextInt(500);
        Mockito.doAnswer(new Answer() {
            int i = randomStartPoint;

            @Override
            public Object answer(InvocationOnMock invocation) {
                Consumer<LineReader.ReadError> readErrorConsumer = invocation.getArgument(1);
                readErrorConsumer.accept(new LineReader.ReadError(Data.SYMBOLS, ((i)++)));
                return null;
            }
        }).when(lineReaders).read(ArgumentMatchers.any(), ArgumentMatchers.any());
        underTest.compute(FileSourceDataComputerTest.FILE, fileSourceDataWarnings);
        ArgumentCaptor<DbFileSources.Line.Builder> lineBuilderCaptor = ArgumentCaptor.forClass(Builder.class);
        Mockito.verify(lineReaders, Mockito.times(lineCount)).read(lineBuilderCaptor.capture(), ArgumentMatchers.any());
        assertThat(lineBuilderCaptor.getAllValues()).extracting(Builder::getSource).containsOnlyElementsOf(lines);
        assertThat(lineBuilderCaptor.getAllValues()).extracting(Builder::getLine).containsExactly(IntStream.range(1, (lineCount + 1)).boxed().toArray(Integer[]::new));
        ArgumentCaptor<LineReader.ReadError> readErrorCaptor = ArgumentCaptor.forClass(ReadError.class);
        Mockito.verify(fileSourceDataWarnings, Mockito.times(lineCount)).addWarning(ArgumentMatchers.same(FileSourceDataComputerTest.FILE), readErrorCaptor.capture());
        assertThat(readErrorCaptor.getAllValues()).extracting(LineReader.ReadError::getLine).containsExactly(IntStream.range(randomStartPoint, (randomStartPoint + lineCount)).boxed().toArray(Integer[]::new));
    }

    @Test
    public void compute_builds_data_object_from_lines() {
        int lineCount = 1 + (new Random().nextInt(10));
        int randomStartPoint = new Random().nextInt(500);
        List<String> lines = IntStream.range(0, lineCount).mapToObj(( i) -> "line" + i).collect(Collectors.toList());
        List<String> expectedLineHashes = IntStream.range(0, (1 + (new Random().nextInt(12)))).mapToObj(( i) -> "str_" + i).collect(Collectors.toList());
        Changeset expectedChangeset = Changeset.newChangesetBuilder().setDate(((long) (new Random().nextInt(9999)))).build();
        String expectedSrcHash = FileSourceDataComputerTest.computeSrcHash(lines);
        CloseableIterator<String> lineIterator = Mockito.spy(CloseableIterator.from(lines.iterator()));
        DbFileSources.Data.Builder expectedLineDataBuilder = DbFileSources.Data.newBuilder();
        for (int i = 0; i < (lines.size()); i++) {
            // scmAuthor will be set with specific value by our mock implementation of LinesReaders.read()
            expectedLineDataBuilder.addLinesBuilder().setSource(lines.get(i)).setLine((i + 1)).setScmAuthor(("reader_called_" + (randomStartPoint + i)));
        }
        Mockito.when(sourceLinesRepository.readLines(FileSourceDataComputerTest.FILE)).thenReturn(lineIterator);
        Mockito.when(sourceLineReadersFactory.getLineReaders(FileSourceDataComputerTest.FILE)).thenReturn(lineReaders);
        Mockito.when(sourceLinesHashRepository.getLineHashesComputerToPersist(FileSourceDataComputerTest.FILE)).thenReturn(lineHashesComputer);
        Mockito.when(lineHashesComputer.getResult()).thenReturn(expectedLineHashes);
        Mockito.when(lineReaders.getLatestChangeWithRevision()).thenReturn(expectedChangeset);
        // mocked implementation of LineReader.read to ensure changes done by it to the lineBuilder argument actually end
        // up in the FileSourceDataComputer.Data object returned
        Mockito.doAnswer(new Answer() {
            int i = 0;

            @Override
            public Object answer(InvocationOnMock invocation) {
                DbFileSources.Line.Builder lineBuilder = invocation.getArgument(0);
                lineBuilder.setScmAuthor(("reader_called_" + (randomStartPoint + ((i)++))));
                return null;
            }
        }).when(lineReaders).read(ArgumentMatchers.any(), ArgumentMatchers.any());
        FileSourceDataComputer.Data data = underTest.compute(FileSourceDataComputerTest.FILE, fileSourceDataWarnings);
        assertThat(data.getLineHashes()).isEqualTo(expectedLineHashes);
        assertThat(data.getSrcHash()).isEqualTo(expectedSrcHash);
        assertThat(data.getLatestChangeWithRevision()).isSameAs(expectedChangeset);
        assertThat(data.getLineData()).isEqualTo(expectedLineDataBuilder.build());
        Mockito.verify(lineIterator).close();
        Mockito.verify(lineReaders).close();
    }
}

