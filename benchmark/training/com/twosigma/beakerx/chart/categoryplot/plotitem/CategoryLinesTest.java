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
package com.twosigma.beakerx.chart.categoryplot.plotitem;


import com.twosigma.beakerx.chart.xychart.plotitem.StrokeType;
import java.util.Arrays;
import org.assertj.core.api.Assertions;
import org.junit.Test;


public class CategoryLinesTest {
    CategoryLines categoryLines;

    @Test
    public void createCategoryLinesByEmptyConstructor_hasWidthIsNotNull() {
        // then
        Assertions.assertThat(categoryLines.getWidth()).isNotNull();
    }

    @Test
    public void setStyleWithStrokeTypeList_hasStylesListIsNotEmpty() {
        // when
        categoryLines.setStyle(Arrays.asList(StrokeType.values()));
        // then
        Assertions.assertThat(categoryLines.getStyles()).isNotEmpty();
    }

    @Test
    public void setValueWithIntegerArrayParam_hasValueIsNotEmpty() {
        // when
        categoryLines.setValue(new Integer[]{ new Integer(1), new Integer(2) });
        // then
        Assertions.assertThat(categoryLines.getValue()).isNotEmpty();
    }
}

