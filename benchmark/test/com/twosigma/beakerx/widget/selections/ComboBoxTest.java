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


import ComboBox.MODEL_NAME_VALUE;
import ComboBox.VALUE;
import ComboBox.VIEW_NAME_VALUE;
import com.twosigma.beakerx.KernelTest;
import com.twosigma.beakerx.widget.ComboBox;
import com.twosigma.beakerx.widget.TestWidgetUtils;
import org.assertj.core.api.Assertions;
import org.junit.Test;


public class ComboBoxTest {
    private KernelTest kernel;

    @Test
    public void createWithBooleanParam_shouldSendCommOpenMessage() throws Exception {
        // given
        // when
        new ComboBox(true);
        // then
        TestWidgetUtils.verifyInternalOpenCommMsgWitLayout(kernel.getPublishedMessages(), MODEL_NAME_VALUE, VIEW_NAME_VALUE);
    }

    @Test
    public void createWithEmptyConstructor_shouldSendCommOpenMessage() throws Exception {
        // given
        // when
        new ComboBox();
        // then
        TestWidgetUtils.verifyInternalOpenCommMsgWitLayout(kernel.getPublishedMessages(), MODEL_NAME_VALUE, VIEW_NAME_VALUE);
    }

    @Test
    public void updateValue_shouldSendCommMessage() throws Exception {
        String expected = "test";
        // given
        ComboBox comboBox = comboBox();
        // when
        comboBox.updateValue(expected);
        // then
        TestWidgetUtils.verifyMsgForProperty(kernel, VALUE, expected);
    }

    @Test
    public void setEditable_hasThatEditableFlag() throws Exception {
        boolean expected = true;
        // given
        ComboBox comboBox = comboBox();
        // when
        comboBox.setEditable(expected);
        // then
        Assertions.assertThat(comboBox.getEditable()).isEqualTo(expected);
    }

    @Test
    public void setSize_hasThatSize() throws Exception {
        int expected = 10;
        // given
        ComboBox comboBox = comboBox();
        // when
        comboBox.setSize(expected);
        // then
        Assertions.assertThat(comboBox.getSize()).isEqualTo(expected);
    }
}

