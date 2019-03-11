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
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.sonar.api.batch.fs.internal.DefaultInputFile;
import org.sonar.api.batch.sensor.internal.SensorStorage;
import org.sonar.api.batch.sensor.measure.internal.DefaultMeasure;
import org.sonar.api.measures.CoreMetrics;
import org.sonar.scanner.scan.measure.MeasureCache;


public class DefaultFileLinesContextTest {
    private static final String HITS_METRIC_KEY = "hits";

    private static final String AUTHOR_METRIC_KEY = "author";

    private static final String BRANCHES_METRIC_KEY = "branches";

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private DefaultFileLinesContext fileLineMeasures;

    private MeasureCache measureCache;

    private SensorStorage sensorStorage;

    private DefaultInputFile file;

    @Test
    public void shouldSave() {
        fileLineMeasures.setIntValue(DefaultFileLinesContextTest.HITS_METRIC_KEY, 1, 2);
        fileLineMeasures.setIntValue(DefaultFileLinesContextTest.HITS_METRIC_KEY, 3, 0);
        fileLineMeasures.save();
        Assert.assertThat(fileLineMeasures.toString()).isEqualTo("DefaultFileLinesContext{map={hits={1=2, 3=0}}}");
        ArgumentCaptor<DefaultMeasure> captor = ArgumentCaptor.forClass(DefaultMeasure.class);
        Mockito.verify(sensorStorage).store(captor.capture());
        DefaultMeasure measure = captor.getValue();
        Assert.assertThat(measure.inputComponent()).isEqualTo(file);
        Assert.assertThat(measure.metric().key()).isEqualTo(DefaultFileLinesContextTest.HITS_METRIC_KEY);
        Assert.assertThat(measure.value()).isEqualTo("1=2;3=0");
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
        Assert.assertThat(measures).extracting(DefaultMeasure::inputComponent, ( m) -> m.metric().key(), DefaultMeasure::value).containsExactlyInAnyOrder(tuple(file, CoreMetrics.NCLOC_DATA_KEY, "2=1"), tuple(file, CoreMetrics.EXECUTABLE_LINES_DATA_KEY, "2=1"));
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
        Assert.assertThat(measures).extracting(DefaultMeasure::inputComponent, ( m) -> m.metric().key(), DefaultMeasure::value).containsExactlyInAnyOrder(tuple(file, DefaultFileLinesContextTest.HITS_METRIC_KEY, "1=2;3=4"), tuple(file, DefaultFileLinesContextTest.AUTHOR_METRIC_KEY, "1=simon;3=evgeny"), tuple(file, DefaultFileLinesContextTest.BRANCHES_METRIC_KEY, "1=2;3=4"));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void shouldNotModifyAfterSave() {
        fileLineMeasures.setIntValue(DefaultFileLinesContextTest.HITS_METRIC_KEY, 1, 2);
        fileLineMeasures.save();
        fileLineMeasures.setIntValue(DefaultFileLinesContextTest.HITS_METRIC_KEY, 1, 2);
    }

    @Test
    public void shouldLoadIntValues() {
        Mockito.when(measureCache.byMetric("foo:src/foo.php", DefaultFileLinesContextTest.HITS_METRIC_KEY)).thenReturn(new DefaultMeasure().withValue("1=2;3=4"));
        Assert.assertThat(fileLineMeasures.getIntValue(DefaultFileLinesContextTest.HITS_METRIC_KEY, 1), CoreMatchers.is(2));
        Assert.assertThat(fileLineMeasures.getIntValue(DefaultFileLinesContextTest.HITS_METRIC_KEY, 3), CoreMatchers.is(4));
        Assert.assertThat("no measure on line", fileLineMeasures.getIntValue(DefaultFileLinesContextTest.HITS_METRIC_KEY, 2), CoreMatchers.nullValue());
    }

    @Test
    public void shouldLoadStringValues() {
        Mockito.when(measureCache.byMetric("foo:src/foo.php", DefaultFileLinesContextTest.AUTHOR_METRIC_KEY)).thenReturn(new DefaultMeasure().withValue("1=simon;3=evgeny"));
        Assert.assertThat(fileLineMeasures.getStringValue(DefaultFileLinesContextTest.AUTHOR_METRIC_KEY, 1), CoreMatchers.is("simon"));
        Assert.assertThat(fileLineMeasures.getStringValue(DefaultFileLinesContextTest.AUTHOR_METRIC_KEY, 3), CoreMatchers.is("evgeny"));
        Assert.assertThat("no measure on line", fileLineMeasures.getStringValue(DefaultFileLinesContextTest.AUTHOR_METRIC_KEY, 2), CoreMatchers.nullValue());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void shouldNotModifyAfterLoad() {
        Mockito.when(measureCache.byMetric("foo:src/foo.php", DefaultFileLinesContextTest.AUTHOR_METRIC_KEY)).thenReturn(new DefaultMeasure().withValue("1=simon;3=evgeny"));
        fileLineMeasures.getStringValue(DefaultFileLinesContextTest.AUTHOR_METRIC_KEY, 1);
        fileLineMeasures.setStringValue(DefaultFileLinesContextTest.AUTHOR_METRIC_KEY, 1, "evgeny");
    }

    @Test
    public void shouldNotFailIfNoMeasureInIndex() {
        Assert.assertThat(fileLineMeasures.getIntValue(DefaultFileLinesContextTest.HITS_METRIC_KEY, 1), CoreMatchers.nullValue());
        Assert.assertThat(fileLineMeasures.getStringValue(DefaultFileLinesContextTest.AUTHOR_METRIC_KEY, 1), CoreMatchers.nullValue());
    }
}

