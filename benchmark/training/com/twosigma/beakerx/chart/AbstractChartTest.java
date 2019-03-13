/**
 * Copyright 2017 TWO SIGMA OPEN SOURCE, LLC
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


import YAxisSerializer.TYPE;
import com.twosigma.beakerx.chart.serializer.AbstractChartSerializer;
import com.twosigma.beakerx.chart.xychart.plotitem.Crosshair;
import com.twosigma.beakerx.chart.xychart.plotitem.YAxis;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import org.junit.Test;


public abstract class AbstractChartTest<T extends AbstractChart> extends ChartTest<AbstractChart> {
    @Test
    public void shouldSendCommMsgWhenAddYAxisByLeftShift() throws Exception {
        // given
        AbstractChart abstractChart = createWidget();
        YAxis yAxis = new YAxis();
        // when
        abstractChart.leftShift(yAxis);
        // then
        assertThat(abstractChart.getYAxes()).contains(yAxis);
        List valueAsArray = getValueAsArray(AbstractChartSerializer.RANGE_AXES);
        Map actual = ((Map) (valueAsArray.get(0)));
        assertThat(actual.get(TYPE)).isEqualTo(YAxis.class.getSimpleName());
        Map actual1 = ((Map) (valueAsArray.get(1)));
        assertThat(actual1.get(TYPE)).isEqualTo(YAxis.class.getSimpleName());
    }

    @Test
    public void addWithList_hasYAxesNotEmpty() {
        // given
        AbstractChart chart = createWidget();
        YAxis yAxis = new YAxis("test");
        // when
        chart.add(Arrays.asList(yAxis));
        // then
        assertThat(chart.getYAxes()).isNotEmpty();
        assertThat(chart.getYAxes().get(1).getLabel()).isEqualTo("test");
        assertThat(getValueAsArray(AbstractChartSerializer.RANGE_AXES).size()).isGreaterThan(1);
    }

    @Test
    public void setOmitCheckboxesByTrue_OmitCheckboxesIsTrue() {
        // given
        AbstractChart chart = createWidget();
        // when
        chart.setOmitCheckboxes(true);
        // then
        assertThat(chart.getOmitCheckboxes()).isTrue();
        LinkedHashMap model = getModelUpdate();
        AbstractChartTest.assertThat(model.size()).isEqualTo(1);
        assertThat(model.get(AbstractChartSerializer.OMIT_CHECKBOXES)).isEqualTo(true);
    }

    @Test
    public void setYAutoRangeIncludesZeroByTrue_YAutoRangeIncludesZeroIsTrue() {
        // given
        AbstractChart chart = createWidget();
        // when
        chart.setYAutoRangeIncludesZero(true);
        // then
        verifyYAutoRangeIncludesZero(chart);
    }

    @Test
    public void setyAutoRangeIncludesZeroByTrue_YAutoRangeIncludesZeroIsTrue() {
        // given
        AbstractChart chart = createWidget();
        // when
        chart.setyAutoRangeIncludesZero(true);
        // then
        verifyYAutoRangeIncludesZero(chart);
    }

    @Test
    public void setYBoundWithList_hasYLoweAndUpperBounds() {
        // given
        AbstractChart chart = createWidget();
        // when
        chart.setYBound(Arrays.asList(1.0, 10.0));
        // then
        assertThat(chart.getYLowerBound()).isEqualTo(1.0);
        assertThat(chart.getYUpperBound()).isEqualTo(10.0);
        LinkedHashMap model = getModelUpdate();
        AbstractChartTest.assertThat(model.size()).isEqualTo(2);
        assertThat(model.get(AbstractChartSerializer.Y_LOWER_BOUND)).isEqualTo(1.0);
        assertThat(model.get(AbstractChartSerializer.Y_UPPER_BOUND)).isEqualTo(10.0);
    }

    @Test
    public void setYBoundWithTwoParams_hasYLoweAndUpperBounds() {
        // given
        AbstractChart chart = createWidget();
        // when
        chart.setYBound(1.0, 10.0);
        // then
        assertThat(chart.getYLowerBound()).isEqualTo(1.0);
        assertThat(chart.getYUpperBound()).isEqualTo(10.0);
        LinkedHashMap model = getModelUpdate();
        AbstractChartTest.assertThat(model.size()).isEqualTo(2);
        assertThat(model.get(AbstractChartSerializer.Y_LOWER_BOUND)).isEqualTo(1.0);
        assertThat(model.get(AbstractChartSerializer.Y_UPPER_BOUND)).isEqualTo(10.0);
    }

    @Test
    public void setyBoundWithList_hasYLoweAndUpperBounds() {
        // given
        AbstractChart chart = createWidget();
        // when
        chart.setyBound(Arrays.asList(1.0, 10.0));
        // then
        assertThat(chart.getYLowerBound()).isEqualTo(1.0);
        assertThat(chart.getYUpperBound()).isEqualTo(10.0);
        LinkedHashMap model = getModelUpdate();
        AbstractChartTest.assertThat(model.size()).isEqualTo(2);
        assertThat(model.get(AbstractChartSerializer.Y_LOWER_BOUND)).isEqualTo(1.0);
        assertThat(model.get(AbstractChartSerializer.Y_UPPER_BOUND)).isEqualTo(10.0);
    }

    @Test
    public void setYLogBaseWithDoubleParam_hasYLogBase() {
        // given
        AbstractChart chart = createWidget();
        // when
        chart.setYLogBase(5.0);
        // then
        assertThat(chart.getYLogBase()).isEqualTo(5.0);
    }

    @Test
    public void setXLabel_hasXLabel() {
        // given
        AbstractChart chart = createWidget();
        // when
        chart.setXLabel("testX");
        // then
        assertThat(chart.getXLabel()).isEqualTo("testX");
        LinkedHashMap model = getModelUpdate();
        AbstractChartTest.assertThat(model.size()).isEqualTo(1);
        assertThat(model.get(AbstractChartSerializer.DOMAIN_AXIS_LABEL)).isEqualTo("testX");
    }

    @Test
    public void setxLabel_hasXLabel() {
        // given
        AbstractChart chart = createWidget();
        // when
        chart.setxLabel("test_x");
        // then
        assertThat(chart.getXLabel()).isEqualTo("test_x");
        LinkedHashMap model = getModelUpdate();
        AbstractChartTest.assertThat(model.size()).isEqualTo(1);
        assertThat(model.get(AbstractChartSerializer.DOMAIN_AXIS_LABEL)).isEqualTo("test_x");
    }

    @Test
    public void setyLabel_hasYLabel() {
        // given
        AbstractChart chart = createWidget();
        // when
        chart.setyLabel("test_y");
        // then
        assertThat(chart.getYLabel()).isEqualTo("test_y");
        LinkedHashMap model = getModelUpdate();
        AbstractChartTest.assertThat(model.size()).isEqualTo(1);
        assertThat(model.get(AbstractChartSerializer.Y_LABEL)).isEqualTo("test_y");
    }

    @Test
    public void setYLabel_hasYLabel() {
        // given
        AbstractChart chart = createWidget();
        // when
        chart.setYLabel("testY");
        // then
        assertThat(chart.getYLabel()).isEqualTo("testY");
        LinkedHashMap model = getModelUpdate();
        AbstractChartTest.assertThat(model.size()).isEqualTo(1);
        assertThat(model.get(AbstractChartSerializer.Y_LABEL)).isEqualTo("testY");
    }

    @Test
    public void setXLowerMarginWithDoubleParam_hasXLowerMargin() {
        // given
        AbstractChart chart = createWidget();
        // when
        chart.setXLowerMargin(3.0);
        // then
        assertThat(chart.getXLowerMargin()).isEqualTo(3.0);
        LinkedHashMap model = getModelUpdate();
        AbstractChartTest.assertThat(model.size()).isEqualTo(1);
        assertThat(model.get(AbstractChartSerializer.X_LOWER_MARGIN)).isEqualTo(3.0);
    }

    @Test
    public void setxLowerMarginWithDoubleParam_hasXLowerMargin() {
        // given
        AbstractChart chart = createWidget();
        // when
        chart.setxLowerMargin(3.5);
        // then
        assertThat(chart.getXLowerMargin()).isEqualTo(3.5);
        LinkedHashMap model = getModelUpdate();
        AbstractChartTest.assertThat(model.size()).isEqualTo(1);
        assertThat(model.get(AbstractChartSerializer.X_LOWER_MARGIN)).isEqualTo(3.5);
    }

    @Test
    public void setXUpperMarginWithDoubleParam_hasXUpperMargin() {
        // given
        AbstractChart chart = createWidget();
        // when
        chart.setXUpperMargin(7.0);
        // then
        assertThat(chart.getXUpperMargin()).isEqualTo(7.0);
        LinkedHashMap model = getModelUpdate();
        AbstractChartTest.assertThat(model.size()).isEqualTo(1);
        assertThat(model.get(AbstractChartSerializer.X_UPPER_MARGIN)).isEqualTo(7.0);
    }

    @Test
    public void setxUpperMarginWithDoubleParam_hasXUpperMargin() {
        // given
        AbstractChart chart = createWidget();
        // when
        chart.setxUpperMargin(7.5);
        // then
        assertThat(chart.getXUpperMargin()).isEqualTo(7.5);
        LinkedHashMap model = getModelUpdate();
        AbstractChartTest.assertThat(model.size()).isEqualTo(1);
        assertThat(model.get(AbstractChartSerializer.X_UPPER_MARGIN)).isEqualTo(7.5);
    }

    @Test
    public void setyAutoRangeByTrue_YAutoRangeIsTrue() {
        // given
        AbstractChart chart = createWidget();
        // when
        chart.setyAutoRange(true);
        // then
        assertThat(chart.getYAutoRange()).isTrue();
        LinkedHashMap model = getModelUpdate();
        AbstractChartTest.assertThat(model.size()).isEqualTo(1);
        assertThat(model.get(AbstractChartSerializer.Y_AUTO_RANGE)).isEqualTo(true);
    }

    @Test
    public void setyAutoRangeByFalse_YAutoRangeIsFalse() {
        // given
        AbstractChart chart = createWidget();
        // when
        chart.setyAutoRange(false);
        // then
        assertThat(chart.getYAutoRange()).isFalse();
        LinkedHashMap model = getModelUpdate();
        AbstractChartTest.assertThat(model.size()).isEqualTo(1);
        assertThat(model.get(AbstractChartSerializer.Y_AUTO_RANGE)).isEqualTo(false);
    }

    @Test
    public void setyLogBaseWithDoubleParam_hasYLogBase() {
        // given
        AbstractChart chart = createWidget();
        // when
        chart.setyLogBase(5.0);
        // then
        assertThat(chart.getYLogBase()).isEqualTo(5.0);
    }

    @Test
    public void setyLowerMarginWithDoubleParam_hasYLowerMargin() {
        // given
        AbstractChart chart = createWidget();
        // when
        chart.setyLowerMargin(0.3);
        // then
        assertThat(chart.getYLowerMargin()).isEqualTo(0.3);
        LinkedHashMap model = getModelUpdate();
        AbstractChartTest.assertThat(model.size()).isEqualTo(1);
        assertThat(model.get(AbstractChartSerializer.Y_LOWER_MARGIN)).isEqualTo(0.3);
    }

    @Test
    public void setyUpperMarginWithDoubleParam_hasYUpperMargin() {
        // given
        AbstractChart chart = createWidget();
        // when
        chart.setyUpperMargin(0.7);
        // then
        assertThat(chart.getYUpperMargin()).isEqualTo(0.7);
        LinkedHashMap model = getModelUpdate();
        AbstractChartTest.assertThat(model.size()).isEqualTo(1);
        assertThat(model.get(AbstractChartSerializer.Y_UPPER_MARGIN)).isEqualTo(0.7);
    }

    @Test
    public void leftShiftWithYAxisParam_shouldAddYAxis() {
        // given
        AbstractChart chart = createWidget();
        // when
        chart.leftShift(new YAxis("test yAxis"));
        // then
        assertThat(chart.getYAxes().get(1).getLabel()).isEqualTo("test yAxis");
    }

    @Test
    public void leftShiftWithListParam_shouldAddYAxes() {
        // given
        AbstractChart chart = createWidget();
        // when
        chart.leftShift(Arrays.asList(new YAxis("axis1"), new YAxis("axis2")));
        // then
        assertThat(chart.getYAxes().get(1).getLabel()).isEqualTo("axis1");
    }

    @Test
    public void setCrosshair_hasCrosshair() {
        // given
        AbstractChart chart = createWidget();
        Crosshair crosshair = new Crosshair();
        // when
        chart.setCrosshair(crosshair);
        // then
        assertThat(chart.getCrosshair()).isNotNull();
        LinkedHashMap model = getModelUpdate();
        AbstractChartTest.assertThat(model.size()).isEqualTo(1);
        Map actual = ((Map) (model.get(AbstractChartSerializer.CROSSHAIR)));
        assertThat(actual.get(CrosshairSerializer.TYPE)).isEqualTo(Crosshair.class.getSimpleName());
    }

    @Test
    public void shouldSendCommMsgWhenTimezoneChange() {
        // given
        AbstractChart chart = createWidget();
        TimeZone aDefault = TimeZone.getDefault();
        // when
        chart.setTimeZone(aDefault);
        // then
        assertThat(chart.getTimeZone()).isEqualTo(aDefault);
        LinkedHashMap model = getModelUpdate();
        AbstractChartTest.assertThat(model.size()).isEqualTo(1);
        assertThat(model.get(AbstractChartSerializer.TIMEZONE)).isEqualTo(aDefault.getID());
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
        AbstractChartTest.assertThat(model.size()).isEqualTo(1);
        assertThat(model.get(AbstractChartSerializer.LOG_Y)).isEqualTo(true);
    }
}

