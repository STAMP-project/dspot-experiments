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


import IntProgress.BAR_STYLE;
import IntProgress.BarStyle.SUCCESS;
import IntProgress.MODEL_NAME_VALUE;
import IntProgress.VIEW_NAME_VALUE;
import IntSlider.ORIENTATION;
import com.twosigma.beakerx.KernelTest;
import com.twosigma.beakerx.widget.IntProgress;
import com.twosigma.beakerx.widget.TestWidgetUtils;
import org.assertj.core.api.Assertions;
import org.junit.Test;


public class IntProgressTest {
    private KernelTest groovyKernel;

    @Test
    public void shouldSendCommOpenWhenCreate() throws Exception {
        // given
        // when
        new IntProgress();
        // then
        TestWidgetUtils.verifyOpenCommMsg(groovyKernel.getPublishedMessages(), MODEL_NAME_VALUE, VIEW_NAME_VALUE);
    }

    @Test
    public void shouldSendCommMsgWhenOrientationChange() throws Exception {
        // given
        IntProgress intProgress = intProgress();
        // when
        intProgress.setOrientation("vertical");
        // then
        TestWidgetUtils.verifyMsgForProperty(groovyKernel, ORIENTATION, "vertical");
    }

    @Test
    public void setOrientation_hasThatOrientation() throws Exception {
        String expected = "test";
        // given
        IntProgress intProgress = intProgress();
        // when
        intProgress.setOrientation(expected);
        // then
        Assertions.assertThat(intProgress.getOrientation()).isEqualTo(expected);
    }

    @Test
    public void shouldSendCommMsgWhenBarStyleChange() throws Exception {
        // given
        IntProgress intProgress = intProgress();
        // when
        intProgress.setBarStyle(SUCCESS);
        // then
        TestWidgetUtils.verifyMsgForProperty(groovyKernel, BAR_STYLE, SUCCESS.getValue());
    }
}

