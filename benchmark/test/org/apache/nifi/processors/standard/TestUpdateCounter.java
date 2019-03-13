/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.nifi.processors.standard;


import UpdateCounter.COUNTER_NAME;
import UpdateCounter.DELTA;
import UpdateCounter.SUCCESS;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.apache.nifi.util.TestRunner;
import org.apache.nifi.util.TestRunners;
import org.junit.Assert;
import org.junit.Test;


public class TestUpdateCounter {
    @Test
    public void testwithFileName() throws Exception {
        final TestRunner firstrunner = TestRunners.newTestRunner(new UpdateCounter());
        firstrunner.setProperty(COUNTER_NAME, "firewall");
        firstrunner.setProperty(DELTA, "1");
        Map<String, String> attributes = new HashMap<String, String>();
        firstrunner.enqueue("", attributes);
        firstrunner.run();
        firstrunner.assertAllFlowFilesTransferred(SUCCESS, 1);
    }

    @Test
    public void testExpressionLanguage() throws Exception {
        final TestRunner firstrunner = TestRunners.newTestRunner(new UpdateCounter());
        firstrunner.setProperty(COUNTER_NAME, "${filename}");
        firstrunner.setProperty(DELTA, "${num}");
        final Map<String, String> attributes = new HashMap<>();
        attributes.put("filename", "test");
        attributes.put("num", "40");
        firstrunner.enqueue(new byte[0], attributes);
        firstrunner.run();
        Long counter = firstrunner.getCounterValue("test");
        Assert.assertEquals(Optional.ofNullable(counter), Optional.ofNullable(40L));
        firstrunner.assertAllFlowFilesTransferred(SUCCESS, 1);
    }
}

