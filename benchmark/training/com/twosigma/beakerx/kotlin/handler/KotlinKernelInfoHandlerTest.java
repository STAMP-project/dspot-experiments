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
package com.twosigma.beakerx.kotlin.handler;


import com.twosigma.beakerx.kotlin.kernel.KotlinKernelMock;
import com.twosigma.beakerx.message.Header;
import com.twosigma.beakerx.message.Message;
import java.io.Serializable;
import java.util.Map;
import org.assertj.core.api.Assertions;
import org.junit.Test;


public class KotlinKernelInfoHandlerTest {
    private Message message;

    private static KotlinKernelMock kernel;

    private static KotlinKernelInfoHandler handler;

    @Test
    public void handle_shouldSendMessage() throws Exception {
        // when
        KotlinKernelInfoHandlerTest.handler.handle(message);
        // then
        Assertions.assertThat(getSentMessages()).isNotEmpty();
    }

    @Test
    public void handle_sentMessageHasContent() throws Exception {
        // when
        KotlinKernelInfoHandlerTest.handler.handle(message);
        // then
        Message sentMessage = getSentMessages().get(0);
        Assertions.assertThat(sentMessage.getContent()).isNotEmpty();
    }

    @Test
    public void handle_sentMessageHasHeaderTypeIsKernelInfoReply() throws Exception {
        // when
        KotlinKernelInfoHandlerTest.handler.handle(message);
        // then
        Message sentMessage = getSentMessages().get(0);
        Header header = sentMessage.getHeader();
        Assertions.assertThat(header).isNotNull();
        Assertions.assertThat(header.getType()).isEqualTo(KERNEL_INFO_REPLY.getName());
    }

    @Test
    public void handle_sentMessageHasLanguageInfo() throws Exception {
        // when
        KotlinKernelInfoHandlerTest.handler.handle(message);
        // then
        Message sentMessage = getSentMessages().get(0);
        Map<String, Serializable> map = sentMessage.getContent();
        Assertions.assertThat(map).isNotNull();
        Assertions.assertThat(map.get("language_info")).isNotNull();
    }

    @Test
    public void handle_messageContentHasKotlinLabel() throws Exception {
        // when
        KotlinKernelInfoHandlerTest.handler.handle(message);
        // then
        Message sentMessage = getSentMessages().get(0);
        Map<String, Serializable> map = sentMessage.getContent();
        Assertions.assertThat(map).isNotNull();
        Assertions.assertThat(map.get("implementation")).isEqualTo("kotlin");
    }
}

