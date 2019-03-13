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
package com.twosigma.beakerx.kernel;


import com.twosigma.beakerx.KernelTest;
import com.twosigma.beakerx.MessageFactorTest;
import com.twosigma.beakerx.evaluator.EvaluatorTest;
import com.twosigma.beakerx.kernel.magic.command.CodeFactory;
import com.twosigma.beakerx.kernel.magic.command.MagicCommand;
import com.twosigma.beakerx.kernel.magic.command.functionality.ClasspathAddJarMagicCommand;
import com.twosigma.beakerx.kernel.magic.command.functionality.JavaScriptMagicCommand;
import org.junit.Test;


public class CodeTest {
    private static KernelTest kernel;

    private static EvaluatorTest evaluator;

    @Test
    public void shouldReadJavaScriptCommand() {
        // give
        String jsCode = "require.config({\n" + (("  paths: {\n" + "      d3: \'//cdnjs.cloudflare.com/ajax/libs/d3/3.4.8/d3.min\'\n") + "  }});");
        // when
        Code code = CodeFactory.create((((JavaScriptMagicCommand.JAVASCRIPT) + "\n") + jsCode), MessageFactorTest.commMsg(), CodeTest.kernel);
        // then
        assertThat(code.getCodeFrames().size()).isEqualTo(1);
        MagicCommand magicCommand = ((MagicCommand) (code.getCodeFrames().get(0)));
        assertThat(magicCommand.getCommand()).isEqualTo(JavaScriptMagicCommand.JAVASCRIPT);
        String toCompare = magicCommand.getCommandCodeBlock().replaceAll("\\s+", "");
        jsCode = jsCode.replaceAll("\\s+", "");
        assertThat(toCompare).isEqualTo(jsCode);
        // assertThat(result.takeCodeWithoutCommand()).isEqualTo(new Code(jsCode));
    }

    @Test
    public void shouldReadAllMagicCommands() throws Exception {
        // give
        String allCode = (((((("" + (ClasspathAddJarMagicCommand.CLASSPATH_ADD_JAR)) + " lib1.jar\n") + (ClasspathAddJarMagicCommand.CLASSPATH_ADD_JAR)) + " lib2.jar\n") + (ClasspathAddJarMagicCommand.CLASSPATH_ADD_JAR)) + " lib3.jar\n") + "code code code";
        // when
        Code code = CodeFactory.create(allCode, MessageFactorTest.commMsg(), CodeTest.kernel);
        // then
        assertThat(code.getCodeFrames().size()).isEqualTo(4);
        assertThat(getCommand()).isEqualTo("%classpath add jar lib1.jar");
        assertThat(getCommand()).isEqualTo("%classpath add jar lib2.jar");
        assertThat(getCommand()).isEqualTo("%classpath add jar lib3.jar");
        assertThat(getPlainCode()).isEqualTo("code code code");
    }
}

