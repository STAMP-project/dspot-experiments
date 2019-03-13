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
package com.twosigma.beakerx.widget.selections;


import ToggleButtons.MODEL_NAME_VALUE;
import ToggleButtons.VIEW_NAME_VALUE;
import com.twosigma.beakerx.KernelTest;
import com.twosigma.beakerx.widget.TestWidgetUtils;
import com.twosigma.beakerx.widget.ToggleButtons;
import org.assertj.core.api.Assertions;
import org.junit.Test;


public class ToggleButtonsTest {
    private KernelTest kernel;

    @Test
    public void createSelectionSlider_shouldSendCommOpenMessage() throws Exception {
        // given
        // when
        new ToggleButtons();
        // then
        TestWidgetUtils.verifyOpenCommMsg(kernel.getPublishedMessages(), MODEL_NAME_VALUE, VIEW_NAME_VALUE);
    }

    @Test
    public void setButtonStyle_hasThatButtonStyle() throws Exception {
        String expected = "test";
        // given
        ToggleButtons toggleButtons = toggleButtons();
        // when
        toggleButtons.setButton_style(expected);
        // then
        Assertions.assertThat(toggleButtons.getButton_style()).isEqualTo(expected);
    }

    @Test
    public void setTooltips_hasThatTooltips() throws Exception {
        String[] expected = new String[]{ "test1", "test2" };
        // given
        ToggleButtons toggleButtons = toggleButtons();
        // when
        toggleButtons.setTooltips(expected);
        // then
        Assertions.assertThat(toggleButtons.getTooltips()).isEqualTo(expected);
    }

    @Test
    public void setIcons_hasThatIcons() throws Exception {
        String[] expected = new String[]{ "icon1", "icon2" };
        // given
        ToggleButtons toggleButtons = toggleButtons();
        // when
        toggleButtons.setIcons(expected);
        // then
        Assertions.assertThat(toggleButtons.getIcons()).isEqualTo(expected);
    }

    @Test
    public void setIcons_hasValue() throws Exception {
        // given
        ToggleButtons toggleButtons = toggleButtons();
        // when
        String[] options = new String[]{ "icon1", "icon2" };
        toggleButtons.setOptions(options);
        // then
        Assertions.assertThat(toggleButtons.getValue()).isEqualTo("icon1");
    }
}

