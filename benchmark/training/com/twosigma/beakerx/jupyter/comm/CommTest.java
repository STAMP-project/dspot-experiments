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
package com.twosigma.beakerx.jupyter.comm;


import Comm.Buffer.EMPTY;
import Comm.COMM_ID;
import Comm.DATA;
import Comm.TARGET_MODULE;
import Comm.TARGET_NAME;
import JupyterMessages.COMM_CLOSE;
import JupyterMessages.COMM_MSG;
import JupyterMessages.COMM_OPEN;
import com.twosigma.beakerx.KernelTest;
import com.twosigma.beakerx.MessageFactorTest;
import com.twosigma.beakerx.kernel.comm.Comm;
import com.twosigma.beakerx.kernel.msg.JupyterMessages;
import com.twosigma.beakerx.message.Message;
import java.security.NoSuchAlgorithmException;
import org.junit.Test;


public class CommTest {
    private KernelTest kernel;

    private Comm comm;

    @Test
    public void commCreatedWithParentMessageShouldAlwaysSendHeaderFromThisParentMessage() throws NoSuchAlgorithmException {
        // given
        submitCodeToExecution();
        // when
        Message parentMessage = MessageFactorTest.commMsg();
        comm.open(parentMessage);
        assertThat(kernel.getPublishedMessages().get(0).getParentHeader()).isEqualTo(parentMessage.getHeader());
        kernel.clearPublishedMessages();
        // then
        comm.send(JupyterMessages.COMM_MSG, EMPTY, comm.getData());
        assertThat(kernel.getPublishedMessages().get(0).getParentHeader()).isEqualTo(parentMessage.getHeader());
    }

    @Test
    public void commCreatedWithoutParentMessageShouldAlwaysSendHeaderFromMessageGivenFromInternalVariable() throws NoSuchAlgorithmException {
        // code from first execution
        Message message1 = submitCodeToExecution();
        comm.open();
        assertThat(kernel.getPublishedMessages().get(0).getParentHeader()).isEqualTo(message1.getHeader());
        kernel.clearPublishedMessages();
        // code from second execution
        Message message2 = submitCodeToExecution();
        comm.send(JupyterMessages.COMM_MSG, EMPTY, comm.getData());
        assertThat(kernel.getPublishedMessages().get(0).getParentHeader()).isEqualTo(message2.getHeader());
    }

    @Test
    public void commOpen_shouldSendIOPubSocketMessage() throws NoSuchAlgorithmException {
        // when
        comm.open();
        // then
        assertThat(kernel.getPublishedMessages()).isNotEmpty();
        assertThat(kernel.getPublishedMessages().get(0)).isNotNull();
    }

    @Test
    public void commOpen_shouldAddCommToStorageMap() throws NoSuchAlgorithmException {
        // when
        comm.open();
        // then
        assertThat(kernel.isCommPresent(comm.getCommId())).isTrue();
    }

    @Test
    public void commOpen_sentMessageHasTypeIsCommOpen() throws NoSuchAlgorithmException {
        // when
        comm.open();
        // then
        assertThat(kernel.getPublishedMessages()).isNotEmpty();
        Message sendMessage = kernel.getPublishedMessages().get(0);
        assertThat(sendMessage.getHeader().getType()).isEqualTo(COMM_OPEN.getName());
    }

    @Test
    public void commOpen_sentMessageHasCommId() throws NoSuchAlgorithmException {
        // when
        comm.open();
        // then
        assertThat(kernel.getPublishedMessages()).isNotEmpty();
        Message sendMessage = kernel.getPublishedMessages().get(0);
        assertThat(((String) (sendMessage.getContent().get(COMM_ID)))).isNotEmpty();
    }

    @Test
    public void commOpen_sentMessageHasTargetName() throws NoSuchAlgorithmException {
        // when
        comm.open();
        // then
        assertThat(kernel.getPublishedMessages()).isNotEmpty();
        Message sendMessage = kernel.getPublishedMessages().get(0);
        assertThat(((String) (sendMessage.getContent().get(TARGET_NAME)))).isNotEmpty();
    }

