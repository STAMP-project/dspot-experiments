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
package com.twosigma.beakerx.widget;


import com.twosigma.beakerx.KernelTest;
import com.twosigma.beakerx.evaluator.EvaluatorResultTestWatcher;
import com.twosigma.beakerx.message.Message;
import java.util.Arrays;
import java.util.Map;
import org.junit.Test;


public class DOMWidgetTest {
    private KernelTest kernel;

    private DOMWidget widget;

    @Test
    public void shouldSendDomClassesUpdateMessage() {
        // given
        // when
        widget.setDomClasses(Arrays.asList("class1", "class2"));
        // then
        Message update = EvaluatorResultTestWatcher.getUpdate(kernel.getPublishedMessages()).get();
        Map state = TestWidgetUtils.getState(update);
        assertThat(state.get(DOMWidget.DOM_CLASSES)).isEqualTo(Arrays.asList("class1", "class2").toArray());
    }
}

