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
package com.twosigma.beakerx.sql;


import com.twosigma.beakerx.KernelSetUpFixtureTest;
import com.twosigma.beakerx.MessageFactoryTest;
import com.twosigma.beakerx.message.Message;
import java.util.Optional;
import org.junit.Test;


public class SQLKernelTest extends KernelSetUpFixtureTest {
    @Test
    public void evaluate() throws Exception {
        // given
        Message message = MessageFactoryTest.getExecuteRequestMessage(SQLForColorTable.CREATE_AND_SELECT_ALL);
        // when
        kernelSocketsService.handleMsg(message);
        // then
        Optional<Message> idleMessage = waitForIdleMessage(kernelSocketsService.getKernelSockets());
        verifyIdleMessage(idleMessage);
        verifyResult();
        verifyPublishedMsgs(kernelSocketsService);
        waitForSentMessage(kernelSocketsService.getKernelSockets());
        verifySentMsgs(kernelSocketsService);
    }
}

