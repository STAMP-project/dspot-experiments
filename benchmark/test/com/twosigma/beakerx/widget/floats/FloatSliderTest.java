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
package com.twosigma.beakerx.widget.floats;


import BoundedFloatWidget.MAX;
import BoundedFloatWidget.MIN;
import BoundedFloatWidget.STEP;
import FloatSlider.CONTINUOUS_UPDATE;
import FloatSlider.MODEL_NAME_VALUE;
import FloatSlider.ORIENTATION;
import FloatSlider.READOUT;
import FloatSlider.VALUE;
import FloatSlider.VIEW_NAME_VALUE;
import com.twosigma.beakerx.KernelTest;
import com.twosigma.beakerx.widget.FloatSlider;
import com.twosigma.beakerx.widget.TestWidgetUtils;
import org.assertj.core.api.Assertions;
import org.junit.Test;


public class FloatSliderTest {
    private KernelTest groovyKernel;

    @Test
    public void shouldSendCommOpenWhenCreate() throws Exception {
        // given
        // when
        new FloatSlider();
        // then
        TestWidgetUtils.verifyOpenCommMsg(groovyKernel.getPublishedMessages(), MODEL_NAME_VALUE, VIEW_NAME_VALUE);
    }

    @Test
    public void shouldSendCommMsgWhenValueChange() throws Exception {
        // given
        FloatSlider floatSlider = floatSlider();
        // when
        floatSlider.setValue(11.1);
        // then
        TestWidgetUtils.verifyMsgForProperty(groovyKernel, VALUE, 11.1);
    }

    @Test
    public void shouldSendCommMsgWhenStepChange() throws Exception {
        // given
        FloatSlider floatSlider = floatSlider();
        // when
        floatSlider.setStep(12.1);
        // then
        TestWidgetUtils.verifyMsgForProperty(groovyKernel, STEP, 12.1);
    }

    @Test
    public void shouldSendCommMsgWhenMaxChange() throws Exception {
        // given
        FloatSlider floatSlider = floatSlider();
        // when
        floatSlider.setMax(122.3);
        // then
        TestWidgetUtils.verifyMsgForProperty(groovyKernel, MAX, 122.3);
    }

    @Test
    public void shouldSendCommMsgWhenMinChange() throws Exception {
        // given
        FloatSlider floatSlider = floatSlider();
        // when
        floatSlider.setMin(10.2);
        // then
        TestWidgetUtils.verifyMsgForProperty(groovyKernel, MIN, 10.2);
    }

    @Test
    public void shouldSendCommMsgWhenOrientationChange() throws Exception {
        // given
        FloatSlider floatSlider = floatSlider();
        // when
        floatSlider.setOrientation("vertical");
        // then
        TestWidgetUtils.verifyMsgForProperty(groovyKernel, ORIENTATION, "vertical");
    }

    @Test
    public void shouldSendCommMsgWhenReadOutChange() throws Exception {
        // given
        FloatSlider floatSlider = floatSlider();
        // when
        floatSlider.setReadOut(false);
        // then
        TestWidgetUtils.verifyMsgForProperty(groovyKernel, READOUT, false);
    }

    @Test
    public void shouldSendCommMsgWhenChangeContinuous_update() throws Exception {
        // given
        FloatSlider floatSlider = floatSlider();
        // when
        floatSlider.setContinuous_update(false);
        // then
        TestWidgetUtils.verifyMsgForProperty(groovyKernel, CONTINUOUS_UPDATE, false);
    }

    @Test
    public void setOrientation_hasThatOrientation() throws Exception {
        String expected = "test";
        // given
        FloatSlider floatSlider = floatSlider();
        // when
        floatSlider.setOrientation(expected);
        // then
        Assertions.assertThat(floatSlider.getOrientation()).isEqualTo(expected);
    }

    @Test
    public void setReadout_hasThatReadoutFlag() throws Exception {
        boolean expected = true;
        // given
        FloatSlider floatSlider = floatSlider();
        // when
        floatSlider.setReadOut(expected);
        // then
        Assertions.assertThat(floatSlider.getReadOut()).isEqualTo(expected);
    }

    @Test
    public void setContinuousUpdate_hasThatContinuousUpdateFlag() throws Exception {
        boolean expected = true;
        // given
        FloatSlider floatSlider = floatSlider();
        // when
        floatSlider.setContinuous_update(expected);
        // then
        Assertions.assertThat(floatSlider.getContinuous_update()).isEqualTo(expected);
    }
}

