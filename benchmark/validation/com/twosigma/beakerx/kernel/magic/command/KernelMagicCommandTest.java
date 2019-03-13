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
import com.twosigma.beakerx.evaluator.EvaluatorTest;
import com.twosigma.beakerx.kernel.Code;
import org.junit.Ignore;
import org.junit.Test;


@Ignore
public class KernelMagicCommandTest {
    private KernelTest kernel;

    private EvaluatorTest evaluator;

    @Test
    public void testPythonMagicCell() throws Exception {
        // given
        String pythonCode = ("%%python" + (System.lineSeparator())) + "print('Test')";
        Code code = CodeFactory.create(pythonCode, MessageFactorTest.commMsg(), kernel);
        // when
        code.execute(this.kernel, 1);
        // then
        assertThat(kernel.getPublishedMessages().get(2).getContent().get("text")).isEqualTo("Test\n");
    }

    @Test
    public void testPythonCodeWithIndents() throws Exception {
        // given
        String pythonCodeWIndents = (((((("%%python" + (System.lineSeparator())) + "def foo():") + (System.lineSeparator())) + "\tprint(\'Test2\')") + (System.lineSeparator())) + (System.lineSeparator())) + "foo()";
        Code code = CodeFactory.create(pythonCodeWIndents, MessageFactorTest.commMsg(), kernel);
        // when
        code.execute(this.kernel, 1);
        // then
        assertThat(kernel.getPublishedMessages().get(2).getContent().get("text")).isEqualTo("Test2\n");
    }

    @Test
    public void testJavaKernelAlias() throws Exception {
        // given
        String javaAliasCode = ("%%java" + (System.lineSeparator())) + "System.out.println(2+2);";
        Code code = CodeFactory.create(javaAliasCode, MessageFactorTest.commMsg(), kernel);
        // when
        code.execute(this.kernel, 1);
        // then
        assertThat(kernel.getPublishedMessages().get(2).getContent().get("text")).isEqualTo("4");
    }

    @Test
    public void testJavaKernel() throws Exception {
        // given
        String javaCode = ("%%kernel java" + (System.lineSeparator())) + "System.out.println(2+2);";
        Code code = CodeFactory.create(javaCode, MessageFactorTest.commMsg(), kernel);
        // when
        code.execute(this.kernel, 1);
        // then
        assertThat(kernel.getPublishedMessages().get(2).getContent().get("text")).isEqualTo("4");
    }

    @Test
    public void testKernelNotExist() throws Exception {
        // given
        String javaCode = ("%%kernel not_exist" + (System.lineSeparator())) + "System.out.println(2+2);";
        Code code = CodeFactory.create(javaCode, MessageFactorTest.commMsg(), kernel);
        // when
        code.execute(this.kernel, 1);
        // then
        assertThat(kernel.getPublishedMessages().get(0).getContent().get("text")).isEqualTo("Kernel not_exist is not available!\n");
    }
}

