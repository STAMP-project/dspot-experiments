/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.zeppelin.spark;


import InterpreterResult.Code.SUCCESS;
import Type.ANGULAR;
import Type.HTML;
import java.io.IOException;
import java.util.List;
import java.util.Properties;
import org.apache.zeppelin.display.AngularObjectRegistry;
import org.apache.zeppelin.interpreter.InterpreterContext;
import org.apache.zeppelin.interpreter.InterpreterException;
import org.apache.zeppelin.interpreter.InterpreterGroup;
import org.apache.zeppelin.interpreter.InterpreterResult;
import org.apache.zeppelin.interpreter.InterpreterResultMessage;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runners.MethodSorters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class PySparkInterpreterMatplotlibTest {
    @ClassRule
    public static TemporaryFolder tmpDir = new TemporaryFolder();

    static SparkInterpreter sparkInterpreter;

    static PySparkInterpreter pyspark;

    static InterpreterGroup intpGroup;

    static Logger LOGGER = LoggerFactory.getLogger(PySparkInterpreterTest.class);

    static InterpreterContext context;

    public static class AltPySparkInterpreter extends PySparkInterpreter {
        /**
         * Since pyspark output is  sent to an outputstream rather than
         * being directly provided by interpret(), this subclass is created to
         * override interpret() to append the result from the outputStream
         * for the sake of convenience in testing.
         */
        public AltPySparkInterpreter(Properties property) {
            super(property);
        }

        /**
         * This code is mainly copied from RemoteInterpreterServer.java which
         * normally handles this in real use cases.
         */
        @Override
        public InterpreterResult interpret(String st, InterpreterContext context) throws InterpreterException {
            context.out.clear();
            InterpreterResult result = super.interpret(st, context);
            List<InterpreterResultMessage> resultMessages = null;
            try {
                context.out.flush();
                resultMessages = context.out.toInterpreterResultMessage();
            } catch (IOException e) {
                e.printStackTrace();
            }
            resultMessages.addAll(result.message());
            return new InterpreterResult(result.code(), resultMessages);
        }
    }

    @Test
    public void dependenciesAreInstalled() throws InterpreterException {
        // matplotlib
        InterpreterResult ret = PySparkInterpreterMatplotlibTest.pyspark.interpret("import matplotlib", PySparkInterpreterMatplotlibTest.context);
        Assert.assertEquals(ret.message().toString(), SUCCESS, ret.code());
        // inline backend
        ret = PySparkInterpreterMatplotlibTest.pyspark.interpret("import backend_zinline", PySparkInterpreterMatplotlibTest.context);
        Assert.assertEquals(ret.message().toString(), SUCCESS, ret.code());
    }

    @Test
    public void showPlot() throws InterpreterException {
        // Simple plot test
        InterpreterResult ret;
        ret = PySparkInterpreterMatplotlibTest.pyspark.interpret("import matplotlib.pyplot as plt", PySparkInterpreterMatplotlibTest.context);
        ret = PySparkInterpreterMatplotlibTest.pyspark.interpret("plt.close()", PySparkInterpreterMatplotlibTest.context);
        ret = PySparkInterpreterMatplotlibTest.pyspark.interpret("z.configure_mpl(interactive=False)", PySparkInterpreterMatplotlibTest.context);
        ret = PySparkInterpreterMatplotlibTest.pyspark.interpret("plt.plot([1, 2, 3])", PySparkInterpreterMatplotlibTest.context);
        ret = PySparkInterpreterMatplotlibTest.pyspark.interpret("plt.show()", PySparkInterpreterMatplotlibTest.context);
        Assert.assertEquals(ret.message().toString(), SUCCESS, ret.code());
        Assert.assertEquals(ret.message().toString(), HTML, ret.message().get(0).getType());
        Assert.assertTrue(ret.message().get(0).getData().contains("data:image/png;base64"));
        Assert.assertTrue(ret.message().get(0).getData().contains("<div>"));
    }

    // Test for when configuration is set to auto-close figures after show().
    @Test
    public void testClose() throws InterpreterException {
        InterpreterResult ret;
        InterpreterResult ret1;
        InterpreterResult ret2;
        ret = PySparkInterpreterMatplotlibTest.pyspark.interpret("import matplotlib.pyplot as plt", PySparkInterpreterMatplotlibTest.context);
        ret = PySparkInterpreterMatplotlibTest.pyspark.interpret("plt.close()", PySparkInterpreterMatplotlibTest.context);
        ret = PySparkInterpreterMatplotlibTest.pyspark.interpret("z.configure_mpl(interactive=False, close=True, angular=False)", PySparkInterpreterMatplotlibTest.context);
        ret = PySparkInterpreterMatplotlibTest.pyspark.interpret("plt.plot([1, 2, 3])", PySparkInterpreterMatplotlibTest.context);
        ret1 = PySparkInterpreterMatplotlibTest.pyspark.interpret("plt.show()", PySparkInterpreterMatplotlibTest.context);
        // Second call to show() should print nothing, and Type should be TEXT.
        // This is because when close=True, there should be no living instances
        // of FigureManager, causing show() to return before setting the output
        // type to HTML.
        ret = PySparkInterpreterMatplotlibTest.pyspark.interpret("plt.show()", PySparkInterpreterMatplotlibTest.context);
        Assert.assertEquals(0, ret.message().size());
        // Now test that new plot is drawn. It should be identical to the
        // previous one.
        ret = PySparkInterpreterMatplotlibTest.pyspark.interpret("plt.plot([1, 2, 3])", PySparkInterpreterMatplotlibTest.context);
        ret2 = PySparkInterpreterMatplotlibTest.pyspark.interpret("plt.show()", PySparkInterpreterMatplotlibTest.context);
        Assert.assertEquals(ret1.message().get(0).getType(), ret2.message().get(0).getType());
        Assert.assertEquals(ret1.message().get(0).getData(), ret2.message().get(0).getData());
    }

    // Test for when configuration is set to not auto-close figures after show().
    @Test
    public void testNoClose() throws InterpreterException {
        InterpreterResult ret;
        InterpreterResult ret1;
        InterpreterResult ret2;
        ret = PySparkInterpreterMatplotlibTest.pyspark.interpret("import matplotlib.pyplot as plt", PySparkInterpreterMatplotlibTest.context);
        ret = PySparkInterpreterMatplotlibTest.pyspark.interpret("plt.close()", PySparkInterpreterMatplotlibTest.context);
        ret = PySparkInterpreterMatplotlibTest.pyspark.interpret("z.configure_mpl(interactive=False, close=False, angular=False)", PySparkInterpreterMatplotlibTest.context);
        ret = PySparkInterpreterMatplotlibTest.pyspark.interpret("plt.plot([1, 2, 3])", PySparkInterpreterMatplotlibTest.context);
        ret1 = PySparkInterpreterMatplotlibTest.pyspark.interpret("plt.show()", PySparkInterpreterMatplotlibTest.context);
        // Second call to show() should print nothing, and Type should be HTML.
        // This is because when close=False, there should be living instances
        // of FigureManager, causing show() to set the output
        // type to HTML even though the figure is inactive.
        ret = PySparkInterpreterMatplotlibTest.pyspark.interpret("plt.show()", PySparkInterpreterMatplotlibTest.context);
        Assert.assertEquals(ret.message().toString(), SUCCESS, ret.code());
        // Now test that plot can be reshown if it is updated. It should be
        // different from the previous one because it will plot the same line
        // again but in a different color.
        ret = PySparkInterpreterMatplotlibTest.pyspark.interpret("plt.plot([1, 2, 3])", PySparkInterpreterMatplotlibTest.context);
        ret2 = PySparkInterpreterMatplotlibTest.pyspark.interpret("plt.show()", PySparkInterpreterMatplotlibTest.context);
        Assert.assertNotSame(ret1.message().get(0).getData(), ret2.message().get(0).getData());
    }

    // Test angular mode
    @Test
    public void testAngular() throws InterpreterException {
        InterpreterResult ret;
        ret = PySparkInterpreterMatplotlibTest.pyspark.interpret("import matplotlib.pyplot as plt", PySparkInterpreterMatplotlibTest.context);
        ret = PySparkInterpreterMatplotlibTest.pyspark.interpret("plt.close()", PySparkInterpreterMatplotlibTest.context);
        ret = PySparkInterpreterMatplotlibTest.pyspark.interpret("z.configure_mpl(interactive=False, close=False, angular=True)", PySparkInterpreterMatplotlibTest.context);
        ret = PySparkInterpreterMatplotlibTest.pyspark.interpret("plt.plot([1, 2, 3])", PySparkInterpreterMatplotlibTest.context);
        ret = PySparkInterpreterMatplotlibTest.pyspark.interpret("plt.show()", PySparkInterpreterMatplotlibTest.context);
        Assert.assertEquals(ret.message().toString(), SUCCESS, ret.code());
        Assert.assertEquals(ret.message().toString(), ANGULAR, ret.message().get(0).getType());
        // Check if the figure data is in the Angular Object Registry
        AngularObjectRegistry registry = PySparkInterpreterMatplotlibTest.context.getAngularObjectRegistry();
        String figureData = registry.getAll("note", null).get(0).toString();
        Assert.assertTrue(figureData.contains("data:image/png;base64"));
    }
}

