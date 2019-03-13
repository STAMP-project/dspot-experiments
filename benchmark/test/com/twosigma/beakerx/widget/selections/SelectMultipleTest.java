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


import SelectMultiple.MODEL_NAME_VALUE;
import SelectMultiple.OPTIONS_LABELS;
import SelectMultiple.VALUE;
import SelectMultiple.VIEW_NAME_VALUE;
import com.twosigma.beakerx.KernelTest;
import com.twosigma.beakerx.widget.SelectMultiple;
import com.twosigma.beakerx.widget.TestWidgetUtils;
import java.util.Arrays;
import org.junit.Test;


public class SelectMultipleTest {
    private KernelTest kernel;

    @Test
    public void shouldSendCommOpenWhenCreate() throws Exception {
        // given
        // when
        new SelectMultiple();
        // then
        TestWidgetUtils.verifyInternalOpenCommMsgWitLayout(kernel.getPublishedMessages(), MODEL_NAME_VALUE, VIEW_NAME_VALUE);
    }

    @Test
    public void shouldSendCommMsgWhenValueChange() throws Exception {
        // given
        SelectMultiple widget = selectMultiple();
        widget.setOptions(new String[]{ "1", "2", "3" });
        kernel.clearPublishedMessages();
        // when
        widget.setValue(Arrays.asList("1", "2"));
        // then
        TestWidgetUtils.verifyMsgForProperty(kernel, VALUE, new String[]{ "1", "2" });
    }

    @Test
    public void shouldSendCommMsgWhenOptionsChange() throws Exception {
        // given
        SelectMultiple widget = selectMultiple();
        // when
        widget.setOptions(new String[]{ "1", "2" });
        // then
        TestWidgetUtils.verifyMsgForProperty(kernel, OPTIONS_LABELS, new String[]{ "1", "2" });
    }
}

