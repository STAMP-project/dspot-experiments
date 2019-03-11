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


import ScannerReport.LineCoverage;
import java.io.File;
import java.util.NoSuchElementException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.sonar.scanner.protocol.output.ScannerReport;


public class ReportIteratorTest {
    @Rule
    public TemporaryFolder temp = new TemporaryFolder();

    private File file;

    private ReportIterator<ScannerReport.LineCoverage> underTest;

    @Test
    public void read_report() {
        underTest = new ReportIterator(file, LineCoverage.parser());
        assertThat(underTest.next().getLine()).isEqualTo(1);
    }

    @Test
    public void do_not_fail_when_calling_has_next_with_iterator_already_closed() {
        underTest = new ReportIterator(file, LineCoverage.parser());
        assertThat(underTest.next().getLine()).isEqualTo(1);
        assertThat(underTest.hasNext()).isFalse();
        underTest.close();
        assertThat(underTest.hasNext()).isFalse();
    }

    @Test(expected = NoSuchElementException.class)
    public void test_error() {
        underTest = new ReportIterator(file, LineCoverage.parser());
        underTest.next();
        // fail !
        underTest.next();
    }
}

