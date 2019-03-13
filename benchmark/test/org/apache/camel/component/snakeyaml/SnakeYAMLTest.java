/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.component.snakeyaml;


import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
public class SnakeYAMLTest extends CamelTestSupport {
    private final SnakeYAMLDataFormat format;

    private final Object body;

    private final String expected;

    public SnakeYAMLTest(SnakeYAMLDataFormat format, Object body, String expected) {
        this.format = format;
        this.body = body;
        this.expected = expected;
    }

    @Test
    public void testMarshalAndUnmarshal() throws Exception {
        SnakeYAMLTestHelper.marshalAndUnmarshal(context(), body, "mock:reverse", "direct:in", "direct:back", expected);
    }
}

