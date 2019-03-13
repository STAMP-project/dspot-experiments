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
package com.twosigma.beakerx.kernel.magic.command;


import com.twosigma.beakerx.KernelTest;
import com.twosigma.beakerx.MessageFactorTest;
import com.twosigma.beakerx.evaluator.EvaluatorResultTestWatcher;
import com.twosigma.beakerx.kernel.Code;
import com.twosigma.beakerx.kernel.PlainCode;
import com.twosigma.beakerx.message.Message;
import java.util.List;
import org.junit.Test;


public class MagicCommandResultOrderTest {
    public static final String DEMO_JAR = "demo.jar";

    public static final String DOC_CONTENTS_DEMO_RESOURCES_BEAKERX_TEST_LIBRARY_JAR = "../../doc/resources/jar/" + (MagicCommandResultOrderTest.DEMO_JAR);

    private KernelTest kernel;

    @Test
    public void codeResultShouldBeLast() {
        // given
        String allCode = (((("" + "%classpath add jar ") + (MagicCommandResultOrderTest.DOC_CONTENTS_DEMO_RESOURCES_BEAKERX_TEST_LIBRARY_JAR)) + "\n") + "%classpath\n") + "code code code";
        // when
        Code code = CodeFactory.create(allCode, MessageFactorTest.commMsg(), kernel);
        code.execute(kernel, 1);
        // then
        PlainCode actual = ((PlainCode) (code.getCodeFrames().get(2)));
        assertThat(actual.getPlainCode()).isEqualTo("code code code");
    }

    @Test
    public void classpathAddJarShouldBeLast() {
        // given
        String allCode = (("" + ("%classpath\n" + "%classpath add jar ")) + (MagicCommandResultOrderTest.DOC_CONTENTS_DEMO_RESOURCES_BEAKERX_TEST_LIBRARY_JAR)) + "\n";
        Code code = CodeFactory.create(allCode, MessageFactorTest.commMsg(), kernel);
        // when
        code.execute(kernel, 1);
        // then
        MagicCommand actual = ((MagicCommand) (code.getCodeFrames().get(1)));
        assertThat(actual.getCommand()).contains("%classpath add jar");
    }

    @Test
    public void classpathShouldBeLast() {
        // given
        String allCode = ((("" + "%classpath add jar ") + (MagicCommandResultOrderTest.DOC_CONTENTS_DEMO_RESOURCES_BEAKERX_TEST_LIBRARY_JAR)) + "\n") + "%classpath";
        Code code = CodeFactory.create(allCode, MessageFactorTest.commMsg(), kernel);
        // when
        code.execute(kernel, 1);
        // then
        List<Message> std = EvaluatorResultTestWatcher.getStdouts(kernel.getPublishedMessages());
        String text = ((String) (std.get(0).getContent().get("text")));
        assertThat(text).contains(MagicCommandResultOrderTest.DEMO_JAR);
    }
}

