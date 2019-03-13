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
package com.twosigma.beakerx.javash.autotranslation;


import NamespaceClientTest.AutotranslationServiceTestImpl;
import com.twosigma.beakerx.KernelSetUpFixtureTest;
import com.twosigma.beakerx.MessageFactoryTest;
import com.twosigma.beakerx.NamespaceClient;
import com.twosigma.beakerx.message.Message;
import java.util.Optional;
import org.junit.Test;


public class JavaAutotranslationTest extends KernelSetUpFixtureTest {
    private AutotranslationServiceTestImpl autotranslationService;

    @Test
    public void transformFromJsonToJavaObject() throws Exception {
        // given
        autotranslationService.update("foo", "\"Hello\"");
        // when
        String code = ("return " + (NamespaceClient.NAMESPACE_CLIENT)) + ".get(\"foo\");";
        Message message = MessageFactoryTest.getExecuteRequestMessage(code);
        kernelSocketsService.handleMsg(message);
        // then
        Optional<Message> idleMessage = waitForIdleMessage(kernelSocketsService.getKernelSockets());
        assertThat(idleMessage).isPresent();
        Optional<Message> result = waitForResult(kernelSocketsService.getKernelSockets());
        verifyJavaObject(result.get());
    }
}

