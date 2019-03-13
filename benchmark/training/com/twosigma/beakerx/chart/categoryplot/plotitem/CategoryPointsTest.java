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


import Color.BLUE;
import com.twosigma.beakerx.chart.xychart.plotitem.ShapeType;
import java.awt.Color;
import java.util.Arrays;
import org.assertj.core.api.Assertions;
import org.junit.Test;


public class CategoryPointsTest {
    CategoryPoints categoryPoints;

    @Test
    public void createCategoryPointsByEmptyConstructor_hasSizeGreaterThanZero() {
        // then
        Assertions.assertThat(categoryPoints.getSize()).isGreaterThan(0);
    }

    @Test
    public void setSizeWithIntegerList_hasSizesLstIsNotEmpty() {
        // when
        categoryPoints.setSize(Arrays.asList(new Integer(1), new Integer(2)));
        // then
        Assertions.assertThat(categoryPoints.getSizes()).isNotEmpty();
    }

    @Test
    public void setShapeWithShapeTypeList_hasShapesListIsNotEmpty() {
        // when
        categoryPoints.setShape(Arrays.asList(ShapeType.values()));
        // then
        Assertions.assertThat(categoryPoints.getShapes()).isNotEmpty();
    }

    @Test
    public void setValueWithIntegerArrayParam_hasValueIsNotEmpty() {
        // when
        categoryPoints.setValue(new Integer[]{ new Integer(1), new Integer(2) });
        // then
        Assertions.assertThat(categoryPoints.getValue()).isNotEmpty();
    }

    @Test
    public void setOutlineColorByAwtColor_hasOutlineColorIsBeakerColor() {
        // when
        categoryPoints.setOutlineColor(Color.BLUE);
        // then
        Assertions.assertThat(categoryPoints.getOutlineColor()).isEqualTo(BLUE);
    }
}

