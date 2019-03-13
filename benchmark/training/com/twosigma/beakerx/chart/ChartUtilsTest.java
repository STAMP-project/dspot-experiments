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


import java.awt.Color;
import java.util.Arrays;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.Test;


public class ChartUtilsTest {
    @Test
    public void callConvertColorsWithAwtColorListParam_shouldReturnBeakerChartColorList() {
        // when
        List<Object> outlineColors = ChartUtils.convertColors(Arrays.asList(Color.BLACK, Color.GREEN), "takes Color or List of Color");
        // then
        Assertions.assertThat(((outlineColors.get(0)) instanceof Color)).isTrue();
        Assertions.assertThat(((outlineColors.get(1)) instanceof Color)).isTrue();
    }

    @Test
    public void callConvertColorsWithListOfListParam_shouldReturnBeakerChartColorListOfList() {
        // given
        ChartUtils chartUtils = new ChartUtils();
        // when
        List<Object> outlineColors = chartUtils.convertColors(Arrays.asList(Arrays.asList(Color.BLACK, Color.GREEN), Arrays.asList(Color.BLACK, Color.GREEN)), "takes Color or List of Color");
        // then
        Assertions.assertThat(((((List) (outlineColors.get(0))).get(0)) instanceof Color)).isTrue();
        Assertions.assertThat(((((List) (outlineColors.get(1))).get(0)) instanceof Color)).isTrue();
    }

    @Test(expected = IllegalArgumentException.class)
    public void callConvertColorsWithNumberListParam_throwIllegalArgumentException() {
        // when
        ChartUtils.convertColors(Arrays.asList(100, 200), "takes Color or List of Color");
    }
}

