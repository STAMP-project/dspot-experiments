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
package org.apache.nifi.processors.gettcp;


import GetTCP.ENDPOINT_LIST;
import GetTCP.END_OF_MESSAGE_BYTE;
import org.apache.nifi.util.TestRunner;
import org.junit.Test;


public final class TestGetTCP {
    private TestRunner testRunner;

    private GetTCP processor;

    @Test
    public void testSelectPropertiesValidation() {
        testRunner.setProperty(ENDPOINT_LIST, "!@;;*blah:9999");
        testRunner.assertNotValid();
        testRunner.setProperty(ENDPOINT_LIST, "localhost:9999");
        testRunner.assertValid();
        testRunner.setProperty(ENDPOINT_LIST, "localhost:-1");
        testRunner.assertNotValid();
        testRunner.setProperty(ENDPOINT_LIST, ",");
        testRunner.assertNotValid();
        testRunner.setProperty(ENDPOINT_LIST, ",localhost:9999");
        testRunner.assertNotValid();
        testRunner.setProperty(ENDPOINT_LIST, "999,localhost:123");
        testRunner.assertNotValid();
        testRunner.setProperty(ENDPOINT_LIST, "localhost:abc_port");
        testRunner.assertNotValid();
        testRunner.setProperty(ENDPOINT_LIST, "localhost:9999;localhost:1234");
        testRunner.assertNotValid();
        testRunner.setProperty(ENDPOINT_LIST, "localhost:9999,localhost:1234");
        testRunner.assertValid();
        testRunner.setProperty(END_OF_MESSAGE_BYTE, "354");
        testRunner.assertNotValid();
        testRunner.setProperty(END_OF_MESSAGE_BYTE, "13");
        testRunner.assertValid();
    }

    @Test
    public void testDynamicProperty() {
        testRunner.setProperty(ENDPOINT_LIST, "localhost:9999,localhost:1234");
        testRunner.setProperty("MyCustomProperty", "abc");
        testRunner.assertValid();
    }
}

