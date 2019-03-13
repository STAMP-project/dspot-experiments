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
package com.twosigma.beakerx.jupyter.handler;


import JupyterMessages.EXECUTE_INPUT;
import JupyterMessages.STATUS;
import com.twosigma.beakerx.KernelTest;
import com.twosigma.beakerx.evaluator.EvaluatorResultTestWatcher;
import com.twosigma.beakerx.evaluator.EvaluatorTest;
import com.twosigma.beakerx.kernel.handler.ExecuteRequestHandler;
import com.twosigma.beakerx.kernel.magic.command.MagicCommandWhichThrowsException;
import com.twosigma.beakerx.kernel.msg.JupyterMessages;
import com.twosigma.beakerx.message.Message;
import java.util.List;
import java.util.Optional;
import org.junit.Test;


public class ExecuteRequestHandlerTest {
    private static KernelTest kernel;

    private static EvaluatorTest evaluatorTest;

    private ExecuteRequestHandler executeRequestHandler;

    private Message message;

    private Message magicMessage;

    @Test
    public void handleMessage_firstSentMessageHasExecutionStateIsBusy() throws Exception {
        // when
        executeRequestHandler.handle(message);
        EvaluatorResultTestWatcher.waitForIdleMessage(ExecuteRequestHandlerTest.kernel);
        // then
        assertThat(ExecuteRequestHandlerTest.kernel.getPublishedMessages()).isNotEmpty();
        Message publishMessage = ExecuteRequestHandlerTest.kernel.getPublishedMessages().get(0);
        assertThat(publishMessage.getContent().get("execution_state")).isEqualTo("busy");
    }

    @Test
    public void handleMessage_firstSentMessageHasSessionId() throws Exception {
        // given
        String expectedSessionId = message.getHeader().getSession();
        // when
        executeRequestHandler.handle(message);
        EvaluatorResultTestWatcher.waitForIdleMessage(ExecuteRequestHandlerTest.kernel);
        // then
        assertThat(ExecuteRequestHandlerTest.kernel.getPublishedMessages()).isNotEmpty();
        Message publishMessage = ExecuteRequestHandlerTest.kernel.getPublishedMessages().get(0);
        assertThat(publishMessage.getHeader().getSession()).isEqualTo(expectedSessionId);
    }

    @Test
    public void handleMessage_firstSentMessageHasTypeIsStatus() throws Exception {
        // when
        executeRequestHandler.handle(message);
        EvaluatorResultTestWatcher.waitForIdleMessage(ExecuteRequestHandlerTest.kernel);
        // then
        assertThat(ExecuteRequestHandlerTest.kernel.getPublishedMessages()).isNotEmpty();
        Message publishMessage = ExecuteRequestHandlerTest.kernel.getPublishedMessages().get(0);
        assertThat(publishMessage.getHeader().getType()).isEqualTo(STATUS.getName());
    }

    @Test
    public void handleMessage_firstSentMessageHasParentHeader() throws Exception {
        // given
        String expectedHeader = message.getHeader().asJson();
        // when
        executeRequestHandler.handle(message);
        EvaluatorResultTestWatcher.waitForIdleMessage(ExecuteRequestHandlerTest.kernel);
        // then
        assertThat(ExecuteRequestHandlerTest.kernel.getPublishedMessages()).isNotEmpty();
        Message publishMessage = ExecuteRequestHandlerTest.kernel.getPublishedMessages().get(0);
        assertThat(publishMessage.getParentHeader().asJson()).isEqualTo(expectedHeader);
    }

    @Test
    public void handleMessage_firstSentMessageHasIdentities() throws Exception {
        // given
        String expectedIdentities = new String(message.getIdentities().get(0));
        // when
        executeRequestHandler.handle(message);
        EvaluatorResultTestWatcher.waitForIdleMessage(ExecuteRequestHandlerTest.kernel);
        // then
        assertThat(ExecuteRequestHandlerTest.kernel.getPublishedMessages()).isNotEmpty();
        Message publishMessage = ExecuteRequestHandlerTest.kernel.getPublishedMessages().get(0);
        assertThat(new String(publishMessage.getIdentities().get(0))).isEqualTo(expectedIdentities);
    }

    @Test
    public void handleMessage_secondSentMessageHasSessionId() throws Exception {
        // given
        String expectedSessionId = message.getHeader().getSession();
        // when
        executeRequestHandler.handle(message);
        EvaluatorResultTestWatcher.waitForIdleMessage(ExecuteRequestHandlerTest.kernel);
        // then
        assertThat(ExecuteRequestHandlerTest.kernel.getPublishedMessages()).isNotEmpty();
        Message publishMessage = ExecuteRequestHandlerTest.kernel.getPublishedMessages().get(1);
        assertThat(publishMessage.getHeader().getSession()).isEqualTo(expectedSessionId);
    }

