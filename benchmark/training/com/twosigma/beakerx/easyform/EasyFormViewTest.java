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
package com.twosigma.beakerx.easyform;


import EasyFormView.EASY_FORM_NAME;
import EasyFormView.MODEL_NAME_VALUE;
import EasyFormView.VIEW_NAME_VALUE;
import com.twosigma.beakerx.KernelTest;
import com.twosigma.beakerx.widget.TestWidgetUtils;
import java.util.Arrays;
import org.junit.Test;


public class EasyFormViewTest {
    private KernelTest groovyKernel;

    @Test
    public void shouldSendCommOpenWhenCreate() throws Exception {
        // given
        // when
        new EasyFormView(Arrays.asList());
        // then
        TestWidgetUtils.verifyInternalOpenCommMsgWitLayout(groovyKernel.getPublishedMessages(), MODEL_NAME_VALUE, VIEW_NAME_VALUE);
    }

    @Test
    public void shouldSendCommMsgWhenEasyFormNameChange() throws Exception {
        // given
        EasyFormView widget = easyForm();
        // when
        widget.setEasyFormName("title2");
        // then
        TestWidgetUtils.verifyMsgForProperty(groovyKernel, EASY_FORM_NAME, "title2");
    }
}

