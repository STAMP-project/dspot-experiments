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


import InterpreterResult.Code.ERROR;
import InterpreterResult.Code.SUCCESS;
import InterpreterResult.Type.HTML;
import java.util.Map;
import org.apache.zeppelin.interpreter.InterpreterContext;
import org.apache.zeppelin.interpreter.InterpreterException;
import org.apache.zeppelin.interpreter.InterpreterResult;
import org.apache.zeppelin.interpreter.remote.RemoteInterpreterEventClient;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class SparkRInterpreterTest {
    private SparkRInterpreter sparkRInterpreter;

    private SparkInterpreter sparkInterpreter;

    private RemoteInterpreterEventClient mockRemoteIntpEventClient = Mockito.mock(RemoteInterpreterEventClient.class);

    @Test
    public void testSparkRInterpreter() throws InterruptedException, InterpreterException {
        InterpreterResult result = sparkRInterpreter.interpret("1+1", getInterpreterContext());
        Assert.assertEquals(SUCCESS, result.code());
        Assert.assertTrue(result.message().get(0).getData().contains("2"));
        result = sparkRInterpreter.interpret("sparkR.version()", getInterpreterContext());
        Assert.assertEquals(SUCCESS, result.code());
        if (result.message().get(0).getData().contains("2.")) {
            // spark 2.x
            result = sparkRInterpreter.interpret("df <- as.DataFrame(faithful)\nhead(df)", getInterpreterContext());
            Assert.assertEquals(SUCCESS, result.code());
            Assert.assertTrue(result.message().get(0).getData().contains("eruptions waiting"));
            // spark job url is sent
            Mockito.verify(mockRemoteIntpEventClient, Mockito.atLeastOnce()).onParaInfosReceived(ArgumentMatchers.any(Map.class));
            // cancel
            final InterpreterContext context = getInterpreterContext();
            Thread thread = new Thread() {
                @Override
                public void run() {
                    try {
                        InterpreterResult result = sparkRInterpreter.interpret(("ldf <- dapplyCollect(\n" + ((((("         df,\n" + "         function(x) {\n") + "           Sys.sleep(3)\n") + "           x <- cbind(x, \"waiting_secs\" = x$waiting * 60)\n") + "         })\n") + "head(ldf, 3)")), context);
                        Assert.assertTrue(result.message().get(0).getData().contains("cancelled"));
                    } catch (InterpreterException e) {
                        Assert.fail("Should not throw InterpreterException");
                    }
                }
            };
            thread.setName("Cancel-Thread");
            thread.start();
            Thread.sleep(1000);
            sparkRInterpreter.cancel(context);
        } else {
            // spark 1.x
            result = sparkRInterpreter.interpret("df <- createDataFrame(sqlContext, faithful)\nhead(df)", getInterpreterContext());
            Assert.assertEquals(SUCCESS, result.code());
            Assert.assertTrue(result.message().get(0).getData().contains("eruptions waiting"));
            // spark job url is sent
            Mockito.verify(mockRemoteIntpEventClient, Mockito.atLeastOnce()).onParaInfosReceived(ArgumentMatchers.any(Map.class));
        }
        // plotting
        InterpreterContext context = getInterpreterContext();
        context.getLocalProperties().put("imageWidth", "100");
        result = sparkRInterpreter.interpret("hist(mtcars$mpg)", context);
        Assert.assertEquals(SUCCESS, result.code());
        Assert.assertEquals(1, result.message().size());
        Assert.assertEquals(HTML, result.message().get(0).getType());
        Assert.assertTrue(result.message().get(0).getData().contains("<img src="));
        Assert.assertTrue(result.message().get(0).getData().contains("width=\"100\""));
        result = sparkRInterpreter.interpret(("library(ggplot2)\n" + "ggplot(diamonds, aes(x=carat, y=price, color=cut)) + geom_point()"), getInterpreterContext());
        Assert.assertEquals(SUCCESS, result.code());
        Assert.assertEquals(1, result.message().size());
        Assert.assertEquals(HTML, result.message().get(0).getType());
        Assert.assertTrue(result.message().get(0).getData().contains("<img src="));
        // sparkr backend would be timeout after 10 seconds
        Thread.sleep((15 * 1000));
        result = sparkRInterpreter.interpret("1+1", getInterpreterContext());
        Assert.assertEquals(ERROR, result.code());
        Assert.assertTrue(result.message().get(0).getData().contains("sparkR backend is dead"));
    }
}

