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
package org.sonar.scanner.scan.measure;


import CoreMetrics.COVERAGE_LINE_HITS_DATA;
import CoreMetrics.COVERAGE_LINE_HITS_DATA_KEY;
import CoreMetrics.NCLOC;
import CoreMetrics.NCLOC_DATA_KEY;
import CoreMetrics.NCLOC_KEY;
import java.util.Iterator;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.sonar.api.batch.measure.MetricFinder;
import org.sonar.api.batch.sensor.measure.internal.DefaultMeasure;
import org.sonar.scanner.index.AbstractCachesTest;
import org.sonar.scanner.storage.Storage.Entry;


public class MeasureCacheTest extends AbstractCachesTest {
    private static final String COMPONENT_KEY = "struts";

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private MetricFinder metricFinder;

    private MeasureCache measureCache;

    @Test
    public void should_add_measure() {
        assertThat(measureCache.entries()).hasSize(0);
        assertThat(measureCache.byComponentKey(MeasureCacheTest.COMPONENT_KEY)).hasSize(0);
        DefaultMeasure<?> m = new DefaultMeasure().forMetric(NCLOC).withValue(1.0);
        measureCache.put(MeasureCacheTest.COMPONENT_KEY, NCLOC_KEY, m);
        assertThat(measureCache.contains(MeasureCacheTest.COMPONENT_KEY, NCLOC_KEY)).isTrue();
        assertThat(measureCache.entries()).hasSize(1);
        Iterator<Entry<DefaultMeasure<?>>> iterator = measureCache.entries().iterator();
        iterator.hasNext();
        Entry<DefaultMeasure<?>> next = iterator.next();
        assertThat(next.value()).isEqualTo(m);
        assertThat(next.key()[0]).isEqualTo(MeasureCacheTest.COMPONENT_KEY);
        assertThat(measureCache.byComponentKey(MeasureCacheTest.COMPONENT_KEY)).hasSize(1);
        assertThat(measureCache.byComponentKey(MeasureCacheTest.COMPONENT_KEY).iterator().next()).isEqualTo(m);
    }

    /**
     * This test fails with stock PersisitIt.
     */
    @Test
    public void should_add_measure_with_too_big_data_for_persistit_pre_patch() {
        assertThat(measureCache.entries()).hasSize(0);
        assertThat(measureCache.byComponentKey(MeasureCacheTest.COMPONENT_KEY)).hasSize(0);
        StringBuilder data = new StringBuilder(4500000);
        for (int i = 0; i < 4500000; i++) {
            data.append('a');
        }
        DefaultMeasure<?> m = new DefaultMeasure().forMetric(COVERAGE_LINE_HITS_DATA).withValue(data.toString());
        measureCache.put(MeasureCacheTest.COMPONENT_KEY, COVERAGE_LINE_HITS_DATA_KEY, m);
        assertThat(measureCache.contains(MeasureCacheTest.COMPONENT_KEY, COVERAGE_LINE_HITS_DATA_KEY)).isTrue();
        assertThat(measureCache.entries()).hasSize(1);
        Iterator<Entry<DefaultMeasure<?>>> iterator = measureCache.entries().iterator();
        iterator.hasNext();
        Entry<DefaultMeasure<?>> next = iterator.next();
        assertThat(next.value()).isEqualTo(m);
        assertThat(next.key()[0]).isEqualTo(MeasureCacheTest.COMPONENT_KEY);
        assertThat(measureCache.byComponentKey(MeasureCacheTest.COMPONENT_KEY)).hasSize(1);
        assertThat(measureCache.byComponentKey(MeasureCacheTest.COMPONENT_KEY).iterator().next()).isEqualTo(m);
    }

    @Test
    public void should_add_measure_with_too_big_data_for_persistit() {
        assertThat(measureCache.entries()).hasSize(0);
        assertThat(measureCache.byComponentKey(MeasureCacheTest.COMPONENT_KEY)).hasSize(0);
        // Limit is 64Mo
        StringBuilder data = new StringBuilder((((64 * 1024) * 1024) + 1));
        for (int i = 0; i < (((64 * 1024) * 1024) + 1); i++) {
            data.append('a');
        }
        DefaultMeasure<?> m = new DefaultMeasure().forMetric(COVERAGE_LINE_HITS_DATA).withValue(data.toString());
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage("Fail to put element in the storage 'measures'");
        measureCache.put(MeasureCacheTest.COMPONENT_KEY, COVERAGE_LINE_HITS_DATA_KEY, m);
    }

    @Test
    public void should_get_measures() {
        String projectKey = "struts";
        String dirKey = "struts:foo/bar";
        String file1Key = "struts:foo/bar/File1.txt";
        String file2Key = "struts:foo/bar/File2.txt";
        assertThat(measureCache.entries()).hasSize(0);
        assertThat(measureCache.byComponentKey(projectKey)).hasSize(0);
        assertThat(measureCache.byComponentKey(dirKey)).hasSize(0);
        DefaultMeasure<?> mFile1 = new DefaultMeasure().forMetric(NCLOC).withValue(1.0);
        measureCache.put(file1Key, NCLOC_DATA_KEY, mFile1);
        DefaultMeasure<?> mFile2 = new DefaultMeasure().forMetric(NCLOC).withValue(3.0);
        measureCache.put(file2Key, NCLOC_DATA_KEY, mFile2);
        assertThat(measureCache.entries()).hasSize(2);
        assertThat(measureCache.byComponentKey(projectKey)).hasSize(0);
        assertThat(measureCache.byComponentKey(dirKey)).hasSize(0);
        DefaultMeasure<?> mDir = new DefaultMeasure().forMetric(NCLOC).withValue(4.0);
        measureCache.put(dirKey, NCLOC_DATA_KEY, mDir);
        assertThat(measureCache.entries()).hasSize(3);
        assertThat(measureCache.byComponentKey(projectKey)).hasSize(0);
        assertThat(measureCache.byComponentKey(dirKey)).hasSize(1);
        assertThat(measureCache.byComponentKey(dirKey).iterator().next()).isEqualTo(mDir);
        DefaultMeasure<?> mProj = new DefaultMeasure().forMetric(NCLOC).withValue(4.0);
        measureCache.put(projectKey, NCLOC_DATA_KEY, mProj);
        assertThat(measureCache.entries()).hasSize(4);
        assertThat(measureCache.byComponentKey(projectKey)).hasSize(1);
        assertThat(measureCache.byComponentKey(projectKey).iterator().next()).isEqualTo(mProj);
        assertThat(measureCache.byComponentKey(dirKey)).hasSize(1);
        assertThat(measureCache.byComponentKey(dirKey).iterator().next()).isEqualTo(mDir);
    }
}

