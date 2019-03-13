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
package com.twosigma.beakerx.scala;


import NamespaceClientTest.AutotranslationServiceTestImpl;
import com.twosigma.beakerx.KernelSetUpFixtureTest;
import com.twosigma.beakerx.MessageFactoryTest;
import com.twosigma.beakerx.message.Message;
import java.util.Optional;
import org.junit.Test;


public class ScalaAutotranslationTest extends KernelSetUpFixtureTest {
    private AutotranslationServiceTestImpl autotranslationService;

    @Test
    public void transformFromJsonToScalaObject() throws Exception {
        // given
        autotranslationService.update("bar", list());
        // when
        String code = "beakerx.bar";
        Message message = MessageFactoryTest.getExecuteRequestMessage(code);
        kernelSocketsService.handleMsg(message);
        // then
        Optional<Message> idleMessage = waitForIdleMessage(kernelSocketsService.getKernelSockets());
        assertThat(idleMessage).isPresent();
        Optional<Message> result = waitForResult(kernelSocketsService.getKernelSockets());
        verifyScalaObject(result.get());
    }

    @Test
    public void transformFromJsonToScalaListHead() throws Exception {
        // given
        autotranslationService.update("bar", list());
        // when
        String code = "beakerx.bar.as[List[Any]].head";
        Message message = MessageFactoryTest.getExecuteRequestMessage(code);
        kernelSocketsService.handleMsg(message);
        // then
        Optional<Message> idleMessage = waitForIdleMessage(kernelSocketsService.getKernelSockets());
        assertThat(idleMessage).isPresent();
        Optional<Message> result = waitForResult(kernelSocketsService.getKernelSockets());
        verifyScalaListHead(result.get());
    }
}

