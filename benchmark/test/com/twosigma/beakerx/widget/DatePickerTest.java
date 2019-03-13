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


import DatePicker.MODEL_NAME_VALUE;
import DatePicker.SHOW_TIME;
import DatePicker.VALUE;
import DatePicker.VIEW_NAME_VALUE;
import com.twosigma.beakerx.KernelTest;
import org.assertj.core.api.Assertions;
import org.junit.Test;


public class DatePickerTest {
    private KernelTest groovyKernel;

    @Test
    public void shouldSendCommOpenWhenCreate() throws Exception {
        // given
        // when
        new DatePicker();
        // then
        TestWidgetUtils.verifyInternalOpenCommMsgWitLayout(groovyKernel.getPublishedMessages(), MODEL_NAME_VALUE, VIEW_NAME_VALUE);
    }

    @Test
    public void changeValue_shouldSendCommMessage() throws Exception {
        String expected = "20120101";
        // given
        DatePicker widget = widget();
        // when
        widget.setValue(expected);
        // then
        TestWidgetUtils.verifyMsgForProperty(groovyKernel, VALUE, expected);
    }

    @Test
    public void setShowTime_shouldSendCommMessage() throws Exception {
        // given
        DatePicker widget = widget();
        // when
        widget.setShowTime(true);
        // then
        TestWidgetUtils.verifyMsgForProperty(groovyKernel, SHOW_TIME, true);
    }

    @Test
    public void setShowTimeFlag_hasThatShowTimeFlag() throws Exception {
        boolean expected = true;
        // given
        DatePicker widget = widget();
        // when
        widget.setShowTime(expected);
        // then
        Assertions.assertThat(widget.getShowTime()).isEqualTo(expected);
    }
}

