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
package com.twosigma.beakerx.chart.xychart.plotitem;


import Color.GREEN;
import java.awt.Color;
import java.util.Arrays;
import java.util.Date;
import org.assertj.core.api.Assertions;
import org.junit.Test;


public class ConstantBandTest {
    ConstantBand constantBand;

    @Test
    public void createConstantBandByEmptyConstructor_hasXsAndYsListsAreNulls() {
        // when
        ConstantBand constantBand = new ConstantBand();
        // then
        Assertions.assertThat(constantBand.getX()).isNull();
        Assertions.assertThat(constantBand.getY()).isNull();
    }

    @Test
    public void setXWithIntegerFloatDateListParam_hasXsListAreNotNulls() {
        // when
        constantBand.setX(Arrays.asList(new Integer(123), new Float(123.0), new Date()));
        // then
        Assertions.assertThat(constantBand.getX()).isNotNull();
    }

    @Test
    public void setColorWithAwtColor_constantBandHasBeakerColor() {
        // when
        constantBand.setColor(Color.GREEN);
        // then
        Assertions.assertThat(((constantBand.getColor()) instanceof com.twosigma.beakerx.chart.Color)).isTrue();
    }

    @Test
    public void setColori_hasColor() {
        // when
        constantBand.setColori(GREEN);
        // then
        Assertions.assertThat(constantBand.getColor()).isEqualTo(GREEN);
    }
}

