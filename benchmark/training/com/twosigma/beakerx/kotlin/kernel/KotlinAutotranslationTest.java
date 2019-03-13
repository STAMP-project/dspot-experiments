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
package com.twosigma.beakerx.kotlin.kernel;


import NamespaceClientTest.AutotranslationServiceTestImpl;
import com.twosigma.beakerx.KernelSetUpFixtureTest;
import com.twosigma.beakerx.MessageFactoryTest;
import com.twosigma.beakerx.message.Message;
import java.util.Optional;
import org.junit.Test;


public class KotlinAutotranslationTest extends KernelSetUpFixtureTest {
    private AutotranslationServiceTestImpl autotranslationService;

    @Test
    public void getAndSetAutotranslation() throws InterruptedException {
        // given
        String code = "beakerx[\"bar\"]  = mapOf(\"name\" to \"John\", \"lastName\" to \"Smith\", \"age\" to \"32\")\n" + "(beakerx[\"bar\"] as Map<String, String>)[\"name\"]";
        String value = "John";
        // when
        Message message = MessageFactoryTest.getExecuteRequestMessage(code);
        kernelSocketsService.handleMsg(message);
        // then
        Optional<Message> idleMessage = waitForIdleMessage(kernelSocketsService.getKernelSockets());
        assertThat(idleMessage).isPresent();
        Message result = waitForResult(kernelSocketsService.getKernelSockets()).get();
        verifyResultValue(result, value);
    }

    @Test
    public void kotlinAutotranslationSetValue() throws InterruptedException {
        // given
        String code = "beakerx[\"bar\"]  = mapOf(\"key\" to \"value\")";
        String serializedMap = "{\"type\":\"TableDisplay\",\"columnNames\":[\"Key\",\"Value\"],\"values\":[[\"key\",\"value\"]],\"subtype\":\"Dictionary\"}";
        // when
        Message message = MessageFactoryTest.getExecuteRequestMessage(code);
        kernelSocketsService.handleMsg(message);
        // then
        Optional<Message> idleMessage = waitForIdleMessage(kernelSocketsService.getKernelSockets());
        assertThat(idleMessage).isPresent();
        String result = autotranslationService.get("bar");
        assertThat(result).isEqualTo(serializedMap);
    }

    @Test
    public void kotlinAutotranslationGetValue() throws InterruptedException {
        // given
        String code = "(beakerx[\"bar\"] as Map<String, String>)[\"key\"]";
        String serializedMap = "{\"type\":\"TableDisplay\",\"columnNames\":[\"Key\",\"Value\"],\"values\":[[\"key\",\"value\"]],\"subtype\":\"Dictionary\"}";
        String value = "value";
        // when
        autotranslationService.update("bar", serializedMap);
        Message message = MessageFactoryTest.getExecuteRequestMessage(code);
        kernelSocketsService.handleMsg(message);
        // then
        Optional<Message> idleMessage = waitForIdleMessage(kernelSocketsService.getKernelSockets());
        assertThat(idleMessage).isPresent();
        Message result = waitForResult(kernelSocketsService.getKernelSockets()).get();
        verifyResultValue(result, value);
    }
}

