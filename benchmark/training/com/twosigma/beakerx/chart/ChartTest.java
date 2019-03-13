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
package com.twosigma.beakerx.chart;


import BeakerxPlot.MODEL_NAME_VALUE;
import BeakerxPlot.VIEW_NAME_VALUE;
import LegendLayout.HORIZONTAL;
import LegendPositionSerializer.POSITION;
import LegendPositionSerializer.TYPE;
import com.twosigma.beakerx.KernelTest;
import com.twosigma.beakerx.chart.actions.CategoryGraphicsActionObject;
import com.twosigma.beakerx.chart.legend.LegendLayout;
import com.twosigma.beakerx.chart.legend.LegendPosition;
import com.twosigma.beakerx.chart.serializer.ChartSerializer;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.junit.Test;


public abstract class ChartTest<T extends Chart> {
    protected KernelTest kernel;

    @Test
    public void shouldSendCommMsgWhenLegendPositionChange() throws Exception {
        // given
        Chart chart = createWidget();
        LegendPosition bottom = LegendPosition.BOTTOM;
        // when
        chart.setLegendPosition(bottom);
        // then
        assertThat(chart.getLegendPosition()).isEqualTo(bottom);
        LinkedHashMap model = getModelUpdate();
        ChartTest.assertThat(model.size()).isEqualTo(1);
        Map actual = ((Map) (model.get(ChartSerializer.LEGEND_POSITION)));
        assertThat(actual.get(TYPE)).isEqualTo(LegendPosition.class.getSimpleName());
        assertThat(actual.get(POSITION)).isEqualTo(bottom.getPosition().toString());
    }

    @Test
    public void shouldSendCommMsgWhenLegendLayoutChange() throws Exception {
        // given
        Chart chart = createWidget();
        LegendLayout horizontal = LegendLayout.HORIZONTAL;
        // when
        chart.setLegendLayout(horizontal);
        // then
        assertThat(chart.getLegendLayout()).isEqualTo(horizontal);
        LinkedHashMap model = getModelUpdate();
        ChartTest.assertThat(model.size()).isEqualTo(1);
        String actual = ((String) (model.get(ChartSerializer.LEGEND_LAYOUT)));
        ChartTest.assertThat(actual).isEqualTo(HORIZONTAL.toString());
    }

    @Test
    public void shouldSendCommMsgWhenTitleChange() throws Exception {
        // given
        Chart chart = createWidget();
        String title = "title1";
        // when
        chart.setTitle(title);
        // then
        LinkedHashMap model = getModelUpdate();
        ChartTest.assertThat(model.size()).isEqualTo(1);
        assertThat(model.get(ChartSerializer.CHART_TITLE)).isEqualTo(title);
    }

    @Test
    public void createChartByEmptyConstructor_ChartHasInitHeightWidth() {
        // given
        Chart chart = createWidget();
        // when
        // then
        assertThat(chart.getInitHeight()).isGreaterThan(0);
        assertThat(chart.getInitWidth()).isGreaterThan(0);
    }

    @Test
    public void setCustomStyles_hasCustomStyles() {
        // given
        Chart chart = createWidget();
        List<String> customStyle = Arrays.asList("style1", "style2");
        // when
        chart.setCustomStyles(customStyle);
        // then
        assertThat(chart.getCustomStyles()).isNotEmpty();
        LinkedHashMap model = getModelUpdate();
        ChartTest.assertThat(model.size()).isEqualTo(1);
        List actual = ((List) (model.get(ChartSerializer.CUSTOM_STYLES)));
        ChartTest.assertThat(actual).isEqualTo(customStyle);
    }

    @Test
    public void setGridLineStyle_hasGridLineStyle() {
        // given
        Chart chart = createWidget();
        String grid_style = "grid_style";
        // when
        chart.setGridLineStyle(grid_style);
        // then
        assertThat(chart.getGridLineStyle()).isEqualTo(grid_style);
        assertThat(chart.getElementStyles().get(Chart.PLOT_GRIDLINE)).isEqualTo(grid_style);
        Map actual = getElementStyles();
        assertThat(actual.get(Chart.PLOT_GRIDLINE)).isEqualTo(grid_style);
    }

