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
package org.sonar.scanner.report;


import CoreMetrics.CONDITIONS_BY_LINE;
import CoreMetrics.CONDITIONS_BY_LINE_KEY;
import CoreMetrics.COVERAGE_LINE_HITS_DATA;
import CoreMetrics.COVERAGE_LINE_HITS_DATA_KEY;
import CoreMetrics.COVERED_CONDITIONS_BY_LINE;
import CoreMetrics.COVERED_CONDITIONS_BY_LINE_KEY;
import java.io.File;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mockito;
import org.sonar.api.batch.fs.internal.DefaultInputFile;
import org.sonar.api.batch.sensor.measure.internal.DefaultMeasure;
import org.sonar.core.util.CloseableIterator;
import org.sonar.scanner.protocol.output.ScannerReport.LineCoverage;
import org.sonar.scanner.protocol.output.ScannerReportReader;
import org.sonar.scanner.protocol.output.ScannerReportWriter;
import org.sonar.scanner.scan.measure.MeasureCache;


public class CoveragePublisherTest {
    @Rule
    public TemporaryFolder temp = new TemporaryFolder();

    private MeasureCache measureCache;

    private CoveragePublisher publisher;

    private DefaultInputFile inputFile;

    @Test
    public void publishCoverage() throws Exception {
        DefaultMeasure<String> utLineHits = new DefaultMeasure<String>().forMetric(COVERAGE_LINE_HITS_DATA).withValue("2=1;3=1;5=0;6=3");
        Mockito.when(measureCache.byMetric("foo:src/Foo.php", COVERAGE_LINE_HITS_DATA_KEY)).thenReturn(((DefaultMeasure) (utLineHits)));
        DefaultMeasure<String> conditionsByLine = new DefaultMeasure<String>().forMetric(CONDITIONS_BY_LINE).withValue("3=4");
        Mockito.when(measureCache.byMetric("foo:src/Foo.php", CONDITIONS_BY_LINE_KEY)).thenReturn(((DefaultMeasure) (conditionsByLine)));
        DefaultMeasure<String> coveredConditionsByUts = new DefaultMeasure<String>().forMetric(COVERED_CONDITIONS_BY_LINE).withValue("3=2");
        Mockito.when(measureCache.byMetric("foo:src/Foo.php", COVERED_CONDITIONS_BY_LINE_KEY)).thenReturn(((DefaultMeasure) (coveredConditionsByUts)));
        File outputDir = temp.newFolder();
        ScannerReportWriter writer = new ScannerReportWriter(outputDir);
        publisher.publish(writer);
        try (CloseableIterator<LineCoverage> it = new ScannerReportReader(outputDir).readComponentCoverage(inputFile.scannerId())) {
            assertThat(it.next()).isEqualTo(LineCoverage.newBuilder().setLine(2).setHits(true).build());
            assertThat(it.next()).isEqualTo(LineCoverage.newBuilder().setLine(3).setHits(true).setConditions(4).setCoveredConditions(2).build());
            assertThat(it.next()).isEqualTo(LineCoverage.newBuilder().setLine(5).setHits(false).build());
        }
    }
}

