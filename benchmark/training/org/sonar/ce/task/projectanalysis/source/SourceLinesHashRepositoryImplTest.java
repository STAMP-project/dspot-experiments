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


import LineHashVersion.WITHOUT_SIGNIFICANT_CODE;
import LineHashVersion.WITH_SIGNIFICANT_CODE;
import Type.FILE;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.Optional;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;
import org.sonar.api.utils.internal.JUnitTempFolder;
import org.sonar.ce.task.projectanalysis.component.Component;
import org.sonar.ce.task.projectanalysis.component.ReportComponent;
import org.sonar.ce.task.projectanalysis.source.SourceLinesHashRepositoryImpl.CachedLineHashesComputer;
import org.sonar.ce.task.projectanalysis.source.SourceLinesHashRepositoryImpl.LineHashesComputer;
import org.sonar.ce.task.projectanalysis.source.SourceLinesHashRepositoryImpl.SignificantCodeLineHashesComputer;
import org.sonar.core.hash.LineRange;
import org.sonar.core.hash.SourceLineHashesComputer;


public class SourceLinesHashRepositoryImplTest {
    private static final int FILE_REF = 1;

    @Rule
    public JUnitTempFolder temp = new JUnitTempFolder();

    @Rule
    public SourceLinesRepositoryRule sourceLinesRepository = new SourceLinesRepositoryRule();

    private SourceLinesHashCache sourceLinesHashCache;

    private SignificantCodeRepository significantCodeRepository = Mockito.mock(SignificantCodeRepository.class);

    private DbLineHashVersion dbLineHashVersion = Mockito.mock(DbLineHashVersion.class);

    private Component file = ReportComponent.builder(FILE, SourceLinesHashRepositoryImplTest.FILE_REF).build();

    private SourceLinesHashRepositoryImpl underTest;

    @Test
    public void should_return_with_significant_code_if_report_contains_it() {
        Mockito.when(significantCodeRepository.getRangesPerLine(file)).thenReturn(Optional.of(new LineRange[0]));
        assertThat(underTest.getLineHashesVersion(file)).isEqualTo(WITH_SIGNIFICANT_CODE.getDbValue());
        Mockito.verify(significantCodeRepository).getRangesPerLine(file);
        Mockito.verifyNoMoreInteractions(significantCodeRepository);
        Mockito.verifyZeroInteractions(dbLineHashVersion);
    }

    @Test
    public void should_return_without_significant_code_if_report_does_not_contain_it() {
        Mockito.when(significantCodeRepository.getRangesPerLine(file)).thenReturn(Optional.empty());
        assertThat(underTest.getLineHashesVersion(file)).isEqualTo(WITHOUT_SIGNIFICANT_CODE.getDbValue());
        Mockito.verify(significantCodeRepository).getRangesPerLine(file);
        Mockito.verifyNoMoreInteractions(significantCodeRepository);
        Mockito.verifyZeroInteractions(dbLineHashVersion);
    }

    @Test
    public void should_create_hash_without_significant_code_if_db_has_no_significant_code() {
        Mockito.when(dbLineHashVersion.hasLineHashesWithSignificantCode(file)).thenReturn(false);
        List<String> lineHashes = underTest.getLineHashesMatchingDBVersion(file);
        assertLineHashes(lineHashes, "line1", "line2", "line3");
        Mockito.verify(dbLineHashVersion).hasLineHashesWithSignificantCode(file);
        Mockito.verifyNoMoreInteractions(dbLineHashVersion);
        Mockito.verifyZeroInteractions(significantCodeRepository);
    }

    @Test
    public void should_create_hash_without_significant_code_if_report_has_no_significant_code() {
        Mockito.when(dbLineHashVersion.hasLineHashesWithSignificantCode(file)).thenReturn(true);
        Mockito.when(significantCodeRepository.getRangesPerLine(file)).thenReturn(Optional.empty());
        List<String> lineHashes = underTest.getLineHashesMatchingDBVersion(file);
        assertLineHashes(lineHashes, "line1", "line2", "line3");
        Mockito.verify(dbLineHashVersion).hasLineHashesWithSignificantCode(file);
        Mockito.verifyNoMoreInteractions(dbLineHashVersion);
        Mockito.verify(significantCodeRepository).getRangesPerLine(file);
        Mockito.verifyNoMoreInteractions(significantCodeRepository);
    }

    @Test
    public void should_create_hash_with_significant_code() {
        LineRange[] lineRanges = new LineRange[]{ new LineRange(0, 1), null, new LineRange(1, 5) };
        Mockito.when(dbLineHashVersion.hasLineHashesWithSignificantCode(file)).thenReturn(true);
        Mockito.when(significantCodeRepository.getRangesPerLine(file)).thenReturn(Optional.of(lineRanges));
        List<String> lineHashes = underTest.getLineHashesMatchingDBVersion(file);
        assertLineHashes(lineHashes, "l", "", "ine3");
        Mockito.verify(dbLineHashVersion).hasLineHashesWithSignificantCode(file);
        Mockito.verifyNoMoreInteractions(dbLineHashVersion);
        Mockito.verify(significantCodeRepository).getRangesPerLine(file);
        Mockito.verifyNoMoreInteractions(significantCodeRepository);
    }

