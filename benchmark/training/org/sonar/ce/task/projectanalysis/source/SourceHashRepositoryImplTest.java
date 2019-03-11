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
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import java.util.Arrays;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.sonar.ce.task.projectanalysis.component.Component;
import org.sonar.ce.task.projectanalysis.component.ReportComponent;
import org.sonar.core.hash.SourceHashComputer;
import org.sonar.core.util.CloseableIterator;


@RunWith(DataProviderRunner.class)
public class SourceHashRepositoryImplTest {
    private static final int FILE_REF = 112;

    private static final String FILE_KEY = "file key";

    private static final Component FILE_COMPONENT = ReportComponent.builder(FILE, SourceHashRepositoryImplTest.FILE_REF).setKey(SourceHashRepositoryImplTest.FILE_KEY).build();

    private static final String[] SOME_LINES = new String[]{ "line 1", "line after line 1", "line 4 minus 1", "line 100 by 10" };

    @Rule
    public SourceLinesRepositoryRule sourceLinesRepository = new SourceLinesRepositoryRule();

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private SourceLinesRepository mockedSourceLinesRepository = Mockito.mock(SourceLinesRepository.class);

    private SourceHashRepositoryImpl underTest = new SourceHashRepositoryImpl(sourceLinesRepository);

    private SourceHashRepositoryImpl mockedUnderTest = new SourceHashRepositoryImpl(mockedSourceLinesRepository);

    @Test
    public void getRawSourceHash_throws_NPE_if_Component_argument_is_null() {
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("Specified component can not be null");
        underTest.getRawSourceHash(null);
    }

    @Test
    public void getRawSourceHash_returns_hash_of_lines_from_SourceLinesRepository() {
        sourceLinesRepository.addLines(SourceHashRepositoryImplTest.FILE_REF, SourceHashRepositoryImplTest.SOME_LINES);
        String rawSourceHash = underTest.getRawSourceHash(SourceHashRepositoryImplTest.FILE_COMPONENT);
        SourceHashComputer sourceHashComputer = new SourceHashComputer();
        for (int i = 0; i < (SourceHashRepositoryImplTest.SOME_LINES.length); i++) {
            sourceHashComputer.addLine(SourceHashRepositoryImplTest.SOME_LINES[i], (i < ((SourceHashRepositoryImplTest.SOME_LINES.length) - 1)));
        }
        assertThat(rawSourceHash).isEqualTo(sourceHashComputer.getHash());
    }

    @Test
    public void getRawSourceHash_reads_lines_from_SourceLinesRepository_only_the_first_time() {
        Mockito.when(mockedSourceLinesRepository.readLines(SourceHashRepositoryImplTest.FILE_COMPONENT)).thenReturn(CloseableIterator.from(Arrays.asList(SourceHashRepositoryImplTest.SOME_LINES).iterator()));
        String rawSourceHash = mockedUnderTest.getRawSourceHash(SourceHashRepositoryImplTest.FILE_COMPONENT);
        String rawSourceHash1 = mockedUnderTest.getRawSourceHash(SourceHashRepositoryImplTest.FILE_COMPONENT);
        assertThat(rawSourceHash).isSameAs(rawSourceHash1);
        Mockito.verify(mockedSourceLinesRepository, Mockito.times(1)).readLines(SourceHashRepositoryImplTest.FILE_COMPONENT);
    }

    @Test
    public void getRawSourceHash_let_exception_go_through() {
        IllegalArgumentException thrown = new IllegalArgumentException("this IAE will cause the hash computation to fail");
        Mockito.when(mockedSourceLinesRepository.readLines(SourceHashRepositoryImplTest.FILE_COMPONENT)).thenThrow(thrown);
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(thrown.getMessage());
        mockedUnderTest.getRawSourceHash(SourceHashRepositoryImplTest.FILE_COMPONENT);
    }
}

