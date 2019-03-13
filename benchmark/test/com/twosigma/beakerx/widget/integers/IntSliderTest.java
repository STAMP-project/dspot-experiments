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


import BoundedIntWidget.MAX;
import BoundedIntWidget.MIN;
import BoundedIntWidget.STEP;
import IntSlider.CONTINUOUS_UPDATE;
import IntSlider.MODEL_NAME_VALUE;
import IntSlider.ORIENTATION;
import IntSlider.READOUT;
import IntSlider.VALUE;
import IntSlider.VIEW_NAME_VALUE;
import Layout.VISIBILITY;
import Widget.DESCRIPTION;
import Widget.DISABLED;
import Widget.MSG_THROTTLE;
import com.twosigma.beakerx.KernelTest;
import com.twosigma.beakerx.widget.IntSlider;
import com.twosigma.beakerx.widget.TestWidgetUtils;
import org.assertj.core.api.Assertions;
import org.junit.Test;


public class IntSliderTest {
    private KernelTest groovyKernel;

    @Test
    public void shouldSendCommOpenWhenCreate() throws Exception {
        // given
        // when
        new IntSlider();
        // then
        TestWidgetUtils.verifyOpenCommMsg(groovyKernel.getPublishedMessages(), MODEL_NAME_VALUE, VIEW_NAME_VALUE);
    }

    @Test
    public void shouldSendCommMsgWhenValueChange() throws Exception {
        // given
        IntSlider intSlider = intSlider();
        // when
        intSlider.setValue(11);
        // then
        TestWidgetUtils.verifyMsgForProperty(groovyKernel, VALUE, 11);
    }

    @Test
    public void shouldSendCommMsgWhenDisableChange() throws Exception {
        // given
        IntSlider intSlider = intSlider();
        // when
        intSlider.setDisabled(true);
        // then
        TestWidgetUtils.verifyMsgForProperty(groovyKernel, DISABLED, true);
    }

    @Test
    public void shouldSendCommMsgWhenVisibleChange() throws Exception {
        // given
        IntSlider intSlider = intSlider();
        // when
        intSlider.getLayout().setVisibility("hidden");
        // then
        TestWidgetUtils.verifyMsgForProperty(groovyKernel, VISIBILITY, "hidden");
    }

    @Test
    public void shouldSendCommMsgWhenDescriptionChange() throws Exception {
        // given
        IntSlider intSlider = intSlider();
        // when
        intSlider.setDescription("Description 2");
        // then
        TestWidgetUtils.verifyMsgForProperty(groovyKernel, DESCRIPTION, "Description 2");
    }

    @Test
    public void shouldSendCommMsgWhenMsg_throttleChange() throws Exception {
        // given
        IntSlider intSlider = intSlider();
        // when
        intSlider.setMsg_throttle(12);
        // then
        TestWidgetUtils.verifyMsgForProperty(groovyKernel, MSG_THROTTLE, 12);
    }

    @Test
    public void shouldSendCommMsgWhenStepChange() throws Exception {
        // given
        IntSlider intSlider = intSlider();
        // when
        intSlider.setStep(12);
        // then
        TestWidgetUtils.verifyMsgForProperty(groovyKernel, STEP, 12);
    }

    @Test
    public void shouldSendCommMsgWhenMaxChange() throws Exception {
        // given
        IntSlider intSlider = intSlider();
        // when
        intSlider.setMax(122);
        // then
        TestWidgetUtils.verifyMsgForProperty(groovyKernel, MAX, 122);
    }

    @Test
    public void shouldSendCommMsgWhenMinChange() throws Exception {
        // given
        IntSlider intSlider = intSlider();
        // when
        intSlider.setMin(10);
        // then
        TestWidgetUtils.verifyMsgForProperty(groovyKernel, MIN, 10);
    }

    @Test
    public void shouldSendCommMsgWhenOrientationChange() throws Exception {
        // given
        IntSlider intSlider = intSlider();
        // when
        intSlider.setOrientation("vertical");
        // then
        TestWidgetUtils.verifyMsgForProperty(groovyKernel, ORIENTATION, "vertical");
    }

    @Test
    public void shouldSendCommMsgWhenReadOutChange() throws Exception {
        // given
        IntSlider intSlider = intSlider();
        // when
        intSlider.setReadOut(false);
        // then
        TestWidgetUtils.verifyMsgForProperty(groovyKernel, READOUT, false);
    }

    @Test
    public void shouldSendCommMsgWhenChangeContinuous_update() throws Exception {
        // given
        IntSlider intSlider = intSlider();
        // when
        intSlider.setContinuous_update(false);
        // then
        TestWidgetUtils.verifyMsgForProperty(groovyKernel, CONTINUOUS_UPDATE, false);
    }

    @Test
    public void setOrientation_hasThatOrientation() throws Exception {
        String expected = "test";
        // given
        IntSlider intSlider = intSlider();
        // when
        intSlider.setOrientation(expected);
        // then
        Assertions.assertThat(intSlider.getOrientation()).isEqualTo(expected);
    }

    @Test
    public void setReadout_hasThatReadoutFlag() throws Exception {
        boolean expected = true;
        // given
        IntSlider intSlider = intSlider();
        // when
        intSlider.setReadOut(expected);
        // then
        Assertions.assertThat(intSlider.getReadOut()).isEqualTo(expected);
    }

    @Test
    public void setContinuousUpdate_hasThatContinuousUpdateFlag() throws Exception {
        boolean expected = true;
        // given
        IntSlider intSlider = intSlider();
        // when
        intSlider.setContinuous_update(expected);
        // then
        Assertions.assertThat(intSlider.getContinuous_update()).isEqualTo(expected);
    }
}

