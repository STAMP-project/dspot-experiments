/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.zeppelin.lens;


import InterpreterResult.Type.TEXT;
import java.util.Properties;
import org.apache.zeppelin.interpreter.InterpreterResult;
import org.junit.Assert;
import org.junit.Test;


/**
 * Lens interpreter unit tests.
 */
public class LensInterpreterTest {
    @Test
    public void test() {
        Properties prop = new Properties();
        prop.setProperty(LensInterpreter.LENS_SERVER_URL, "http://127.0.0.1:9999/lensapi");
        prop.setProperty(LensInterpreter.LENS_CLIENT_DBNAME, "default");
        prop.setProperty(LensInterpreter.LENS_PERSIST_RESULTSET, "false");
        prop.setProperty(LensInterpreter.LENS_SESSION_CLUSTER_USER, "default");
        prop.setProperty(LensInterpreter.ZEPPELIN_MAX_ROWS, "1000");
        prop.setProperty(LensInterpreter.ZEPPELIN_LENS_RUN_CONCURRENT_SESSION, "true");
        prop.setProperty(LensInterpreter.ZEPPELIN_LENS_CONCURRENT_SESSIONS, "10");
        LensInterpreter t = new LensInterpreterTest.MockLensInterpreter(prop);
        t.open();
        // simple help test
        InterpreterResult result = t.interpret("help", null);
        Assert.assertEquals(result.message().get(0).getType(), TEXT);
        // assertEquals("unable to find 'query execute' in help message",
        // result.message().contains("query execute"), result.message());
        t.close();
    }

    class MockLensInterpreter extends LensInterpreter {
        MockLensInterpreter(Properties property) {
            super(property);
        }

        @Override
        public void open() {
            init();
        }
    }
}