    @Test
    public void setInitHeight_hasInitHeight() {
        // given
        Chart chart = createWidget();
        // when
        chart.setInitHeight(5);
        // then
        assertThat(chart.getInitHeight()).isEqualTo(5);
        LinkedHashMap model = getModelUpdate();
        ChartTest.assertThat(model.size()).isEqualTo(1);
        assertThat(model.get(ChartSerializer.INIT_HEIGHT)).isEqualTo(5);
    }

    @Test
    public void setInitWidth_hasInitWidth() {
        // given
        Chart chart = createWidget();
        // when
        chart.setInitWidth(10);
        // then
        assertThat(chart.getInitWidth()).isEqualTo(10);
        LinkedHashMap model = getModelUpdate();
        ChartTest.assertThat(model.size()).isEqualTo(1);
        assertThat(model.get(ChartSerializer.INIT_WIDTH)).isEqualTo(10);
    }

    @Test
    public void setLabelYStyle_hasLabelYStyle() {
        // given
        Chart chart = createWidget();
        String labely_style = "labely_style";
        // when
        chart.setLabelYStyle(labely_style);
        // then
        assertThat(chart.getLabelYStyle()).isEqualTo(labely_style);
        assertThat(chart.getElementStyles().get(Chart.PLOT_LABEL_Y)).isEqualTo(labely_style);
        Map actual = getElementStyles();
        assertThat(actual.get(Chart.PLOT_LABEL_Y)).isEqualTo(labely_style);
    }

    @Test
    public void setLabelXStyle_hasLabelXStyle() {
        // given
        Chart chart = createWidget();
        String labelx_style = "labelx_style";
        // when
        chart.setLabelXStyle(labelx_style);
        // then
        assertThat(chart.getLabelXStyle()).isEqualTo(labelx_style);
        assertThat(chart.getElementStyles().get(Chart.PLOT_LABEL_X)).isEqualTo(labelx_style);
        Map actual = getElementStyles();
        assertThat(actual.get(Chart.PLOT_LABEL_X)).isEqualTo(labelx_style);
    }

    @Test
    public void setLabelStyle_hasLabelStyle() {
        // given
        Chart chart = createWidget();
        String label_style = "label_style";
        // when
        chart.setLabelStyle(label_style);
        // then
        assertThat(chart.getLabelStyle()).isEqualTo(label_style);
        assertThat(chart.getElementStyles().get(Chart.PLOT_LABEL)).isEqualTo(label_style);
        Map actual = getElementStyles();
        assertThat(actual.get(Chart.PLOT_LABEL)).isEqualTo(label_style);
    }

    @Test
    public void setTitleStyle_hasTitleStyle() {
        // given
        Chart chart = createWidget();
        String style = "style";
        // when
        chart.setTitleStyle(style);
        // then
        assertThat(chart.getTitleStyle()).isEqualTo(style);
        assertThat(chart.getElementStyles().get(Chart.PLOT_TITLE)).isEqualTo(style);
        Map actual = getElementStyles();
        assertThat(actual.get(Chart.PLOT_TITLE)).isEqualTo(style);
    }

    @Test
    public void setDetails_hasDetails() {
        // given
        Chart chart = createWidget();
        CategoryGraphicsActionObject aObject = new CategoryGraphicsActionObject();
        // when
        chart.setDetails(aObject);
        // then
        assertThat(chart.getDetails()).isEqualTo(aObject);
    }

    @Test
    public void defaultChart_hasModelAndViewNameValues() {
        // given
        Chart chart = createWidget();
        // when
        // then
        assertThat(chart.getModelNameValue()).isEqualTo(MODEL_NAME_VALUE);
        assertThat(chart.getViewNameValue()).isEqualTo(VIEW_NAME_VALUE);
    }
}

