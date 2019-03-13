/**
 * Copyright 2014 TWO SIGMA OPEN SOURCE, LLC
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.twosigma.beakerx.chart.histogram;


import Histogram.DisplayMode;
import HistogramSerializer.LOG;
import HistogramSerializer.NAMES;
import com.twosigma.beakerx.chart.AbstractChart;
import com.twosigma.beakerx.chart.AbstractChartTest;
import com.twosigma.beakerx.chart.serializer.HistogramSerializer;
import java.awt.Color;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import org.junit.Test;


public class HistogramTest extends AbstractChartTest<Histogram> {
    private List<Integer> list1;

    private List<Integer> list2;

    private Histogram histogram;

    @Test
    public void defaultBinCountEquals10() {
        // given
        histogram = new Histogram();
        // then
        assertThat(histogram.getBinCount()).isEqualTo(10);
    }

    @Test
    public void createHistogramByEmptyConstructor_hasDisplayModeIsNotNull() {
        // given
        histogram = createWidget();
        // then
        assertThat(histogram.getDisplayMode()).isNotNull();
    }

    @Test
    public void shouldSendCommMsgWhenBinCountChange() {
        // given
        histogram = createWidget();
        // when
        histogram.setBinCount(11);
        // then
        assertThat(histogram.getBinCount()).isEqualTo(11);
        LinkedHashMap model = getModelUpdate();
        assertThat(model.size()).isEqualTo(1);
        assertThat(model.get(HistogramSerializer.BIN_COUNT)).isEqualTo(11);
    }

    @Test
    public void shouldSendCommMsgWhenNamesChange() {
        // given
        histogram = createWidget();
        // when
        histogram.setNames(Arrays.asList("name123"));
        // then
        assertThat(histogram.getNames()).isEqualTo(Arrays.asList("name123"));
        LinkedHashMap model = getModelUpdate();
        assertThat(model.size()).isEqualTo(1);
        assertThat(model.get(NAMES)).isNotNull();
    }

    @Test
    public void shouldSendCommMsgWhenDisplayModeChange() {
        // given
        histogram = createWidget();
        Histogram.DisplayMode overlap = DisplayMode.OVERLAP;
        // when
        histogram.setDisplayMode(overlap);
        // then
        assertThat(histogram.getDisplayMode()).isEqualTo(overlap);
        LinkedHashMap model = getModelUpdate();
        assertThat(model.size()).isEqualTo(1);
        assertThat(model.get(HistogramSerializer.DISPLAY_MODE)).isNotNull();
    }

    @Test
    public void shouldSendCommMsgWhenCumulativeChange() {
        // given
        histogram = createWidget();
        // when
        histogram.setCumulative(true);
        // then
        assertThat(histogram.getCumulative()).isEqualTo(true);
        LinkedHashMap model = getModelUpdate();
        assertThat(model.size()).isEqualTo(1);
        assertThat(model.get(HistogramSerializer.CUMULATIVE)).isEqualTo(true);
    }

    @Test
    public void shouldSendCommMsgWhenNormedChange() {
        // given
        histogram = createWidget();
        // when
        histogram.setNormed(true);
        // then
        assertThat(histogram.getNormed()).isEqualTo(true);
        LinkedHashMap model = getModelUpdate();
        assertThat(model.size()).isEqualTo(1);
        assertThat(model.get(HistogramSerializer.NORMED)).isEqualTo(true);
    }

    @Test
    public void shouldSendCommMsgWhenLogChange() {
        // given
        histogram = createWidget();
        // when
        histogram.setLog(true);
        // then
        assertThat(histogram.getLog()).isEqualTo(true);
        LinkedHashMap model = getModelUpdate();
        assertThat(model.size()).isEqualTo(1);
        assertThat(model.get(HistogramSerializer.LOG)).isEqualTo(true);
    }

    @Test
    public void setDataWithListOfIntegerListsParam_hasListDataIsNotEmpty() {
        // given
        histogram = createWidget();
        // when
        histogram.setData(Arrays.asList(list1, list2));
        // then
        assertThat(histogram.getListData()).isNotEmpty();
        LinkedHashMap model = getModelUpdate();
        assertThat(model.size()).isEqualTo(1);
        assertThat(model.get(HistogramSerializer.GRAPHICS_LIST)).isNotNull();
    }

    @Test
    public void setDataWithIntegerListParam_hasDataIsNotEmpty() {
        // given
        histogram = createWidget();
        // when
        histogram.setData(list1);
        // then
        assertThat(histogram.getData()).isNotEmpty();
        LinkedHashMap model = getModelUpdate();
        assertThat(model.size()).isEqualTo(1);
        assertThat(model.get(HistogramSerializer.GRAPHICS_LIST)).isNotNull();
    }

    @Test
    public void setColorWithAwtColorParam_colorHasBeakerColorType() {
        // given
        histogram = createWidget();
        // when
        histogram.setColor(Color.GREEN);
        // then
        assertThat(((histogram.getColor()) instanceof com.twosigma.beakerx.chart.Color)).isTrue();
        LinkedHashMap model = getModelUpdate();
        assertThat(model.size()).isEqualTo(1);
        assertThat(model.get(HistogramSerializer.COLOR)).isNotNull();
    }

    @Test
    public void setColorWithAwtColorListParam_hasBeakerColorsIsNotEmpty() {
        // given
        histogram = createWidget();
        // when
        histogram.setColor(Arrays.asList(Color.GREEN, Color.BLUE));
        // then
        assertThat(histogram.getColors()).isNotEmpty();
        LinkedHashMap model = getModelUpdate();
        assertThat(model.size()).isEqualTo(1);
        List<String> actual = ((List<String>) (model.get(HistogramSerializer.COLORS)));
        assertThat(actual.get(0)).startsWith("#");
    }

    @Test(expected = IllegalArgumentException.class)
    public void setColorWithStringParam_throwIllegalArgumentException() {
        // given
        histogram = createWidget();
        // when
        histogram.setColor(Arrays.asList("blue"));
    }

    @Test
    public void shouldSendCommMsgWhenLogYChange() {
        // given
        AbstractChart chart = createWidget();
        // when
        chart.setLogY(true);
        // then
        assertThat(chart.getLogY()).isEqualTo(true);
        LinkedHashMap model = getModelUpdate();
        assertThat(model.size()).isEqualTo(1);
        assertThat(model.get(LOG)).isEqualTo(true);
    }
}

