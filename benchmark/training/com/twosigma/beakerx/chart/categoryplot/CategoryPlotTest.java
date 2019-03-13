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
package com.twosigma.beakerx.chart.categoryplot;


import CategoryPlotSerializer.GRAPHICS_LIST;
import PlotOrientationType.HORIZONTAL;
import PlotOrientationType.VERTICAL;
import com.twosigma.beakerx.chart.AbstractChartTest;
import com.twosigma.beakerx.chart.categoryplot.plotitem.CategoryBars;
import com.twosigma.beakerx.chart.categoryplot.plotitem.CategoryGraphics;
import com.twosigma.beakerx.chart.serializer.CategoryPlotSerializer;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.Test;


public class CategoryPlotTest extends AbstractChartTest<CategoryPlot> {
    Integer[] array1;

    Integer[] array2;

    CategoryGraphics categoryBars;

    CategoryGraphics categoryLines;

    CategoryGraphics categoryPoints;

    CategoryGraphics categoryStems;

    @Test
    public void shouldSendCommMsgWhenCategoryNamesLabelAngleChange() throws Exception {
        // given
        CategoryPlot plot = createWidget();
        // when
        plot.setCategoryNamesLabelAngle(22.2);
        // then
        assertThat(plot.getCategoryNamesLabelAngle()).isEqualTo(22.2);
        LinkedHashMap model = getModelUpdate();
        assertThat(model.size()).isEqualTo(1);
        assertThat(model.get(CategoryPlotSerializer.CATEGORY_NAMES_LABEL_ANGLE)).isEqualTo(22.2);
    }

    @Test
    public void shouldSendCommMsgWhenCategoryMarginChange() throws Exception {
        // given
        CategoryPlot plot = createWidget();
        // when
        plot.setCategoryMargin(11.1);
        // then
        assertThat(plot.getCategoryMargin()).isEqualTo(11.1);
        LinkedHashMap model = getModelUpdate();
        assertThat(model.size()).isEqualTo(1);
        assertThat(model.get(CategoryPlotSerializer.CATEGORY_MARGIN)).isEqualTo(11.1);
    }

    @Test
    public void shouldSendCommMsgWhenOrientationChange() throws Exception {
        // given
        CategoryPlot plot = createWidget();
        // when
        plot.setOrientation(HORIZONTAL);
        // then
        assertThat(plot.getOrientation()).isEqualTo(HORIZONTAL);
        LinkedHashMap model = getModelUpdate();
        assertThat(model.size()).isEqualTo(1);
        assertThat(model.get(CategoryPlotSerializer.ORIENTATION)).isEqualTo(HORIZONTAL.toString());
    }

    @Test
    public void shouldSendCommMsgWhenAddCategoryNamesByLeftShift() throws Exception {
        // given
        CategoryPlot plot = createWidget();
        List<String> names = Arrays.asList("name1");
        // when
        plot.setCategoryNames(names);
        // then
        assertThat(plot.getCategoryNames()).isEqualTo(names);
        List valueAsArray = getValueAsArray(CategoryPlotSerializer.CATEGORY_NAMES);
        assertThat(valueAsArray).isNotEmpty();
    }

    @Test
    public void shouldSendCommMsgWhenAddCategoryBarsByLeftShift() throws Exception {
        // given
        CategoryPlot plot = createWidget();
        CategoryBars graphics = new CategoryBars();
        // when
        plot.leftShift(graphics);
        // then
        assertThat(plot.getGraphics().get(0)).isEqualTo(graphics);
        List valueAsArray = getValueAsArray(GRAPHICS_LIST);
        assertThat(valueAsArray).isNotEmpty();
    }

    @Test
    public void createCategoryPlotByEmptyConstructor_hasVerticalOrientation() {
        // when
        CategoryPlot categoryPlot = new CategoryPlot();
        // then
        Assertions.assertThat(categoryPlot.getOrientation()).isEqualTo(VERTICAL);
    }

    @Test
    public void addWithListOfCategoryBarsPointsAndStemsParam_hasCategoryGraphicsListSizeIsThree() {
        // when
        CategoryPlot categoryPlot = new CategoryPlot();
        categoryPlot.add(Arrays.asList(categoryBars, categoryPoints, categoryStems));
        // then
        Assertions.assertThat(categoryPlot.getGraphics().size()).isEqualTo(3);
    }

    @Test
    public void addWithCategoryLinesParam_hasCategoryGraphicsListSizeIsOne() {
        // when
        CategoryPlot categoryPlot = new CategoryPlot();
        categoryPlot.add(categoryLines);
        // then
        Assertions.assertThat(categoryPlot.getGraphics().size()).isEqualTo(1);
    }

    @Test
    public void threeCallsLeftShiftWithCategoryBarsPointsAndStemsParam_hasCategoryGraphicsListSizeIsThree() {
        // when
        CategoryPlot categoryPlot = new CategoryPlot();
        categoryPlot.leftShift(categoryBars).leftShift(categoryPoints).leftShift(categoryStems);
        // then
        Assertions.assertThat(categoryPlot.getGraphics().size()).isEqualTo(3);
    }

    @Test
    public void leftShiftWithCategoryLinesParam_hasCategoryGraphicsListSizeIsOne() {
        // when
        CategoryPlot categoryPlot = new CategoryPlot();
        categoryPlot.leftShift(categoryLines);
        // then
        Assertions.assertThat(categoryPlot.getGraphics().size()).isEqualTo(1);
    }
}