    @Test
    public void handleMessage_secondSendMessageHasTypeIsExecutionInput() throws Exception {
        // when
        executeRequestHandler.handle(message);
        EvaluatorResultTestWatcher.waitForIdleMessage(ExecuteRequestHandlerTest.kernel);
        // then
        assertThat(ExecuteRequestHandlerTest.kernel.getPublishedMessages()).isNotEmpty();
        Message publishMessage = ExecuteRequestHandlerTest.kernel.getPublishedMessages().get(1);
        assertThat(publishMessage.getHeader().getType()).isEqualTo(EXECUTE_INPUT.getName());
    }

    @Test
    public void handleMessage_secondSentMessageHasContentCode() throws Exception {
        // given
        String expectedCode = ((String) (message.getContent().get("code")));
        // when
        executeRequestHandler.handle(message);
        EvaluatorResultTestWatcher.waitForIdleMessage(ExecuteRequestHandlerTest.kernel);
        // then
        assertThat(ExecuteRequestHandlerTest.kernel.getPublishedMessages()).isNotEmpty();
        Message publishMessage = ExecuteRequestHandlerTest.kernel.getPublishedMessages().get(1);
        assertThat(publishMessage.getContent().get("code")).isEqualTo(expectedCode);
    }

    @Test
    public void handleMessage_secondSentMessageHasContentExecutionCount() throws Exception {
        // when
        executeRequestHandler.handle(message);
        EvaluatorResultTestWatcher.waitForIdleMessage(ExecuteRequestHandlerTest.kernel);
        // then
        assertThat(ExecuteRequestHandlerTest.kernel.getPublishedMessages()).isNotEmpty();
        Message publishMessage = ExecuteRequestHandlerTest.kernel.getPublishedMessages().get(1);
        assertThat(publishMessage.getContent().get("execution_count")).isNotNull();
    }

    @Test
    public void handleMessage_secondSentMessageHasParentHeader() throws Exception {
        // given
        String expectedHeader = message.getHeader().asJson();
        // when
        executeRequestHandler.handle(message);
        EvaluatorResultTestWatcher.waitForIdleMessage(ExecuteRequestHandlerTest.kernel);
        // then
        assertThat(ExecuteRequestHandlerTest.kernel.getPublishedMessages()).isNotEmpty();
        Message publishMessage = ExecuteRequestHandlerTest.kernel.getPublishedMessages().get(1);
        assertThat(publishMessage.getParentHeader().asJson()).isEqualTo(expectedHeader);
    }

    @Test
    public void handleMessage_secondSentMessageHasIdentities() throws Exception {
        // given
        String expectedIdentities = new String(message.getIdentities().get(0));
        // when
        executeRequestHandler.handle(message);
        EvaluatorResultTestWatcher.waitForIdleMessage(ExecuteRequestHandlerTest.kernel);
        // then
        assertThat(ExecuteRequestHandlerTest.kernel.getPublishedMessages()).isNotEmpty();
        Message publishMessage = ExecuteRequestHandlerTest.kernel.getPublishedMessages().get(1);
        assertThat(new String(publishMessage.getIdentities().get(0))).isEqualTo(expectedIdentities);
    }

    @Test
    public void handleMagicMessage_executionStateStartsBusyEndsIdle() throws Exception {
        // when
        executeRequestHandler.handle(magicMessage);
        EvaluatorResultTestWatcher.waitForIdleMessage(ExecuteRequestHandlerTest.kernel);
        // then
        final List<Message> publishedMessages = ExecuteRequestHandlerTest.kernel.getPublishedMessages();
        assertThat(publishedMessages).isNotEmpty();
        Message firstPublishedMessage = publishedMessages.get(0);
        assertThat(firstPublishedMessage.getContent().get("execution_state")).isEqualTo("busy");
        Message lastPublishedMessage = publishedMessages.get(((publishedMessages.size()) - 1));
        assertThat(lastPublishedMessage.getContent().get("execution_state")).isEqualTo("idle");
    }

    @Test
    public void handleMagicMessage_replyIsSent() throws InterruptedException {
        // when
        executeRequestHandler.handle(magicMessage);
        EvaluatorResultTestWatcher.waitForIdleMessage(ExecuteRequestHandlerTest.kernel);
        // then
        final List<Message> sentMessages = ExecuteRequestHandlerTest.kernel.getSentMessages();
        assertThat(sentMessages).isNotEmpty();
        Message firstSentMessage = sentMessages.get(0);
        assertThat(firstSentMessage.getHeader().getTypeEnum()).isEqualTo(JupyterMessages.EXECUTE_REPLY);
    }

    @Test
    public void shouldSendErrorMessageWhenMagicCommandThrowsException() throws InterruptedException {
        magicMessage = JupyterHandlerTest.initExecuteRequestMessage();
        magicMessage.getContent().put("code", MagicCommandWhichThrowsException.MAGIC_COMMAND_WHICH_THROWS_EXCEPTION);
        executeRequestHandler.handle(magicMessage);
        Optional<Message> message = EvaluatorResultTestWatcher.waitForErrorMessage(ExecuteRequestHandlerTest.kernel);
        assertThat(message).isPresent();
    }
}

