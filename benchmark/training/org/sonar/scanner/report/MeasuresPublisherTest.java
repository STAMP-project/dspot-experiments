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


import CoreMetrics.LINES_TO_COVER;
import CoreMetrics.NCLOC_LANGUAGE_DISTRIBUTION;
import ScannerReport.Measure;
import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mockito;
import org.sonar.api.batch.fs.internal.DefaultInputFile;
import org.sonar.api.batch.fs.internal.DefaultInputProject;
import org.sonar.api.batch.sensor.measure.internal.DefaultMeasure;
import org.sonar.core.util.CloseableIterator;
import org.sonar.scanner.protocol.output.ScannerReport;
import org.sonar.scanner.protocol.output.ScannerReportReader;
import org.sonar.scanner.protocol.output.ScannerReportWriter;
import org.sonar.scanner.scan.measure.MeasureCache;


public class MeasuresPublisherTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Rule
    public TemporaryFolder temp = new TemporaryFolder();

    private MeasureCache measureCache;

    private MeasuresPublisher publisher;

    private File outputDir;

    private ScannerReportWriter writer;

    private DefaultInputFile inputFile;

    private DefaultInputProject project;

    @Test
    public void publishMeasures() throws Exception {
        DefaultMeasure<Integer> measure = new DefaultMeasure<Integer>().forMetric(LINES_TO_COVER).withValue(2);
        // String value
        DefaultMeasure<String> stringMeasure = new DefaultMeasure<String>().forMetric(NCLOC_LANGUAGE_DISTRIBUTION).withValue("foo bar");
        Mockito.when(measureCache.byComponentKey(inputFile.key())).thenReturn(Arrays.asList(measure, stringMeasure));
        publisher.publish(writer);
        ScannerReportReader reader = new ScannerReportReader(outputDir);
        assertThat(reader.readComponentMeasures(project.scannerId())).hasSize(0);
        try (CloseableIterator<ScannerReport.Measure> componentMeasures = reader.readComponentMeasures(inputFile.scannerId())) {
            assertThat(componentMeasures).hasSize(2);
        }
    }

    @Test
    public void fail_with_IAE_when_measure_has_no_value() throws Exception {
        DefaultMeasure<Integer> measure = new DefaultMeasure<Integer>().forMetric(LINES_TO_COVER);
        Mockito.when(measureCache.byComponentKey(inputFile.key())).thenReturn(Collections.singletonList(measure));
        try {
            publisher.publish(writer);
            Assert.fail();
        } catch (RuntimeException e) {
            assertThat(ExceptionUtils.getFullStackTrace(e)).contains("Measure on metric 'lines_to_cover' and component 'foo:src/Foo.php' has no value, but it's not allowed");
        }
    }
}

