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
package com.twosigma.beakerx.widget.integers;


import IntRangeSlider.CONTINUOUS_UPDATE;
import IntRangeSlider.MODEL_NAME_VALUE;
import IntRangeSlider.ORIENTATION;
import IntRangeSlider.READOUT;
import IntRangeSlider.VIEW_NAME_VALUE;
import com.twosigma.beakerx.KernelTest;
import com.twosigma.beakerx.widget.IntRangeSlider;
import com.twosigma.beakerx.widget.TestWidgetUtils;
import org.assertj.core.api.Assertions;
import org.junit.Test;


public class IntRangeSliderTest {
    private KernelTest groovyKernel;

    @Test
    public void createByEmptyConstructor_sendCommOpenMessage() throws Exception {
        // given
        // when
        new IntRangeSlider();
        // then
        TestWidgetUtils.verifyOpenCommMsg(groovyKernel.getPublishedMessages(), MODEL_NAME_VALUE, VIEW_NAME_VALUE);
    }

    @Test
    public void setOrientation_sendCommMessage() throws Exception {
        String expected = "test";
        // given
        IntRangeSlider intRangeSlider = IntRangeSlider();
        // when
        intRangeSlider.setOrientation(expected);
        // then
        TestWidgetUtils.verifyMsgForProperty(groovyKernel, ORIENTATION, expected);
    }

    @Test
    public void setReadOut_sendCommMessage() throws Exception {
        boolean expected = true;
        // given
        IntRangeSlider intRangeSlider = IntRangeSlider();
        // when
        intRangeSlider.setReadOut(expected);
        // then
        TestWidgetUtils.verifyMsgForProperty(groovyKernel, READOUT, expected);
    }

    @Test
    public void setContinuousUpdate_sendCommMessage() throws Exception {
        boolean expected = true;
        // given
        IntRangeSlider intRangeSlider = IntRangeSlider();
        // when
        intRangeSlider.setContinuous_update(expected);
        // then
        TestWidgetUtils.verifyMsgForProperty(groovyKernel, CONTINUOUS_UPDATE, expected);
    }

    @Test
    public void setOrientation_hasThatOrientation() throws Exception {
        String expected = "test";
        // given
        IntRangeSlider intRangeSlider = IntRangeSlider();
        // when
        intRangeSlider.setOrientation(expected);
        // then
        Assertions.assertThat(intRangeSlider.getOrientation()).isEqualTo(expected);
    }

    @Test
    public void setReadOut_hasThatReadOutFlag() throws Exception {
        boolean expected = true;
        // given
        IntRangeSlider intRangeSlider = IntRangeSlider();
        // when
        intRangeSlider.setReadOut(expected);
        // then
        Assertions.assertThat(intRangeSlider.getReadOut()).isEqualTo(expected);
    }

    @Test
    public void setContinuousUpdate_hasThatContinuousUpdateFlag() throws Exception {
        boolean expected = true;
        // given
        IntRangeSlider intRangeSlider = IntRangeSlider();
        // when
        intRangeSlider.setContinuous_update(expected);
        // then
        Assertions.assertThat(intRangeSlider.getContinuous_update()).isEqualTo(expected);
    }

    @Test
    public void setStep_hasThatStep() throws Exception {
        Integer expected = new Integer(10);
        // given
        IntRangeSlider intRangeSlider = IntRangeSlider();
        // when
        intRangeSlider.setStep(expected);
        // then
        Assertions.assertThat(intRangeSlider.getStep()).isEqualTo(expected);
    }

    @Test
    public void setMax_hasThatMax() throws Exception {
        Integer expected = new Integer(11);
        // given
        IntRangeSlider intRangeSlider = IntRangeSlider();
        // when
        intRangeSlider.setMax(expected);
        // then
        Assertions.assertThat(intRangeSlider.getMax()).isEqualTo(expected);
    }

    @Test
    public void setMin_hasThatMin() throws Exception {
        Integer expected = new Integer(12);
        // given
        IntRangeSlider intRangeSlider = IntRangeSlider();
        // when
        intRangeSlider.setMin(expected);
        // then
        Assertions.assertThat(intRangeSlider.getMin()).isEqualTo(expected);
    }

    @Test
    public void setUpper_hasThatUpper() throws Exception {
        Integer expected = new Integer(13);
        // given
        IntRangeSlider intRangeSlider = IntRangeSlider();
        // when
        intRangeSlider.setUpper(expected);
        // then
        Assertions.assertThat(intRangeSlider.getUpper()).isEqualTo(expected);
    }

    @Test
    public void setLower_hasThatLower() throws Exception {
        Integer expected = new Integer(14);
        // given
        IntRangeSlider intRangeSlider = IntRangeSlider();
        // when
        intRangeSlider.setLower(expected);
        // then
        Assertions.assertThat(intRangeSlider.getLower()).isEqualTo(expected);
    }

    @Test
    public void getValueFromObject_returnArrayOfIntegers() throws Exception {
        Integer[] expected = new Integer[]{ 15, 16 };
        // given
        IntRangeSlider intRangeSlider = IntRangeSlider();
        // when
        Integer[] result = intRangeSlider.getValueFromObject(expected);
        // then
        Assertions.assertThat(result[0]).isEqualTo(expected[0]);
        Assertions.assertThat(result[1]).isEqualTo(expected[1]);
    }
}

