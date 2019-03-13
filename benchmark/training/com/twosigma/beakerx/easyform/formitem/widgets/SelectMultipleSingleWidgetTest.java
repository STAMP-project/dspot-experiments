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
package com.twosigma.beakerx.easyform.formitem.widgets;


import SelectMultiple.VALUE;
import SelectMultipleSingle.SIZE;
import com.twosigma.beakerx.widget.TestWidgetUtils;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Test;


public class SelectMultipleSingleWidgetTest extends EasyFormWidgetTest {
    @Test
    public void setValue() throws Exception {
        // given
        List<String> newValue = Collections.singletonList("2");
        SelectMultipleSingleWidget widget = new SelectMultipleSingleWidget();
        widget.setValues(Arrays.asList("1", "2", "3"));
        kernel.clearPublishedMessages();
        // when
        widget.setValue(newValue);
        // then
        TestWidgetUtils.verifyMsgForProperty(kernel, VALUE, new String[]{ "2" });
        assertThat(widget.getValue()).isEqualTo(newValue);
    }

    @Test
    public void setSize() throws Exception {
        // given
        Integer newValue = 2;
        SelectMultipleSingleWidget widget = new SelectMultipleSingleWidget();
        widget.setValues(Arrays.asList("1", "2", "3"));
        kernel.clearPublishedMessages();
        // when
        widget.setSize(newValue);
        // then
        TestWidgetUtils.verifyMsgForProperty(kernel, SIZE, 2);
    }
}

