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


import Color.BLACK;
import Color.BLUE;
import java.awt.Color;
import java.util.Arrays;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.Test;


public class CategoryBarsTest {
    Integer[] array1;

    Integer[] array2;

    CategoryBars categoryBars;

    @Test
    public void createCategoryBarsByEmptyConstructor_hasBaseBaseEqualsZero() {
        // then
        Assertions.assertThat(categoryBars.getBase()).isEqualTo(0.0);
    }

    @Test
    public void setValueWithIntegerArrayParam_hasValueIsNotEmpty() {
        // when
        categoryBars.setValue(array1);
        // then
        Assertions.assertThat(categoryBars.getValue()).isNotEmpty();
    }

    @Test
    public void setValueWithIntegerArrayOfListsParam_hasValueIsNotEmpty() {
        // when
        categoryBars.setValue(new List[]{ Arrays.asList(array1), Arrays.asList(array2) });
        // then
        Assertions.assertThat(categoryBars.getValue()).isNotEmpty();
    }

    @Test
    public void setBaseWithIntegerListParam_hasBasesIsNotEmpty() {
        // when
        categoryBars.setBase(Arrays.asList(((Object[]) (array1))));
        // then
        Assertions.assertThat(categoryBars.getBases()).isNotEmpty();
    }

    @Test
    public void setWidthIntegerListParam_hasWidthsIsNotEmpty() {
        // when
        categoryBars.setWidth(Arrays.asList(array2));
        // then
        Assertions.assertThat(categoryBars.getWidths()).isNotEmpty();
    }

    @Test
    public void setOutlineColorByAwtColor_hasOutlineColorIsBeakerColor() {
        // when
        categoryBars.setOutlineColor(Color.BLUE);
        // then
        Assertions.assertThat(categoryBars.getOutlineColor()).isEqualTo(BLUE);
    }

    @Test
    public void setCenterSeriesByTrue_centerSeriesIsTrue() {
        // when
        categoryBars.setCenterSeries(true);
        // then
        Assertions.assertThat(categoryBars.getCenterSeries()).isTrue();
    }

    @Test
    public void setUseToolTip_useToolTipIsTrue() {
        // when
        categoryBars.setUseToolTip(true);
        // then
        Assertions.assertThat(categoryBars.getUseToolTip()).isTrue();
    }

    @Test
    public void setColori_hasColor() {
        // when
        categoryBars.setColori(BLUE);
        // then
        Assertions.assertThat(categoryBars.getColor()).isEqualTo(BLUE);
    }

    @Test
    public void setColorWithAwtColor_hasBeakerColor() {
        // when
        categoryBars.setColor(Color.BLUE);
        // then
        Assertions.assertThat(categoryBars.getColor()).isEqualTo(BLUE);
    }

    @Test
    public void setColorWithColorList_hasBeakerColors() {
        // when
        categoryBars.setColor(Arrays.asList(BLACK, BLUE));
        // then
        Assertions.assertThat(categoryBars.getColors()).isNotEmpty();
    }
}

