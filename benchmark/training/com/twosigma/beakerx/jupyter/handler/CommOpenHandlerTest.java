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


import JupyterMessages.COMM_CLOSE;
import com.twosigma.beakerx.KernelTest;
import com.twosigma.beakerx.kernel.handler.CommOpenHandler;
import com.twosigma.beakerx.message.Message;
import java.util.Map;
import org.assertj.core.api.Assertions;
import org.junit.Test;


public class CommOpenHandlerTest {
    private KernelTest kernel;

    private CommOpenHandler commOpenHandler;

    private Message message;

    @Test
    public void handleMessage_shouldSendShellSocketMessage() throws Exception {
        // when
        commOpenHandler.handle(message);
        // then
        Assertions.assertThat(kernel.getSentMessages()).isNotEmpty();
    }

    @Test
    public void handleMessageWithoutCommId_shouldSendCloseCommMessage() throws Exception {
        // given
        message.getContent().remove(COMM_ID);
        // when
        commOpenHandler.handle(message);
        // then
        Assertions.assertThat(kernel.getSentMessages()).isNotEmpty();
        Message sendMessage = kernel.getSentMessages().get(0);
        Assertions.assertThat(sendMessage.getHeader().getType()).isEqualTo(COMM_CLOSE.getName());
    }

    @Test
    public void handleMessageWithoutTargetName_shouldSendCloseCommMessage() throws Exception {
        // given
        message.getContent().remove(TARGET_NAME);
        // when
        commOpenHandler.handle(message);
        // then
        Assertions.assertThat(kernel.getSentMessages()).isNotEmpty();
        Message sendMessage = kernel.getSentMessages().get(0);
        Assertions.assertThat(sendMessage.getHeader().getType()).isEqualTo(COMM_CLOSE.getName());
    }

    @Test
    public void handleMessage_sentMessageHasCommId() throws Exception {
        // given
        String expectedCommId = ((String) (message.getContent().get(COMM_ID)));
        // when
        commOpenHandler.handle(message);
        // then
        Assertions.assertThat(kernel.getSentMessages()).isNotEmpty();
        Message sendMessage = kernel.getSentMessages().get(0);
        Assertions.assertThat(sendMessage.getContent().get(COMM_ID)).isEqualTo(expectedCommId);
    }

    @Test
    public void handleMessage_sentMessageHasTargetName() throws Exception {
        // given
        String expectedTargetName = ((String) (message.getContent().get(TARGET_NAME)));
        // when
        commOpenHandler.handle(message);
        // then
        Assertions.assertThat(kernel.getSentMessages()).isNotEmpty();
        Message sendMessage = kernel.getSentMessages().get(0);
        Assertions.assertThat(sendMessage.getContent().get(TARGET_NAME)).isEqualTo(expectedTargetName);
    }

    @Test
    public void handleMessage_sentMessageHasEmptyData() throws Exception {
        // when
        commOpenHandler.handle(message);
        // then
        Assertions.assertThat(kernel.getSentMessages()).isNotEmpty();
        Message sendMessage = kernel.getSentMessages().get(0);
        Assertions.assertThat(((Map) (sendMessage.getContent().get(DATA)))).isEmpty();
    }

    @Test
    public void handleMessage_sentMessageHasTargetModule() throws Exception {
        // given
        String expectedTargetModule = ((String) (message.getContent().get(TARGET_MODULE)));
        // when
        commOpenHandler.handle(message);
        // then
        Assertions.assertThat(kernel.getSentMessages()).isNotEmpty();
        Message sendMessage = kernel.getSentMessages().get(0);
        Assertions.assertThat(sendMessage.getContent().get(TARGET_MODULE)).isEqualTo(expectedTargetModule);
    }

    @Test
    public void handleMessage_sentMessageHasParentHeader() throws Exception {
        // given
        String expectedHeader = message.getHeader().asJson();
        // when
        commOpenHandler.handle(message);
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
        commOpenHandler.handle(message);
        // then
        Assertions.assertThat(kernel.getSentMessages()).isNotEmpty();
        Message sendMessage = kernel.getSentMessages().get(0);
        Assertions.assertThat(new String(sendMessage.getIdentities().get(0))).isEqualTo(expectedIdentities);
    }
}

