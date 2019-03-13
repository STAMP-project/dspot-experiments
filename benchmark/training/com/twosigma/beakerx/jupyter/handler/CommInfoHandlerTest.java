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


import JupyterMessages.COMM_INFO_REPLY;
import com.twosigma.beakerx.KernelTest;
import com.twosigma.beakerx.kernel.handler.CommInfoHandler;
import com.twosigma.beakerx.message.Message;
import org.assertj.core.api.Assertions;
import org.junit.Test;


public class CommInfoHandlerTest {
    private KernelTest kernel;

    private CommInfoHandler commInfoHandler;

    private Message message;

    @Test
    public void handleMessage_shouldSendShellSocketMessage() throws Exception {
        // when
        commInfoHandler.handle(message);
        // then
        Assertions.assertThat(kernel.getSentMessages()).isNotEmpty();
    }

    @Test
    public void handleMessage_sentMessageHasTypeIsCommInfoReply() throws Exception {
        // when
        commInfoHandler.handle(message);
        // then
        Assertions.assertThat(kernel.getSentMessages()).isNotEmpty();
        Message sendMessage = kernel.getSentMessages().get(0);
        Assertions.assertThat(sendMessage.getHeader().getType()).isEqualTo(COMM_INFO_REPLY.getName());
    }

    @Test
    public void handleMessage_sentMessageHasSessionId() throws Exception {
        // given
        String expectedSessionId = message.getHeader().getSession();
        // when
        commInfoHandler.handle(message);
        // then
        Assertions.assertThat(kernel.getSentMessages()).isNotEmpty();
        Message sendMessage = kernel.getSentMessages().get(0);
        Assertions.assertThat(sendMessage.getHeader().getSession()).isEqualTo(expectedSessionId);
    }

    @Test
    public void handleMessage_sentMessageHasParentHeader() throws Exception {
        // given
        String expectedHeader = message.getHeader().asJson();
        // when
        commInfoHandler.handle(message);
        // then
        Assertions.assertThat(kernel.getSentMessages()).isNotEmpty();
        Message sendMessage = kernel.getSentMessages().get(0);
        Assertions.assertThat(sendMessage.getParentHeader().asJson()).isEqualTo(expectedHeader);
    }

    @Test
    public void handleMessage_sentMessageHasIdentities() throws Exception {
        // given
        String expectedIdentities = new String(message.getIdentities().get(0));
        // when
        commInfoHandler.handle(message);
        // then
        Assertions.assertThat(kernel.getSentMessages()).isNotEmpty();
        Message sendMessage = kernel.getSentMessages().get(0);
        Assertions.assertThat(new String(sendMessage.getIdentities().get(0))).isEqualTo(expectedIdentities);
    }
}

