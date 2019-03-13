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
package com.twosigma.beakerx.groovy.kernel;


import com.twosigma.beakerx.KernelExecutionTest;
import com.twosigma.beakerx.MessageFactorTest;
import com.twosigma.beakerx.MessageFactoryTest;
import com.twosigma.beakerx.kernel.Code;
import com.twosigma.beakerx.kernel.magic.command.CodeFactory;
import com.twosigma.beakerx.message.Message;
import com.twosigma.beakerx.widget.OutputManager;
import java.util.Optional;
import org.junit.Test;


public class GroovyKernelTest extends KernelExecutionTest {
    @Test
    public void evaluateBooleanArray() throws Exception {
        // given
        String code = "new boolean[3];";
        Message message = MessageFactoryTest.getExecuteRequestMessage(code);
        // when
        kernelSocketsService.handleMsg(message);
        // then
        Optional<Message> idleMessage = waitForIdleMessage(kernelSocketsService.getKernelSockets());
        assertThat(idleMessage).isPresent();
        Optional<Message> result = waitForResult(kernelSocketsService.getKernelSockets());
        verifyResult(result.get());
    }

    @Test
    public void outputWidget() throws Exception {
        // given
        String outputCommId = "outputCommId";
        addOutputWidget(outputCommId);
        // when
        String println = "" + ("println(\"Hello 1\")\n" + "2+5");
        evaluateCode(println);
        simulateSendingUpdateMessageFromUI((outputCommId + "1"));
        // then
        verifyOutputWidgetResult();
        verifyIfStreamMsgIsEarlierThanResult();
        OutputManager.setOutput(null);
    }

    @Test
    public void shouldImportStaticWildcardDemoClassByMagicCommand() throws Exception {
        // given
        addDemoJar();
        String path = (pathToDemoClassFromAddedDemoJar()) + ".*";
        // when
        Code code = CodeFactory.create((((ADD_STATIC_IMPORT) + " ") + path), MessageFactorTest.commMsg(), getKernel());
        code.execute(kernel, 1);
        // then
        verifyStaticImportedDemoClassByMagicCommand(((pathToDemoClassFromAddedDemoJar()) + ".staticTest()"));
        verifyStaticImportedDemoClassByMagicCommand(((pathToDemoClassFromAddedDemoJar()) + ".STATIC_TEST_123"));
    }

    @Test
    public void shouldImportStaticMethodDemoClassByMagicCommand() throws Exception {
        // given
        addDemoJar();
        String path = (pathToDemoClassFromAddedDemoJar()) + ".staticTest";
        // when
        Code code = CodeFactory.create((((ADD_STATIC_IMPORT) + " ") + path), MessageFactorTest.commMsg(), getKernel());
        code.execute(kernel, 1);
        // then
        verifyStaticImportedDemoClassByMagicCommand(((pathToDemoClassFromAddedDemoJar()) + ".staticTest()"));
    }

    @Test
    public void shouldImportStaticFieldDemoClassByMagicCommand() throws Exception {
        // given
        addDemoJar();
        String path = (pathToDemoClassFromAddedDemoJar()) + ".STATIC_TEST_123";
        // when
        Code code = CodeFactory.create((((ADD_STATIC_IMPORT) + " ") + path), MessageFactorTest.commMsg(), getKernel());
        code.execute(kernel, 1);
        // then
        verifyStaticImportedDemoClassByMagicCommand(((pathToDemoClassFromAddedDemoJar()) + ".STATIC_TEST_123"));
    }

    @Test
    public void shouldNotImportStaticUnknownClassByMagicCommand() throws Exception {
        // given
        String allCode = (((ADD_STATIC_IMPORT) + " ") + (pathToDemoClassFromAddedDemoJar())) + "UnknownClass";
        // when
        Code code = CodeFactory.create(allCode, MessageFactorTest.commMsg(), getKernel());
        code.execute(kernel, 1);
        // then
        verifyNotImportedStaticMagicCommand();
    }

    @Test
    public void shouldNotImportStaticUnknownFieldDemoClassByMagicCommand() throws Exception {
        // given
        addDemoJar();
        String path = (pathToDemoClassFromAddedDemoJar()) + ".STATIC_TEST_123_unknown";
        // when
        Code code = CodeFactory.create((((ADD_STATIC_IMPORT) + " ") + path), MessageFactorTest.commMsg(), getKernel());
        code.execute(kernel, 1);
        // then
        verifyNotImportedStaticMagicCommand();
    }

    @Test
    public void shouldNotImportStaticUnknownMethodDemoClassByMagicCommand() throws Exception {
        // given
        addDemoJar();
        String path = (pathToDemoClassFromAddedDemoJar()) + ".staticTest_unknown";
        // when
        Code code = CodeFactory.create((((ADD_STATIC_IMPORT) + " ") + path), MessageFactorTest.commMsg(), getKernel());
        code.execute(kernel, 1);
        // then
        verifyNotImportedStaticMagicCommand();
    }

    @Test
    public void shouldNotImportStaticNotStaticPathByMagicCommand() throws Exception {
        // given
        addDemoJar();
        String path = "garbage";
        // when
        Code code = CodeFactory.create((((ADD_STATIC_IMPORT) + " ") + path), MessageFactorTest.commMsg(), getKernel());
        code.execute(kernel, 1);
        // then
        verifyNotImportedStaticMagicCommand();
    }
}

