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


import JupyterMessages.COMM_MSG;
import com.twosigma.beakerx.KernelTest;
import com.twosigma.beakerx.kernel.handler.CommCloseHandler;
import com.twosigma.beakerx.kernel.handler.CommInfoHandler;
import com.twosigma.beakerx.kernel.handler.CommMsgHandler;
import com.twosigma.beakerx.kernel.handler.CommOpenHandler;
import com.twosigma.beakerx.message.Message;
import java.util.Map;
import org.assertj.core.api.Assertions;
import org.junit.Test;


public class JupyterHandlerTest {
    private static KernelTest kernel;

    private CommOpenHandler commOpenHandler;

    private CommCloseHandler commCloseHandler;

    private CommInfoHandler commInfoHandler;

    private CommMsgHandler commMsgHandler;

    @Test
    public void handleOpenCommMessage_shouldAddCommMessageToStorageMap() throws Exception {
        // given
        Message message = JupyterHandlerTest.initCloseMessage();
        String commId = ((String) (message.getContent().get(COMM_ID)));
        // when
        commOpenHandler.handle(message);
        // then
        Assertions.assertThat(JupyterHandlerTest.kernel.isCommPresent(commId)).isTrue();
    }

    @Test
    public void handleCloseCommMessage_shouldRemoveCommMessageFromStorageMap() throws Exception {
        // given
        String commId = JupyterHandlerTest.initKernelCommMapWithOneComm(JupyterHandlerTest.kernel);
        // when
        commCloseHandler.handle(JupyterHandlerTest.initCloseMessage());
        // then
        Assertions.assertThat(JupyterHandlerTest.kernel.isCommPresent(commId)).isFalse();
    }

    @Test
    public void handleOpenThenCloseCommMessages_shouldRemoveCommMessageFromStorageMap() throws Exception {
        // given
        Message openMessage = JupyterHandlerTest.initOpenMessage();
        String commId = ((String) (openMessage.getContent().get(COMM_ID)));
        // when
        commOpenHandler.handle(openMessage);
        commCloseHandler.handle(JupyterHandlerTest.initCloseMessage());
        // then
        Assertions.assertThat(JupyterHandlerTest.kernel.isCommPresent(commId)).isFalse();
    }

    @Test
    public void handleInfoCommMessages_replyCommMessageHasCommsInfoContent() throws Exception {
        // given
        JupyterHandlerTest.initKernelCommMapWithOneComm(JupyterHandlerTest.kernel);
        // when
        commInfoHandler.handle(JupyterHandlerTest.initInfoMessage());
        // then
        Assertions.assertThat(JupyterHandlerTest.kernel.getSentMessages()).isNotEmpty();
        Message sendMessage = JupyterHandlerTest.kernel.getSentMessages().get(0);
        Assertions.assertThat(((Map) (sendMessage.getContent().get(COMMS)))).isNotEmpty();
    }

    @Test
    public void commInfoHandlerHandleEmptyMessage_dontThrowNullPointerException() throws Exception {
        // given
        Message message = new Message(JupyterHandlerTest.initHeader(COMM_MSG));
        // when
        commInfoHandler.handle(message);
    }

    @Test
    public void commOpenHandlerHandleEmptyMessage_dontThrowNullPointerException() throws Exception {
        // given
        Message message = new Message(JupyterHandlerTest.initHeader(COMM_MSG));
        JupyterHandlerTest.initMessage(message);
        // wnen
        commOpenHandler.handle(message);
    }

    @Test
    public void commMsgHandlerHandleEmptyMessage_dontThrowNullPointerException() throws Exception {
        // given
        Message message = new Message(JupyterHandlerTest.initHeader(COMM_MSG));
        JupyterHandlerTest.initMessage(message);
        // when
        commMsgHandler.handle(message);
    }

    @Test
    public void commCloseHandlerHandleEmptyMessage_dontThrowNullPointerException() throws Exception {
        // given
        Message message = new Message(JupyterHandlerTest.initHeader(COMM_MSG));
        JupyterHandlerTest.initMessage(message);
        // when
        commCloseHandler.handle(message);
    }
}

