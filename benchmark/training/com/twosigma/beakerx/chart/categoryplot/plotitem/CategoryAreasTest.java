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
package com.twosigma.beakerx.chart.categoryplot.plotitem;


import Color.BLACK;
import Color.BLUE;
import java.awt.Color;
import java.util.Arrays;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.Test;


public class CategoryAreasTest {
    Integer[] array1;

    Integer[] array2;

    CategoryArea categoryArea;

    @Test
    public void createCategoryAreaByEmptyConstructor_hasBaseBaseEqualsZero() {
        // then
        Assertions.assertThat(categoryArea.getBase()).isEqualTo(0.0);
    }

    @Test
    public void setValueWithIntegerArrayParam_hasValueIsNotEmpty() {
        // when
        categoryArea.setValue(array1);
        // then
        Assertions.assertThat(categoryArea.getValue()).isNotEmpty();
    }

    @Test
    public void setValueWithIntegerArrayOfListsParam_hasValueIsNotEmpty() {
        // when
        categoryArea.setValue(new List[]{ Arrays.asList(array1), Arrays.asList(array2) });
        // then
        Assertions.assertThat(categoryArea.getValue()).isNotEmpty();
    }

    @Test
    public void setBaseWithIntegerListParam_hasBasesIsNotEmpty() {
        // when
        categoryArea.setBase(Arrays.asList(((Object[]) (array1))));
        // then
        Assertions.assertThat(categoryArea.getBases()).isNotEmpty();
    }

    @Test
    public void setWidthIntegerListParam_hasWidthsIsNotEmpty() {
        // when
        categoryArea.setWidth(Arrays.asList(array2));
        // then
        Assertions.assertThat(categoryArea.getWidths()).isNotEmpty();
    }

    @Test
    public void setOutlineColorByAwtColor_hasOutlineColorIsBeakerColor() {
        // when
        categoryArea.setOutlineColor(Color.BLUE);
        // then
        Assertions.assertThat(categoryArea.getOutlineColor()).isEqualTo(BLUE);
    }

    @Test
    public void setCenterSeriesByTrue_centerSeriesIsTrue() {
        // when
        categoryArea.setCenterSeries(true);
        // then
        Assertions.assertThat(categoryArea.getCenterSeries()).isTrue();
    }

    @Test
    public void setUseToolTip_useToolTipIsTrue() {
        // when
        categoryArea.setUseToolTip(true);
        // then
        Assertions.assertThat(categoryArea.getUseToolTip()).isTrue();
    }

    @Test
    public void setColori_hasColor() {
        // when
        categoryArea.setColori(BLUE);
        // then
        Assertions.assertThat(categoryArea.getColor()).isEqualTo(BLUE);
    }

    @Test
    public void setColorWithAwtColor_hasBeakerColor() {
        // when
        categoryArea.setColor(Color.BLUE);
        // then
        Assertions.assertThat(categoryArea.getColor()).isEqualTo(BLUE);
    }

    @Test
    public void setColorWithColorList_hasBeakerColors() {
        // when
        categoryArea.setColor(Arrays.asList(BLACK, BLUE));
        // then
        Assertions.assertThat(categoryArea.getColors()).isNotEmpty();
    }
}

