/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.drill.exec.compile;


import ClassCompilerSelector.CompilerPolicy.JANINO;
import ClassCompilerSelector.CompilerPolicy.JDK;
import ClassCompilerSelector.JAVA_COMPILER_DEBUG_OPTION;
import ClassCompilerSelector.JAVA_COMPILER_OPTION;
import java.io.IOException;
import org.apache.drill.exec.compile.ClassTransformer.ClassSet;
import org.apache.drill.exec.exception.ClassTransformationException;
import org.apache.drill.exec.expr.CodeGenerator;
import org.apache.drill.exec.server.options.SessionOptionManager;
import org.apache.drill.test.BaseTestQuery;
import org.codehaus.commons.compiler.CompileException;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestClassTransformation extends BaseTestQuery {
    private static final Logger logger = LoggerFactory.getLogger(TestClassTransformation.class);

    private static final int ITERATION_COUNT = Integer.valueOf(System.getProperty("TestClassTransformation.iteration", "1"));

    private static SessionOptionManager sessionOptions;

    @Test
    public void testJaninoClassCompiler() throws Exception {
        TestClassTransformation.logger.debug("Testing JaninoClassCompiler");
        TestClassTransformation.sessionOptions.setLocalOption(JAVA_COMPILER_OPTION, JANINO.name());
        for (int i = 0; i < (TestClassTransformation.ITERATION_COUNT); i++) {
            compilationInnerClass(false);// Traditional byte-code manipulation

            compilationInnerClass(true);// Plain-old Java

        }
    }

    @Test
    public void testJDKClassCompiler() throws Exception {
        TestClassTransformation.logger.debug("Testing JDKClassCompiler");
        TestClassTransformation.sessionOptions.setLocalOption(JAVA_COMPILER_OPTION, JDK.name());
        for (int i = 0; i < (TestClassTransformation.ITERATION_COUNT); i++) {
            compilationInnerClass(false);// Traditional byte-code manipulation

            compilationInnerClass(true);// Plain-old Java

        }
    }

    @Test
    public void testCompilationNoDebug() throws IOException, ClassNotFoundException, ClassTransformationException, CompileException {
        CodeGenerator<ExampleInner> cg = newCodeGenerator(ExampleInner.class, ExampleTemplateWithInner.class);
        ClassSet classSet = new ClassSet(null, cg.getDefinition().getTemplateClassName(), cg.getMaterializedClassName());
        String sourceCode = cg.generateAndGet();
        TestClassTransformation.sessionOptions.setLocalOption(JAVA_COMPILER_OPTION, JDK.name());
        TestClassTransformation.sessionOptions.setLocalOption(JAVA_COMPILER_DEBUG_OPTION, false);
        @SuppressWarnings("resource")
        QueryClassLoader loader = new QueryClassLoader(BaseTestQuery.config, TestClassTransformation.sessionOptions);
        final byte[][] codeWithoutDebug = loader.getClassByteCode(classSet.generated, sourceCode);
        loader.close();
        int sizeWithoutDebug = 0;
        for (byte[] bs : codeWithoutDebug) {
            sizeWithoutDebug += bs.length;
        }
        TestClassTransformation.sessionOptions.setLocalOption(JAVA_COMPILER_DEBUG_OPTION, true);
        loader = new QueryClassLoader(BaseTestQuery.config, TestClassTransformation.sessionOptions);
        final byte[][] codeWithDebug = loader.getClassByteCode(classSet.generated, sourceCode);
        loader.close();
        int sizeWithDebug = 0;
        for (byte[] bs : codeWithDebug) {
            sizeWithDebug += bs.length;
        }
        Assert.assertTrue("Debug code is smaller than optimized code!!!", (sizeWithDebug > sizeWithoutDebug));
        TestClassTransformation.logger.debug("Optimized code is {}% smaller than debug code.", ((int) (((sizeWithDebug - sizeWithoutDebug) / ((double) (sizeWithDebug))) * 100)));
    }
}

