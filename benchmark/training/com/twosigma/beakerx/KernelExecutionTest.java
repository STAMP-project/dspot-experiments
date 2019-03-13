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
package com.twosigma.beakerx;


import com.twosigma.beakerx.evaluator.EvaluatorResultTestWatcher;
import com.twosigma.beakerx.kernel.Code;
import com.twosigma.beakerx.kernel.magic.command.CodeFactory;
import com.twosigma.beakerx.kernel.magic.command.functionality.AddImportMagicCommand;
import com.twosigma.beakerx.kernel.magic.command.functionality.UnImportMagicCommand;
import com.twosigma.beakerx.message.Message;
import java.util.List;
import java.util.Optional;
import org.junit.Test;


public abstract class KernelExecutionTest extends ClasspathManagerTest {
    public static final String DEMO_RESOURCES_JAR = "../../doc/resources/jar";

    public static final String DEMO_JAR_NAME = "demo.jar";

    public static final String DEMO_JAR = ((KernelExecutionTest.DEMO_RESOURCES_JAR) + "/") + (KernelExecutionTest.DEMO_JAR_NAME);

    public static final String LOAD_MAGIC_JAR_DEMO_JAR_NAME = "loadMagicJarDemo.jar";

    public static final String LOAD_MAGIC_DEMO_JAR = ((KernelExecutionTest.DEMO_RESOURCES_JAR) + "/") + (KernelExecutionTest.LOAD_MAGIC_JAR_DEMO_JAR_NAME);

    @Test
    public void evaluate16Divide2() throws Exception {
        // given
        String code = codeFor16Divide2();
        Message message = MessageFactoryTest.getExecuteRequestMessage(code);
        // when
        getKernelSocketsService().handleMsg(message);
        // then
        Optional<Message> idleMessage = EvaluatorResultTestWatcher.waitForIdleMessage(getKernelSocketsService().getKernelSockets());
        assertThat(idleMessage).isPresent();
        Optional<Message> result = EvaluatorResultTestWatcher.waitForResult(getKernelSocketsService().getKernelSockets());
        checkResultForErrors(result, code);
        assertThat(result).isPresent();
        verifyResult(result.get());
        verifyPublishedMsgs(getKernelSocketsService());
        EvaluatorResultTestWatcher.waitForSentMessage(getKernelSocketsService().getKernelSockets());
        verifySentMsgs(getKernelSocketsService());
    }

    @Test
    public void loadMagicCommand() throws Exception {
        // given
        addJarWithCustomMagicCommand();
        // when
        loadMagicCommandByClass();
        // then
        verifyLoadedMagicCommand();
    }

    @Test
    public void shouldImportFromAddedDemoJar() throws Exception {
        // given
        // when
        addDemoJar();
        // then
        verifyAddedDemoJar();
    }

    @Test
    public void shouldImportDemoClassByMagicCommand() throws Exception {
        // given
        addDemoJar();
        String path = pathToDemoClassFromAddedDemoJar();
        // when
        Code code = CodeFactory.create((((AddImportMagicCommand.IMPORT) + " ") + path), MessageFactorTest.commMsg(), getKernel());
        code.execute(kernel, 1);
        // then
        verifyImportedDemoClassByMagicCommand();
    }

    @Test
    public void shouldImportDemoClassWithWildcardByMagicCommand() throws Exception {
        // given
        addDemoJar();
        String path = pathToDemoClassFromAddedDemoJar();
        String allCode = (((AddImportMagicCommand.IMPORT) + " ") + (path.substring(0, path.lastIndexOf(".")))) + ".*";
        // when
        Code code = CodeFactory.create(allCode, MessageFactorTest.commMsg(), getKernel());
        code.execute(kernel, 1);
        // then
        verifyImportedDemoClassByMagicCommand();
    }

    @Test
    public void shouldNotImportClassesFromUnknownPackageWithWildcardByMagicCommand() throws Exception {
        // given
        String path = pathToDemoClassFromAddedDemoJar();
        String allCode = ((AddImportMagicCommand.IMPORT) + " ") + ((path.substring(0, path.lastIndexOf("."))) + "Unknown.*");
        addDemoJar();
        // when
        Code code = CodeFactory.create(allCode, MessageFactorTest.commMsg(), getKernel());
        code.execute(kernel, 1);
        // then
        List<Message> std = EvaluatorResultTestWatcher.waitForStderr(getKernelSocketsService().getKernelSockets());
        String text = ((String) (std.get(0).getContent().get("text")));
        assertThat(text).contains("Could not import");
    }

    @Test
    public void shouldNotImportUnknownClassByMagicCommand() throws Exception {
        // given
        String allCode = (((AddImportMagicCommand.IMPORT) + " ") + (pathToDemoClassFromAddedDemoJar())) + "UnknownClass";
        // when
        Code code = CodeFactory.create(allCode, MessageFactorTest.commMsg(), getKernel());
        code.execute(kernel, 1);
        // then
        List<Message> std = EvaluatorResultTestWatcher.waitForStderr(getKernelSocketsService().getKernelSockets());
        String text = ((String) (std.get(0).getContent().get("text")));
        assertThat(text).contains("Could not import");
    }

    @Test
    public void shouldUnimportDemoClassByMagicCommand() throws Exception {
        // given
        addDemoJar();
        String path = pathToDemoClassFromAddedDemoJar();
        Code code = CodeFactory.create((((AddImportMagicCommand.IMPORT) + " ") + path), MessageFactorTest.commMsg(), getKernel());
        code.execute(kernel, 1);
        // when
        Code code2 = CodeFactory.create((((UnImportMagicCommand.UNIMPORT) + " ") + path), MessageFactorTest.commMsg(), getKernel());
        code2.execute(kernel, 2);
        // then
        // assertThat(status).isEqualTo(MagicCommandOutcomeItem.Status.OK);
        verifyUnImportedDemoClassByMagicCommand();
    }
}

