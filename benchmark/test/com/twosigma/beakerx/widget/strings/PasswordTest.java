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
package com.twosigma.beakerx.widget.strings;


import Password.MODEL_MODULE_VALUE;
import Password.MODEL_NAME_VALUE;
import Password.VALUE;
import Password.VIEW_MODULE_VALUE;
import Password.VIEW_NAME_VALUE;
import com.twosigma.beakerx.KernelTest;
import com.twosigma.beakerx.widget.Password;
import com.twosigma.beakerx.widget.TestWidgetUtils;
import org.junit.Test;


public class PasswordTest {
    private KernelTest groovyKernel;

    @Test
    public void shouldSendCommOpenWhenCreate() throws Exception {
        // given
        // when
        new Password();
        // then
        PasswordTest.verifyPasswordField(groovyKernel.getPublishedMessages(), MODEL_NAME_VALUE, MODEL_MODULE_VALUE, VIEW_NAME_VALUE, VIEW_MODULE_VALUE);
    }

    @Test
    public void shouldSendCommMsgWhenValueChange() throws Exception {
        // given
        Password widget = password();
        // when
        widget.setValue("1");
        // then
        TestWidgetUtils.verifyMsgForProperty(groovyKernel, VALUE, "1");
    }
}

