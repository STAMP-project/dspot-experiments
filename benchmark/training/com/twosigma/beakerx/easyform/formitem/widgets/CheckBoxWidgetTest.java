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


import org.junit.Test;


public class CheckBoxWidgetTest extends EasyFormWidgetTest {
    @Test
    public void setValue() throws Exception {
        // given
        Boolean newValue = Boolean.TRUE;
        CheckBoxWidget widget = new CheckBoxWidget();
        kernel.clearPublishedMessages();
        // when
        widget.setValue(newValue.toString());
        // then
        verifyValue(kernel.getPublishedMessages().get(0), newValue);
    }
}

