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


import Color.BLUE;
import Color.GREEN;
import java.util.Date;
import org.assertj.core.api.Assertions;
import org.junit.Test;


public class TextTest {
    Text text;

    @Test
    public void createTextByEmptyConstructor_hasXYValuesEqualsZero() {
        // when
        Text text = new Text();
        // then
        Assertions.assertThat(text.getX()).isEqualTo(0.0);
        Assertions.assertThat(text.getY()).isEqualTo(0.0);
    }

    @Test
    public void createTextByEmptyConstructor_hasSizeValueGreaterThanZero() {
        // when
        Text text = new Text();
        // then
        Assertions.assertThat(text.getSize()).isGreaterThan(0);
    }

    @Test
    public void setXWithDateParam_hasXWithNumberValue() {
        // when
        text.setX(new Date());
        // then
        Assertions.assertThat(((text.getX()) instanceof Number)).isTrue();
    }

    @Test
    public void setXWithIntegerParam_hasXWithNumberValue() {
        // when
        text.setX(new Integer(10));
        // then
        Assertions.assertThat(((text.getX()) instanceof Number)).isTrue();
    }

    @Test
    public void clone_shouldCloneText() throws CloneNotSupportedException {
        // when
        text.setColor(GREEN);
        Text cloneText = ((Text) (text.clone()));
        text.setColor(BLUE);
        // then
        Assertions.assertThat(cloneText.getColor()).isEqualTo(GREEN);
        Assertions.assertThat(text.getColor()).isEqualTo(BLUE);
    }
}

