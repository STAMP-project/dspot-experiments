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


import SelectionSlider.INDEX;
import SelectionSlider.MODEL_NAME_VALUE;
import SelectionSlider.OPTIONS_LABELS;
import SelectionSlider.VIEW_NAME_VALUE;
import com.twosigma.beakerx.KernelTest;
import com.twosigma.beakerx.widget.SelectionSlider;
import com.twosigma.beakerx.widget.TestWidgetUtils;
import java.util.Arrays;
import org.assertj.core.api.Assertions;
import org.junit.Test;


public class SelectionSliderTest {
    private KernelTest kernel;

    @Test
    public void createSelectionSlider_shouldSendCommOpenMessage() throws Exception {
        // given
        // when
        new SelectionSlider();
        // then
        TestWidgetUtils.verifyOpenCommMsg(kernel.getPublishedMessages(), MODEL_NAME_VALUE, VIEW_NAME_VALUE);
    }

    @Test
    public void setOptions_shouldSendCommMessage() throws Exception {
        String expected = "test";
        // given
        SelectionSlider selectionSlider = selectionSlider();
        // when
        selectionSlider.setOptions(expected);
        // then
        TestWidgetUtils.verifyMsgForProperty(kernel, OPTIONS_LABELS, expected);
    }

    @Test
    public void setSize_hasThatSize() throws Exception {
        int expected = 10;
        // given
        SelectionSlider selectionSlider = selectionSlider();
        // when
        selectionSlider.setSize(expected);
        // then
        Assertions.assertThat(selectionSlider.getSize()).isEqualTo(expected);
    }

    @Test
    public void setOrientation_hasThatOrientation() throws Exception {
        String expected = "test";
        // given
        SelectionSlider selectionSlider = selectionSlider();
        // when
        selectionSlider.setOrientation(expected);
        // then
        Assertions.assertThat(selectionSlider.getOrientation()).isEqualTo(expected);
    }

    @Test
    public void getValueFromObject_returnString() throws Exception {
        // given
        SelectionSlider selectionSlider = selectionSlider();
        // when
        String result = selectionSlider.getValueFromObject(11);
        // then
        Assertions.assertThat(result).isEqualTo("11");
    }

    @Test
    public void setValue_shouldSendMessageWithIndex() throws Exception {
        // given
        SelectionSlider selectionSlider = selectionSlider();
        selectionSlider.setOptions(Arrays.asList("scrambled", "sunny side up", "poached", "over easy"));
        kernel.clearPublishedMessages();
        // when
        selectionSlider.setValue("sunny side up");
        // then
        Integer index = TestWidgetUtils.findValueForProperty(kernel, INDEX, Integer.class);
        Assertions.assertThat(index).isEqualTo(1);
    }

    @Test
    public void updateValue() throws Exception {
        // given
        SelectionSlider selectionSlider = selectionSlider();
        selectionSlider.setOptions(Arrays.asList("scrambled", "sunny side up", "poached", "over easy"));
        kernel.clearPublishedMessages();
        // when
        selectionSlider.updateValue(2);
        // then
        Assertions.assertThat(selectionSlider.getValue()).isEqualTo("poached");
    }
}