    @Test
    public void commOpen_sentMessageHasData() throws NoSuchAlgorithmException {
        initCommData(comm);
        // when
        comm.open();
        // then
        assertThat(kernel.getPublishedMessages()).isNotEmpty();
        Message sendMessage = kernel.getPublishedMessages().get(0);
        assertThat(((java.util.Map) (sendMessage.getContent().get(DATA)))).isNotEmpty();
    }

    @Test
    public void commOpen_sentMessageHasTargetModule() throws NoSuchAlgorithmException {
        // given
        comm.setTargetModule("targetModuleName");
        // when
        comm.open();
        // then
        assertThat(kernel.getPublishedMessages()).isNotEmpty();
        Message sendMessage = kernel.getPublishedMessages().get(0);
        assertThat(((String) (sendMessage.getContent().get(TARGET_MODULE)))).isNotEmpty();
    }

    @Test
    public void commClose_shouldSendIOPubSocketMessage() throws NoSuchAlgorithmException {
        // when
        comm.close();
        // then
        assertThat(kernel.getPublishedMessages()).isNotEmpty();
        assertThat(kernel.getPublishedMessages().get(0)).isNotNull();
    }

    @Test
    public void commClose_shouldRemoveCommFromStorageMap() throws NoSuchAlgorithmException {
        // when
        comm.close();
        // then
        assertThat(kernel.isCommPresent(comm.getCommId())).isFalse();
    }

    @Test
    public void commClose_sentMessageHasTypeIsCommClose() throws NoSuchAlgorithmException {
        // when
        comm.close();
        // then
        assertThat(kernel.getPublishedMessages()).isNotEmpty();
        Message sendMessage = kernel.getPublishedMessages().get(0);
        assertThat(sendMessage.getHeader().getType()).isEqualTo(COMM_CLOSE.getName());
    }

    @Test
    public void commClose_sentMessageHasEmptyData() throws NoSuchAlgorithmException {
        initCommData(comm);
        // when
        comm.close();
        // then
        assertThat(kernel.getPublishedMessages()).isNotEmpty();
        Message sendMessage = kernel.getPublishedMessages().get(0);
        assertThat(((java.util.Map) (sendMessage.getContent().get(DATA)))).isEmpty();
    }

    @Test
    public void commSend_shouldSendIOPubSocketMessage() throws NoSuchAlgorithmException {
        // when
        comm.send(JupyterMessages.COMM_MSG, EMPTY, comm.getData());
        // then
        assertThat(kernel.getPublishedMessages()).isNotEmpty();
        assertThat(kernel.getPublishedMessages().get(0)).isNotNull();
    }

    @Test
    public void commSend_sentMessageHasTypeIsCommClose() throws NoSuchAlgorithmException {
        // when
        comm.send(JupyterMessages.COMM_MSG, EMPTY, comm.getData());
        // then
        assertThat(kernel.getPublishedMessages()).isNotEmpty();
        Message sendMessage = kernel.getPublishedMessages().get(0);
        assertThat(sendMessage.getHeader().getType()).isEqualTo(COMM_MSG.getName());
    }

    @Test
    public void commSend_sentMessageHasCommId() throws NoSuchAlgorithmException {
        // when
        comm.send(JupyterMessages.COMM_MSG, EMPTY, comm.getData());
        // then
        assertThat(kernel.getPublishedMessages()).isNotEmpty();
        Message sendMessage = kernel.getPublishedMessages().get(0);
        assertThat(((String) (sendMessage.getContent().get(COMM_ID)))).isNotEmpty();
    }

    @Test
    public void commClose_sentMessageHasData() throws NoSuchAlgorithmException {
        initCommData(comm);
        // when
        comm.send(JupyterMessages.COMM_MSG, EMPTY, comm.getData());
        // then
        assertThat(kernel.getPublishedMessages()).isNotEmpty();
        Message sendMessage = kernel.getPublishedMessages().get(0);
        assertThat(((java.util.Map) (sendMessage.getContent().get(DATA)))).isNotEmpty();
    }
}

