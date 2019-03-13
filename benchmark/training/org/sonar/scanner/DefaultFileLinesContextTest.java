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
package org.sonar.scanner;


import java.util.List;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.sonar.api.batch.fs.internal.DefaultInputFile;
import org.sonar.api.batch.sensor.internal.SensorStorage;
import org.sonar.api.batch.sensor.measure.internal.DefaultMeasure;
import org.sonar.api.measures.CoreMetrics;


public class DefaultFileLinesContextTest {
    private static final String HITS_METRIC_KEY = "hits";

    private static final String AUTHOR_METRIC_KEY = "author";

    private static final String BRANCHES_METRIC_KEY = "branches";

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private DefaultFileLinesContext fileLineMeasures;

    private SensorStorage sensorStorage;

    private DefaultInputFile file;

    @Test
    public void shouldSave() {
        fileLineMeasures.setIntValue(DefaultFileLinesContextTest.HITS_METRIC_KEY, 1, 2);
        fileLineMeasures.setIntValue(DefaultFileLinesContextTest.HITS_METRIC_KEY, 3, 0);
        fileLineMeasures.save();
        assertThat(fileLineMeasures.toString()).isEqualTo("DefaultFileLinesContext{map={hits={1=2, 3=0}}}");
        ArgumentCaptor<DefaultMeasure> captor = ArgumentCaptor.forClass(DefaultMeasure.class);
        Mockito.verify(sensorStorage).store(captor.capture());
        DefaultMeasure measure = captor.getValue();
        assertThat(measure.inputComponent()).isEqualTo(file);
        assertThat(measure.metric().key()).isEqualTo(DefaultFileLinesContextTest.HITS_METRIC_KEY);
        assertThat(measure.value()).isEqualTo("1=2;3=0");
    }

    @Test
    public void validateLineGreaterThanZero() {
        thrown.expectMessage("Line number should be positive for file src/foo.php.");
        fileLineMeasures.setIntValue(DefaultFileLinesContextTest.HITS_METRIC_KEY, 0, 2);
    }

    @Test
    public void validateLineLowerThanLineCount() {
        thrown.expectMessage("Line 4 is out of range for file src/foo.php. File has 3 lines");
        fileLineMeasures.setIntValue(DefaultFileLinesContextTest.HITS_METRIC_KEY, 4, 2);
    }

    @Test
    public void optimizeValues() {
        fileLineMeasures.setIntValue(CoreMetrics.NCLOC_DATA_KEY, 1, 0);
        fileLineMeasures.setIntValue(CoreMetrics.NCLOC_DATA_KEY, 2, 1);
        fileLineMeasures.setIntValue(CoreMetrics.EXECUTABLE_LINES_DATA_KEY, 1, 0);
        fileLineMeasures.setIntValue(CoreMetrics.EXECUTABLE_LINES_DATA_KEY, 2, 1);
        fileLineMeasures.save();
        ArgumentCaptor<DefaultMeasure> captor = ArgumentCaptor.forClass(DefaultMeasure.class);
        Mockito.verify(sensorStorage, Mockito.times(2)).store(captor.capture());
        List<DefaultMeasure> measures = captor.getAllValues();
        assertThat(measures).extracting(DefaultMeasure::inputComponent, ( m) -> m.metric().key(), DefaultMeasure::value).containsExactlyInAnyOrder(tuple(file, CoreMetrics.NCLOC_DATA_KEY, "2=1"), tuple(file, CoreMetrics.EXECUTABLE_LINES_DATA_KEY, "2=1"));
    }

    @Test
    public void shouldSaveSeveral() {
        fileLineMeasures.setIntValue(DefaultFileLinesContextTest.HITS_METRIC_KEY, 1, 2);
        fileLineMeasures.setIntValue(DefaultFileLinesContextTest.HITS_METRIC_KEY, 3, 4);
        fileLineMeasures.setStringValue(DefaultFileLinesContextTest.AUTHOR_METRIC_KEY, 1, "simon");
        fileLineMeasures.setStringValue(DefaultFileLinesContextTest.AUTHOR_METRIC_KEY, 3, "evgeny");
        fileLineMeasures.save();
        fileLineMeasures.setIntValue(DefaultFileLinesContextTest.BRANCHES_METRIC_KEY, 1, 2);
        fileLineMeasures.setIntValue(DefaultFileLinesContextTest.BRANCHES_METRIC_KEY, 3, 4);
        fileLineMeasures.save();
        ArgumentCaptor<DefaultMeasure> captor = ArgumentCaptor.forClass(DefaultMeasure.class);
        Mockito.verify(sensorStorage, Mockito.times(3)).store(captor.capture());
        List<DefaultMeasure> measures = captor.getAllValues();
        assertThat(measures).extracting(DefaultMeasure::inputComponent, ( m) -> m.metric().key(), DefaultMeasure::value).containsExactlyInAnyOrder(tuple(file, DefaultFileLinesContextTest.HITS_METRIC_KEY, "1=2;3=4"), tuple(file, DefaultFileLinesContextTest.AUTHOR_METRIC_KEY, "1=simon;3=evgeny"), tuple(file, DefaultFileLinesContextTest.BRANCHES_METRIC_KEY, "1=2;3=4"));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void shouldNotModifyAfterSave() {
        fileLineMeasures.setIntValue(DefaultFileLinesContextTest.HITS_METRIC_KEY, 1, 2);
        fileLineMeasures.save();
        fileLineMeasures.setIntValue(DefaultFileLinesContextTest.HITS_METRIC_KEY, 1, 2);
    }
}

