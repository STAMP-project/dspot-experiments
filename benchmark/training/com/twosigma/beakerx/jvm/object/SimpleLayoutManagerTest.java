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
package com.twosigma.beakerx.jvm.object;


import Widget.VALUE;
import com.twosigma.beakerx.KernelTest;
import com.twosigma.beakerx.mimetype.MIMEContainer;
import com.twosigma.beakerx.widget.TestWidgetUtils;
import org.junit.Test;


public class SimpleLayoutManagerTest {
    private KernelTest kernel;

    @Test
    public void create_borderDisplayedIsFalse() throws Exception {
        // when
        SimpleLayoutManager manager = new SimpleLayoutManager();
        // then
        assertThat(manager.isBorderDisplayed()).isFalse();
    }

    @Test
    public void outputContainerWithHtml() throws Exception {
        // given
        String code = "<h4>Title</h4>";
        OutputContainer outputContainer = new OutputContainer();
        outputContainer.leftShift(MIMEContainer.HTML(code));
        // when
        outputContainer.display();
        // then
        String value = TestWidgetUtils.findValueForProperty(kernel, VALUE, String.class);
        assertThat(value).isEqualTo(code);
    }

    @Test
    public void outputContainerWithNull() throws Exception {
        // given
        OutputContainer outputContainer = new OutputContainer();
        outputContainer.leftShift(null);
        // when
        outputContainer.display();
        // then
        String value = TestWidgetUtils.findValueForProperty(kernel, VALUE, String.class);
        assertThat(value).isEqualTo("null");
    }
}

