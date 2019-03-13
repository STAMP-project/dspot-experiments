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


import SelectMultiple.OPTIONS_LABELS;
import SelectMultiple.VALUE;
import SelectMultipleSingle.MODEL_NAME_VALUE;
import SelectMultipleSingle.VIEW_NAME_VALUE;
import Widget.INDEX;
import com.twosigma.beakerx.KernelTest;
import com.twosigma.beakerx.widget.SelectMultipleSingle;
import com.twosigma.beakerx.widget.TestWidgetUtils;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;


public class SelectMultipleSingleTest {
    private KernelTest kernel;

    @Test
    public void noSelection() throws Exception {
        SelectMultipleSingle widget = selectMultipleSingle();
        kernel.clearPublishedMessages();
        // when
        widget.setOptions(new String[]{ "1", "2", "3" });
        // then
        Integer index = TestWidgetUtils.getValueForProperty(kernel.getPublishedMessages().get(1), INDEX, Integer.class);
        assertThat(index).isEqualTo((-1));
    }

    @Test
    public void updateValue() throws Exception {
        SelectMultipleSingle widget = selectMultipleSingle();
        widget.setOptions(new String[]{ "1", "2", "3" });
        kernel.clearPublishedMessages();
        // when
        widget.updateValue(1);
        // then
        assertThat(widget.getValue()).isEqualTo(new String[]{ "2" });
    }

    @Test
    public void shouldNotUpdateValueWhenArrayOfIndexes() throws Exception {
        SelectMultipleSingle widget = selectMultipleSingle();
        widget.setOptions(new String[]{ "1", "2", "3" });
        kernel.clearPublishedMessages();
        // when
        try {
            widget.updateValue(Arrays.asList(1, 2));
            Assert.fail("should not update value when list of indexes");
        } catch (Exception e) {
            // then
            // test passes
        }
    }

    @Test
    public void shouldSendCommOpenWhenCreate() throws Exception {
        // given
        // when
        new SelectMultipleSingle();
        // then
        TestWidgetUtils.verifyInternalOpenCommMsgWitLayout(kernel.getPublishedMessages(), MODEL_NAME_VALUE, VIEW_NAME_VALUE);
    }

    @Test
    public void shouldSendCommMsgWhenValueChange() throws Exception {
        // given
        SelectMultipleSingle widget = selectMultipleSingle();
        widget.setOptions(new String[]{ "1", "2", "3" });
        kernel.clearPublishedMessages();
        // when
        widget.setValue("2");
        // then
        TestWidgetUtils.verifyMsgForProperty(kernel, VALUE, new String[]{ "2" });
    }

    @Test
    public void shouldSendCommMsgWhenOptionsChange() throws Exception {
        // given
        SelectMultipleSingle widget = selectMultipleSingle();
        // when
        widget.setOptions(new String[]{ "2", "3" });
        // then
        TestWidgetUtils.verifyMsgForProperty(kernel, OPTIONS_LABELS, new String[]{ "2", "3" });
    }
}

