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
package com.twosigma.beakerx.handler;


import CompleteHandler.CURSOR_END;
import CompleteHandler.CURSOR_START;
import CompleteHandler.MATCHES;
import CompleteHandler.STATUS;
import com.twosigma.beakerx.KernelTest;
import com.twosigma.beakerx.evaluator.EvaluatorTest;
import com.twosigma.beakerx.kernel.msg.JupyterMessages;
import com.twosigma.beakerx.message.Header;
import com.twosigma.beakerx.message.Message;
import java.io.Serializable;
import java.util.Map;
import org.assertj.core.api.Assertions;
import org.junit.Test;


public class CompleteHandlerTest {
    private CompleteHandler completeHandler;

    private Message message;

    private static KernelTest kernel;

    private static EvaluatorTest evaluator;

    @Test
    public void handle_shouldSendMessage() throws Exception {
        // when
        completeHandler.handle(message);
        // then
        Assertions.assertThat(CompleteHandlerTest.kernel.getSentMessages()).isNotEmpty();
        Assertions.assertThat(CompleteHandlerTest.kernel.getSentMessages().get(0)).isNotNull();
    }

    @Test
    public void handle_sentMessageHasHeaderTypeIsCompleteReply() throws Exception {
        // when
        completeHandler.handle(message);
        // then
        Header header = CompleteHandlerTest.kernel.getSentMessages().get(0).getHeader();
        Assertions.assertThat(header).isNotNull();
        Assertions.assertThat(header.getType()).isEqualTo(JupyterMessages.COMPLETE_REPLY.getName());
    }

    @Test
    public void handle_messageContentHasCursorStartAndEndFields() throws Exception {
        // when
        completeHandler.handle(message);
        // then
        Map<String, Serializable> content = CompleteHandlerTest.kernel.getSentMessages().get(0).getContent();
        Assertions.assertThat(content.get(CURSOR_START)).isNotNull();
        Assertions.assertThat(content.get(CURSOR_END)).isNotNull();
    }

    @Test
    public void handle_messageContentHasMatchesField() throws Exception {
        // when
        completeHandler.handle(message);
        // then
        Map<String, Serializable> content = CompleteHandlerTest.kernel.getSentMessages().get(0).getContent();
        Assertions.assertThat(content.get(MATCHES)).isNotNull();
    }

    @Test
    public void handle_messageContentHasStatus() throws Exception {
        // when
        completeHandler.handle(message);
        // then
        Map<String, Serializable> content = CompleteHandlerTest.kernel.getSentMessages().get(0).getContent();
        Assertions.assertThat(content.get(STATUS)).isNotNull();
    }
}

