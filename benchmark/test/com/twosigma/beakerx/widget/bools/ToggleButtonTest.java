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
package com.twosigma.beakerx.widget.bools;


import ToggleButton.MODEL_NAME_VALUE;
import ToggleButton.TOOLTIP;
import ToggleButton.VALUE;
import ToggleButton.VIEW_NAME_VALUE;
import com.twosigma.beakerx.KernelTest;
import com.twosigma.beakerx.widget.TestWidgetUtils;
import com.twosigma.beakerx.widget.ToggleButton;
import org.assertj.core.api.Assertions;
import org.junit.Test;


public class ToggleButtonTest {
    private KernelTest groovyKernel;

    @Test
    public void shouldSendCommOpenWhenCreate() throws Exception {
        // given
        // when
        new ToggleButton();
        // then
        TestWidgetUtils.verifyOpenCommMsg(groovyKernel.getPublishedMessages(), MODEL_NAME_VALUE, VIEW_NAME_VALUE);
    }

    @Test
    public void shouldSendCommMsgWhenValueChange() throws Exception {
        // given
        ToggleButton widget = toggleButton();
        // when
        widget.setValue(true);
        // then
        TestWidgetUtils.verifyMsgForProperty(groovyKernel, VALUE, true);
    }

    @Test
    public void shouldSendCommMsgWhenTooltipChange() throws Exception {
        // given
        ToggleButton widget = toggleButton();
        // when
        widget.setTooltip("Tooltip 2");
        // then
        TestWidgetUtils.verifyMsgForProperty(groovyKernel, TOOLTIP, "Tooltip 2");
    }

    @Test
    public void setButtonStyle_hasThatButtonStyle() throws Exception {
        String expected = "test";
        // given
        ToggleButton toggleButton = toggleButton();
        // when
        toggleButton.setButton_style(expected);
        // then
        Assertions.assertThat(toggleButton.getButton_style()).isEqualTo(expected);
    }

    @Test
    public void setIcon_hasThatIcon() throws Exception {
        String expected = "test";
        // given
        ToggleButton toggleButton = toggleButton();
        // when
        toggleButton.setIcon(expected);
        // then
        Assertions.assertThat(toggleButton.getIcon()).isEqualTo(expected);
    }

    @Test
    public void setTooltip_hasThatTooltip() throws Exception {
        String expected = "test";
        // given
        ToggleButton toggleButton = toggleButton();
        // when
        toggleButton.setTooltip(expected);
        // then
        Assertions.assertThat(toggleButton.getTooltip()).isEqualTo(expected);
    }
}

