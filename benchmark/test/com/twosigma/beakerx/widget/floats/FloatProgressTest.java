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


import FloatProgress.BAR_STYLE;
import FloatProgress.BarStyle.SUCCESS;
import FloatProgress.MODEL_NAME_VALUE;
import FloatProgress.ORIENTATION;
import FloatProgress.VIEW_NAME_VALUE;
import com.twosigma.beakerx.KernelTest;
import com.twosigma.beakerx.widget.FloatProgress;
import com.twosigma.beakerx.widget.TestWidgetUtils;
import org.assertj.core.api.Assertions;
import org.junit.Test;


public class FloatProgressTest {
    private KernelTest groovyKernel;

    @Test
    public void shouldSendCommOpenWhenCreate() throws Exception {
        // given
        // when
        new FloatProgress();
        // then
        TestWidgetUtils.verifyOpenCommMsg(groovyKernel.getPublishedMessages(), MODEL_NAME_VALUE, VIEW_NAME_VALUE);
    }

    @Test
    public void shouldSendCommMsgWhenOrientationChange() throws Exception {
        // given
        FloatProgress floatProgress = floatProgress();
        // when
        floatProgress.setOrientation("vertical");
        // then
        TestWidgetUtils.verifyMsgForProperty(groovyKernel, ORIENTATION, "vertical");
    }

    @Test
    public void setOrientation_hasThatOrientation() throws Exception {
        String expected = "test";
        // given
        FloatProgress floatProgress = floatProgress();
        // when
        floatProgress.setOrientation(expected);
        // then
        Assertions.assertThat(floatProgress.getOrientation()).isEqualTo(expected);
    }

    @Test
    public void shouldSendCommMsgWhenBarStyleChange() throws Exception {
        // given
        FloatProgress floatProgress = floatProgress();
        // when
        floatProgress.setBarStyle(SUCCESS);
        // then
        TestWidgetUtils.verifyMsgForProperty(groovyKernel, BAR_STYLE, SUCCESS.getValue());
    }
}

