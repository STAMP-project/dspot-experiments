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
package com.twosigma.beakerx.widget.floats;


import FloatText.MODEL_NAME_VALUE;
import FloatText.VALUE;
import FloatText.VIEW_NAME_VALUE;
import com.twosigma.beakerx.KernelTest;
import com.twosigma.beakerx.widget.FloatText;
import com.twosigma.beakerx.widget.TestWidgetUtils;
import org.junit.Test;


public class FloatTextTest {
    private KernelTest groovyKernel;

    @Test
    public void createByEmptyConstructor_sendCommOpenMessage() throws Exception {
        // given
        // when
        new FloatText();
        // then
        TestWidgetUtils.verifyOpenCommMsg(groovyKernel.getPublishedMessages(), MODEL_NAME_VALUE, VIEW_NAME_VALUE);
    }

    @Test
    public void setValue_sendCommMessage() throws Exception {
        // given
        FloatText floatText = floatText();
        // when
        floatText.setValue(5.1);
        // then
        TestWidgetUtils.verifyMsgForProperty(groovyKernel, VALUE, 5.1);
    }
}

