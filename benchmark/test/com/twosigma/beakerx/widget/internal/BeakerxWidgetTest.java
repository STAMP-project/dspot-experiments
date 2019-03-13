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
package com.twosigma.beakerx.widget.internal;


import com.twosigma.beakerx.KernelTest;
import com.twosigma.beakerx.widget.BeakerxWidget;
import com.twosigma.beakerx.widget.BeakerxWidgetTestRunner;
import org.junit.Test;


public class BeakerxWidgetTest {
    @Test
    public void shouldSendCommOpenWhenCreateForAllClassesWhichImplementInternalWidgetInterface() throws Exception {
        new BeakerxWidgetTestRunner().test(( clazz, groovyKernel) -> {
            // given
            // when
            BeakerxWidget widget = clazz.newInstance();
            // then
            verifyOpenCommMsgBeakerxWidget(groovyKernel.getPublishedMessages(), widget.getModelNameValue(), widget.getViewNameValue());
        });
    }
}

