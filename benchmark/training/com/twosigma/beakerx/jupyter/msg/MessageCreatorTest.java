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
package com.twosigma.beakerx.jupyter.msg;


import JupyterMessages.ERROR;
import JupyterMessages.EXECUTE_REPLY;
import JupyterMessages.EXECUTE_RESULT;
import JupyterMessages.STATUS;
import MessageCreator.BUSY;
import MessageCreator.EXECUTION_STATE;
import MessageCreator.IDLE;
import MessageCreator.TEXT_PLAIN;
import SocketEnum.IOPUB_SOCKET;
import SocketEnum.SHELL_SOCKET;
import com.twosigma.beakerx.KernelTest;
import com.twosigma.beakerx.MessageFactorTest;
import com.twosigma.beakerx.jvm.object.SimpleEvaluationObject;
import com.twosigma.beakerx.kernel.msg.MessageCreator;
import com.twosigma.beakerx.kernel.msg.MessageHolder;
import com.twosigma.beakerx.message.Message;
import com.twosigma.beakerx.widget.TestWidgetUtils;
import java.util.List;
import java.util.Map;
import org.junit.Test;


public class MessageCreatorTest {
    private SimpleEvaluationObject seo;

    KernelTest kernel;

    @Test
    public void createMessageWithNullResult_shouldReturnNullStringForNull() throws Exception {
        // given
        seo.finished(null);
        // when
        List<MessageHolder> message = MessageCreator.createMessage(seo);
        // then
        Map data = TestWidgetUtils.getData(message.get(0).getMessage());
        assertThat(data.get(TEXT_PLAIN)).isEqualTo(MessageCreator.NULL_RESULT);
    }

    @Test
    public void createMessageWithNotNullResult_shouldReturnResult() throws Exception {
        // given
        seo.finished("NotNullResult");
        // when
        List<MessageHolder> message = MessageCreator.createMessage(seo);
        // then
        Map data = TestWidgetUtils.getData(message.get(0).getMessage());
        assertThat(data.get(TEXT_PLAIN)).isEqualTo("NotNullResult");
    }

    @Test
    public void createMessageWithNotNullResult_createTwoMessages() throws Exception {
        // given
        seo.finished("result");
        // when
        List<MessageHolder> messages = MessageCreator.createMessage(seo);
        // then
        assertThat(messages).isNotEmpty();
        assertThat(messages.size()).isEqualTo(2);
    }

    @Test
    public void createMessageWithNotNullResult_firstIOPubMessageHasTypeIsExecuteResult() throws Exception {
        // given
        seo.finished("result");
        // when
        List<MessageHolder> messages = MessageCreator.createMessage(seo);
        // then
        assertThat(messages).isNotEmpty();
        assertThat(messages.get(0).getSocketType()).isEqualTo(IOPUB_SOCKET);
        assertThat(messages.get(0).getMessage().type()).isEqualTo(EXECUTE_RESULT);
    }

    @Test
    public void createMessageWithNotNullResult_secondShellMessageHasTypeIsExecuteReply() throws Exception {
        // given
        seo.finished("result");
        // when
        List<MessageHolder> messages = MessageCreator.createMessage(seo);
        // then
        assertThat(messages).isNotEmpty();
        assertThat(messages.get(1).getSocketType()).isEqualTo(SHELL_SOCKET);
        assertThat(messages.get(1).getMessage().type()).isEqualTo(EXECUTE_REPLY);
    }

    @Test
    public void createIdleMessage_messageHasTypeIsStatus() {
        // when
        Message message = MessageCreator.createIdleMessage(MessageFactorTest.commMsg());
        // then
        assertThat(message.type()).isEqualTo(STATUS);
    }

    @Test
    public void createIdleMessage_messageHasExecutionStateIsIdle() {
        // when
        Message message = MessageCreator.createIdleMessage(MessageFactorTest.commMsg());
        // then
        Map data = message.getContent();
        assertThat(data.get(EXECUTION_STATE)).isEqualTo(IDLE);
    }

    @Test
    public void createBusyMessage_messageHasTypeIsStatus() {
        // when
        Message message = MessageCreator.createBusyMessage(MessageFactorTest.commMsg());
        // then
        assertThat(message.type()).isEqualTo(STATUS);
    }

    @Test
    public void createBusyMessage_messageHasExecutionStateIsBusy() {
        // when
        Message message = MessageCreator.createBusyMessage(MessageFactorTest.commMsg());
        // then
        Map data = message.getContent();
        assertThat(data.get(EXECUTION_STATE)).isEqualTo(BUSY);
    }

    @Test
    public void createMessageWithError_createTwoMessages() throws Exception {
        // given
        seo.error("some error");
        // when
        List<MessageHolder> messages = MessageCreator.createMessage(seo);
        // then
        assertThat(messages).isNotEmpty();
        assertThat(messages.size()).isEqualTo(2);
    }

    @Test
    public void createMessageWithError_firstIOPubMessageHasTypeIsStream() throws Exception {
        // given
        seo.error("some error");
        // when
        List<MessageHolder> messages = MessageCreator.createMessage(seo);
        // then
        assertThat(messages).isNotEmpty();
        assertThat(messages.get(0).getSocketType()).isEqualTo(IOPUB_SOCKET);
        assertThat(messages.get(0).getMessage().type()).isEqualTo(ERROR);
    }

    @Test
    public void createMessageWithError_secondShellMessageHasTypeIsExecuteReply() throws Exception {
        // given
        seo.error("some error");
        // when
        List<MessageHolder> messages = MessageCreator.createMessage(seo);
        // then
        assertThat(messages).isNotEmpty();
        assertThat(messages.get(1).getSocketType()).isEqualTo(SHELL_SOCKET);
        assertThat(messages.get(1).getMessage().type()).isEqualTo(EXECUTE_REPLY);
    }

    @Test
    public void createMessageWithError_whenException() throws Exception {
        // given
        seo.error(new RuntimeException("oops"));
        // when
        List<MessageHolder> messages = MessageCreator.createMessage(seo);
        // then
        assertThat(messages).isNotEmpty();
        assertThat(messages.get(1).getSocketType()).isEqualTo(SHELL_SOCKET);
        assertThat(messages.get(1).getMessage().type()).isEqualTo(EXECUTE_REPLY);
    }
}

