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
package com.twosigma.beakerx.widget;


import Button.ICON;
import Button.MODEL_NAME_VALUE;
import Button.TAG;
import Button.TOOLTIP;
import Button.VIEW_NAME_VALUE;
import Utils.EMPTY_STRING;
import Widget.MODEL_MODULE_VALUE;
import Widget.VIEW_MODULE_VALUE;
import com.twosigma.beakerx.KernelTest;
import org.junit.Test;


public class ButtonTest {
    private KernelTest groovyKernel;

    @Test
    public void shouldSendCommOpenWhenCreate() throws Exception {
        // given
        // when
        new Button();
        // then
        TestWidgetUtils.verifyInternalOpenCommMsgWitLayout(groovyKernel.getPublishedMessages(), MODEL_NAME_VALUE, VIEW_NAME_VALUE, MODEL_MODULE_VALUE, VIEW_MODULE_VALUE);
    }

    @Test
    public void noTooltipAsDefault() throws Exception {
        // given
        // when
        Button widget = button();
        // then
        assertThat(widget.getTooltip()).isEqualTo(EMPTY_STRING);
    }

    @Test
    public void shouldSendCommMsgWhenTooltipChange() throws Exception {
        // given
        Button widget = button();
        // when
        widget.setTooltip("Tooltip 2");
        // then
        TestWidgetUtils.verifyMsgForProperty(groovyKernel, TOOLTIP, "Tooltip 2");
    }

    @Test
    public void shouldSendCommMsgWhenTagChange() throws Exception {
        // given
        Button widget = button();
        // when
        widget.setTag("Tag2");
        // then
        TestWidgetUtils.verifyMsgForProperty(groovyKernel, TAG, "Tag2");
    }

    @Test
    public void shouldSendCommMsgWhenIconChange() throws Exception {
        // given
        Button widget = button();
        // when
        widget.setIcon("check");
        // then
        TestWidgetUtils.verifyMsgForProperty(groovyKernel, ICON, "check");
    }

    @Test
    public void setButtonStyle_hasThatButtonStyle() throws Exception {
        String expected = "test";
        // given
        Button button = button();
        // when
        button.setButton_style(expected);
        // then
        assertThat(button.getButton_style()).isEqualTo(expected);
    }

    @Test
    public void setTooltip_hasThatTooltip() throws Exception {
        String expected = "test";
        // given
        Button button = button();
        // when
        button.setTooltip(expected);
        // then
        assertThat(button.getTooltip()).isEqualTo(expected);
    }
}

