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
package com.twosigma.beakerx.easyform.formitem.widgets;


import com.twosigma.beakerx.jvm.threads.BeakerStdInOutErrHandler;
import com.twosigma.beakerx.kernel.msg.MessageCreator;
import com.twosigma.beakerx.message.Message;
import org.junit.Test;


public class ButtonComponentWidgetTest extends EasyFormWidgetTest {
    @Test
    public void setTag() throws Exception {
        // given
        String tag = "tag1";
        ButtonComponentWidget widget = new ButtonComponentWidget();
        kernel.clearPublishedMessages();
        // when
        widget.setTag(tag);
        // then
        verifyTag(kernel.getPublishedMessages().get(0), tag);
    }

    @Test
    public void handleActionPerformed() throws Exception {
        // given
        final StringBuilder result = new StringBuilder();
        ButtonComponentWidget widget = new ButtonComponentWidget();
        widget.actionPerformed = ( value) -> result.append("action done 1");
        // when
        widget.getComm().handleMsg(messageWithClickEvent());
        // then
        assertThat(result.toString()).isEqualTo("action done 1");
    }

    @Test
    public void noHandleActionPerformed() throws Exception {
        // given
        final StringBuilder result = new StringBuilder().append("no action");
        ButtonComponentWidget widget = new ButtonComponentWidget();
        widget.actionPerformed = ( value) -> result.append("action done 2");
        // when
        widget.getComm().handleMsg(messageWithoutClickEvent());
        // then
        assertThat(result.toString()).isEqualTo("no action");
    }

    @Test
    public void handleStdOut() throws Exception {
        try {
            // given
            String outputText = "handleOutput";
            BeakerStdInOutErrHandler.init(kernel);
            ButtonComponentWidget widget = new ButtonComponentWidget();
            kernel.clearPublishedMessages();
            widget.actionPerformed = ( value) -> System.out.print(outputText);
            // when
            widget.getComm().handleMsg(messageWithClickEvent());
            // then
            Message outputMessage = getOutputMessage(kernel);
            assertThat(outputMessage.getContent().get(MessageCreator.NAME)).isEqualTo(MessageCreator.STDOUT);
            assertThat(outputMessage.getContent().get(MessageCreator.TEXT)).isEqualTo(outputText);
        } finally {
            BeakerStdInOutErrHandler.fini();
        }
    }
}

