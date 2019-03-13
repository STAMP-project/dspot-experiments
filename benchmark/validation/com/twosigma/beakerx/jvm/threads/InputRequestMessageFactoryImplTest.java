/**
 * Copyright 2018 TWO SIGMA OPEN SOURCE, LLC
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
package com.twosigma.beakerx.jvm.threads;


import JupyterMessages.INPUT_REQUEST;
import com.twosigma.beakerx.kernel.msg.JupyterMessages;
import com.twosigma.beakerx.message.Message;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;


public class InputRequestMessageFactoryImplTest {
    private InputRequestMessageFactoryImpl inputRequestMessageFactory;

    @Test
    public void inputRequestMessage() {
        // given
        Message parent = new Message(new com.twosigma.beakerx.message.Header(JupyterMessages.EXECUTE_REQUEST, "session1"));
        List<byte[]> identities = Arrays.asList("MessageIdentities123".getBytes());
        parent.setIdentities(identities);
        // when
        Message message = inputRequestMessageFactory.create(parent);
        // then
        assertThat(message.getIdentities()).isEqualTo(identities);
        assertThat(message.type()).isEqualTo(INPUT_REQUEST);
    }
}

