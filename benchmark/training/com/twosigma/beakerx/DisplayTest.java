/**
 * Copyright 2018 TWO SIGMA OPEN SOURCE, LLC
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
package com.twosigma.beakerx;


import com.twosigma.beakerx.message.Message;
import com.twosigma.beakerx.widget.IntSlider;
import org.junit.Test;


public class DisplayTest {
    private KernelTest kernel;

    @Test
    public void shouldSendMessageForText() {
        // given
        // when
        Display.display("Hello");
        // then
        verifyText();
    }

    @Test
    public void shouldSendMessageForWidget() {
        // given
        // when
        Display.display(new IntSlider());
        // then
        verifyWidget();
    }

    @Test
    public void contentShouldContainMetadata() {
        // given
        // when
        Display.display("Hello");
        // then
        Message message = kernel.getPublishedMessages().get(0);
        assertThat(message.getContent().get(METADATA)).isNotNull();
    }
}