    @Test
    public void should_return_version_of_line_hashes_with_significant_code_in_the_report() {
        LineRange[] lineRanges = new LineRange[]{ new LineRange(0, 1), null, new LineRange(1, 5) };
        Mockito.when(significantCodeRepository.getRangesPerLine(file)).thenReturn(Optional.of(lineRanges));
        assertThat(underTest.getLineHashesVersion(file)).isEqualTo(WITH_SIGNIFICANT_CODE.getDbValue());
        Mockito.verify(significantCodeRepository).getRangesPerLine(file);
        Mockito.verifyNoMoreInteractions(significantCodeRepository);
        Mockito.verifyZeroInteractions(dbLineHashVersion);
    }

    @Test
    public void should_return_version_of_line_hashes_without_significant_code_in_the_report() {
        Mockito.when(significantCodeRepository.getRangesPerLine(file)).thenReturn(Optional.empty());
        assertThat(underTest.getLineHashesVersion(file)).isEqualTo(WITHOUT_SIGNIFICANT_CODE.getDbValue());
        Mockito.verify(significantCodeRepository).getRangesPerLine(file);
        Mockito.verifyNoMoreInteractions(significantCodeRepository);
        Mockito.verifyZeroInteractions(dbLineHashVersion);
    }

    @Test
    public void should_persist_with_significant_code_from_cache_if_possible() {
        List<String> lineHashes = Lists.newArrayList("line1", "line2", "line3");
        LineRange[] lineRanges = new LineRange[]{ new LineRange(0, 1), null, new LineRange(1, 5) };
        sourceLinesHashCache.computeIfAbsent(file, ( c) -> lineHashes);
        Mockito.when(dbLineHashVersion.hasLineHashesWithSignificantCode(file)).thenReturn(true);
        Mockito.when(significantCodeRepository.getRangesPerLine(file)).thenReturn(Optional.of(lineRanges));
        LineHashesComputer hashesComputer = underTest.getLineHashesComputerToPersist(file);
        assertThat(hashesComputer).isInstanceOf(CachedLineHashesComputer.class);
        assertThat(hashesComputer.getResult()).isEqualTo(lineHashes);
    }

    @Test
    public void should_persist_without_significant_code_from_cache_if_possible() {
        List<String> lineHashes = Lists.newArrayList("line1", "line2", "line3");
        sourceLinesHashCache.computeIfAbsent(file, ( c) -> lineHashes);
        Mockito.when(dbLineHashVersion.hasLineHashesWithSignificantCode(file)).thenReturn(false);
        Mockito.when(significantCodeRepository.getRangesPerLine(file)).thenReturn(Optional.empty());
        LineHashesComputer hashesComputer = underTest.getLineHashesComputerToPersist(file);
        assertThat(hashesComputer).isInstanceOf(CachedLineHashesComputer.class);
        assertThat(hashesComputer.getResult()).isEqualTo(lineHashes);
    }

    @Test
    public void should_generate_to_persist_if_needed() {
        List<String> lineHashes = Lists.newArrayList("line1", "line2", "line3");
        LineRange[] lineRanges = new LineRange[]{ new LineRange(0, 1), null, new LineRange(1, 5) };
        sourceLinesHashCache.computeIfAbsent(file, ( c) -> lineHashes);
        // DB has line hashes without significant code and significant code is available in the report, so we need to generate new line hashes
        Mockito.when(dbLineHashVersion.hasLineHashesWithSignificantCode(file)).thenReturn(false);
        Mockito.when(significantCodeRepository.getRangesPerLine(file)).thenReturn(Optional.of(lineRanges));
        LineHashesComputer hashesComputer = underTest.getLineHashesComputerToPersist(file);
        assertThat(hashesComputer).isInstanceOf(SignificantCodeLineHashesComputer.class);
    }

    @Test
    public void SignificantCodeLineHashesComputer_delegates_after_taking_ranges_into_account() {
        LineRange[] lineRanges = new LineRange[]{ new LineRange(0, 1), null, new LineRange(1, 5), new LineRange(2, 7), new LineRange(4, 5) };
        SourceLineHashesComputer lineHashComputer = Mockito.mock(SourceLineHashesComputer.class);
        SignificantCodeLineHashesComputer computer = new SignificantCodeLineHashesComputer(lineHashComputer, lineRanges);
        computer.addLine("testline");
        computer.addLine("testline");
        computer.addLine("testline");
        computer.addLine("testline");
        computer.addLine("testline");
        computer.addLine("testline");
        Mockito.verify(lineHashComputer).addLine("t");
        // there is an extra line at the end which will be ignored since there is no range for it
        Mockito.verify(lineHashComputer, Mockito.times(2)).addLine("");
        Mockito.verify(lineHashComputer).addLine("estl");
        Mockito.verify(lineHashComputer).addLine("stlin");
        Mockito.verify(lineHashComputer).addLine("l");
        Mockito.verifyNoMoreInteractions(lineHashComputer);
    }
}

