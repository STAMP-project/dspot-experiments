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
package com.twosigma.beakerx.groovy.widgets;


import Comm.DATA;
import Comm.STATE;
import JupyterMessages.COMM_MSG;
import Text.VIEW_NAME_VALUE;
import com.twosigma.beakerx.KernelTest;
import com.twosigma.beakerx.evaluator.BaseEvaluator;
import com.twosigma.beakerx.jupyter.SearchMessages;
import com.twosigma.beakerx.kernel.comm.Comm;
import com.twosigma.beakerx.message.Message;
import java.util.Map;
import org.assertj.core.api.Assertions;
import org.junit.Test;


public class InteractiveTest {
    private BaseEvaluator groovyEvaluator;

    private KernelTest groovyKernel;

    @Test
    public void interactWithStringParam_shouldCreateTextWidget() throws Exception {
        // when
        callInteractWithStringParam("\"A\"");
        // then
        verifyTextField(groovyKernel.getPublishedMessages(), Text.MODEL_NAME_VALUE, Text.MODEL_MODULE_VALUE, VIEW_NAME_VALUE, Text.VIEW_MODULE_VALUE);
    }

    @Test
    public void valueChangeMsgCallback_createDisplayDataMessage() throws Exception {
        // given
        callInteractWithStringParam("\"A\"");
        Comm comm = getCommWidgetByViewName(VIEW_NAME_VALUE);
        // when
        comm.handleMsg(initSyncDataMessage(comm.getCommId(), "TEST"));
        // then
        Message display = SearchMessages.getListMessagesByType(groovyKernel.getPublishedMessages(), COMM_MSG).get(2);
        Map date = ((Map) (display.getContent().get(DATA)));
        Map state = ((Map) (date.get(STATE)));
        Assertions.assertThat(state).isNotEmpty();
        Assertions.assertThat(state.get("value")).isEqualTo("TEST");
    }
}

