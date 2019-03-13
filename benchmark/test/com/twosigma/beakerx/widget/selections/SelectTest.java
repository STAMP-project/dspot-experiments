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


import RadioButtons.OPTIONS_LABELS;
import Select.MODEL_NAME_VALUE;
import Select.VALUE;
import Select.VIEW_NAME_VALUE;
import com.twosigma.beakerx.KernelTest;
import com.twosigma.beakerx.widget.Select;
import com.twosigma.beakerx.widget.TestWidgetUtils;
import org.junit.Test;


public class SelectTest {
    private KernelTest kernel;

    @Test
    public void shouldSendCommOpenWhenCreate() throws Exception {
        // given
        // when
        new Select();
        // then
        TestWidgetUtils.verifyOpenCommMsg(kernel.getPublishedMessages(), MODEL_NAME_VALUE, VIEW_NAME_VALUE);
    }

    @Test
    public void shouldSendCommMsgWhenValueChange() throws Exception {
        // given
        Select widget = select();
        // when
        widget.setValue("1");
        // then
        TestWidgetUtils.verifyMsgForProperty(kernel, VALUE, "1");
    }

    @Test
    public void shouldSendCommMsgWhenOptionsChange() throws Exception {
        // given
        Select widget = select();
        // when
        widget.setOptions(new String[]{ "2", "3" });
        // then
        TestWidgetUtils.verifyMsgForProperty(kernel, OPTIONS_LABELS, new String[]{ "2", "3" });
    }
}

