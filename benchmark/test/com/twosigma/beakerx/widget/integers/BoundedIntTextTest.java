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
package com.twosigma.beakerx.widget.integers;


import BoundedIntText.MODEL_NAME_VALUE;
import BoundedIntText.VIEW_NAME_VALUE;
import com.twosigma.beakerx.KernelTest;
import com.twosigma.beakerx.widget.BoundedIntText;
import com.twosigma.beakerx.widget.TestWidgetUtils;
import org.assertj.core.api.Assertions;
import org.junit.Test;


public class BoundedIntTextTest {
    private KernelTest groovyKernel;

    @Test
    public void createByEmptyConstructor_sendCommOpenMessage() throws Exception {
        // given
        // when
        new BoundedIntText();
        // then
        TestWidgetUtils.verifyOpenCommMsg(groovyKernel.getPublishedMessages(), MODEL_NAME_VALUE, VIEW_NAME_VALUE);
    }

    @Test
    public void setValue_sendCommMessage() {
        int expected = 2;
        // given
        BoundedIntText boundedIntText = BoundedIntText();
        // when
        boundedIntText.setValue(expected);
        // then
        TestWidgetUtils.verifyMsgForProperty(groovyKernel, boundedIntText.VALUE, expected);
    }

    @Test
    public void respectMax() {
        // given
        BoundedIntText boundedIntText = BoundedIntText();
        boundedIntText.setMax(10);
        groovyKernel.clearPublishedMessages();
        // when
        boundedIntText.setValue(15);
        // then
        TestWidgetUtils.verifyMsgForProperty(groovyKernel, boundedIntText.VALUE, 10);
    }

    @Test
    public void respectMin() {
        // given
        int min = 0;
        BoundedIntText boundedIntText = BoundedIntText();
        boundedIntText.setMin(min);
        groovyKernel.clearPublishedMessages();
        // when
        boundedIntText.setValue((-1));
        // then
        TestWidgetUtils.verifyMsgForProperty(groovyKernel, boundedIntText.VALUE, 0);
    }

    @Test
    public void shouldReturnMinWhenValueLessThenMin() {
        // given
        int min = 5;
        BoundedIntText boundedIntText = BoundedIntText();
        groovyKernel.clearPublishedMessages();
        // when
        boundedIntText.setMin(min);
        // then
        Integer valueForProperty = TestWidgetUtils.findValueForProperty(groovyKernel, boundedIntText.VALUE, Integer.class);
        Assertions.assertThat(valueForProperty).isEqualTo(min);
    }

    @Test
    public void shouldReturnMaxWhenValueGreaterThenMax() {
        // given
        int max = 5;
        BoundedIntText boundedIntText = BoundedIntText();
        boundedIntText.setValue(100);
        groovyKernel.clearPublishedMessages();
        // when
        boundedIntText.setMax(max);
        // then
        Integer valueForProperty = TestWidgetUtils.findValueForProperty(groovyKernel, boundedIntText.VALUE, Integer.class);
        Assertions.assertThat(valueForProperty).isEqualTo(max);
    }
}

