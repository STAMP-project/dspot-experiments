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


import BoundedFloatText.MODEL_NAME_VALUE;
import BoundedFloatText.VIEW_NAME_VALUE;
import com.twosigma.beakerx.KernelTest;
import com.twosigma.beakerx.widget.BoundedFloatText;
import com.twosigma.beakerx.widget.TestWidgetUtils;
import org.assertj.core.api.Assertions;
import org.junit.Test;


public class BoundedFloatTextTest {
    private KernelTest groovyKernel;

    @Test
    public void createByEmptyConstructor_sendCommOpenMessage() throws Exception {
        // given
        // when
        new BoundedFloatText();
        // then
        TestWidgetUtils.verifyOpenCommMsg(groovyKernel.getPublishedMessages(), MODEL_NAME_VALUE, VIEW_NAME_VALUE);
    }

    @Test
    public void setValue_sendCommMessage() throws Exception {
        double expected = 5.5;
        // given
        BoundedFloatText boundedFloatText = boundedFloatText();
        // when
        boundedFloatText.setValue(expected);
        // then
        TestWidgetUtils.verifyMsgForProperty(groovyKernel, boundedFloatText.VALUE, expected);
    }

    @Test
    public void respectMax() {
        // given
        BoundedFloatText boundedFloatText = new BoundedFloatText();
        boundedFloatText.setMax(10.5);
        groovyKernel.clearPublishedMessages();
        // when
        boundedFloatText.setValue(15.123);
        // then
        TestWidgetUtils.verifyMsgForProperty(groovyKernel, boundedFloatText.VALUE, 10.5);
    }

    @Test
    public void respectMin() {
        // given
        double min = 0.0;
        BoundedFloatText boundedFloatText = new BoundedFloatText();
        boundedFloatText.setMin(min);
        groovyKernel.clearPublishedMessages();
        // when
        boundedFloatText.setValue((-1.1));
        // then
        TestWidgetUtils.verifyMsgForProperty(groovyKernel, boundedFloatText.VALUE, 0.0);
    }

    @Test
    public void shouldReturnMinWhenValueLessThenMin() {
        // given
        double min = 5.0;
        BoundedFloatText boundedFloatText = new BoundedFloatText();
        groovyKernel.clearPublishedMessages();
        // when
        boundedFloatText.setMin(min);
        // then
        Double valueForProperty = TestWidgetUtils.findValueForProperty(groovyKernel, boundedFloatText.VALUE, Double.class);
        Assertions.assertThat(valueForProperty).isEqualTo(min);
    }

    @Test
    public void shouldReturnMaxWhenValueGreaterThenMax() {
        // given
        double max = 5.0;
        BoundedFloatText boundedFloatText = new BoundedFloatText();
        boundedFloatText.setValue(100);
        groovyKernel.clearPublishedMessages();
        // when
        boundedFloatText.setMax(max);
        // then
        Double valueForProperty = TestWidgetUtils.findValueForProperty(groovyKernel, boundedFloatText.VALUE, Double.class);
        Assertions.assertThat(valueForProperty).isEqualTo(max);
    }
}

