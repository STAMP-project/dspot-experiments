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
package com.twosigma.beakerx.kernel.magic.command.functionality;


import com.twosigma.beakerx.KernelExecutionTest;
import com.twosigma.beakerx.KernelSetUpFixtureTest;
import com.twosigma.beakerx.jupyter.handler.JupyterHandlerTest;
import com.twosigma.beakerx.message.Message;
import org.junit.Test;


public abstract class ClasspathAddDynamicMagicCommandTest extends KernelSetUpFixtureTest {
    @Test
    public void handleDynamicMagics() throws InterruptedException {
        // given
        String code = ((((("" + (("a = true" + "\n") + "b = \"")) + (KernelExecutionTest.DEMO_JAR)) + "\"") + "\n") + "c = \"/tmp/dictC\"") + "\n";
        runCode(code);
        // when
        String magicCode = "%classpath add dynamic a ? b : c" + "\n";
        Message magicMessage = JupyterHandlerTest.createExecuteRequestMessage(magicCode);
        kernelSocketsService.handleMsg(magicMessage);
        // then
        verifyResult();
    }

    @Test
    public void shouldSupportList() throws InterruptedException {
        // given
        String code = ((((((("" + "location1 = \"") + (KernelExecutionTest.DEMO_JAR)) + "\"") + "\n") + "location2 = \"") + (KernelExecutionTest.LOAD_MAGIC_DEMO_JAR)) + "\"") + "\n";
        runCode(code);
        // when
        String magicCode = "%classpath add dynamic [location1, location2]" + "\n";
        Message magicMessage = JupyterHandlerTest.createExecuteRequestMessage(magicCode);
        kernelSocketsService.handleMsg(magicMessage);
        // then
        verifyList();
    }
}

