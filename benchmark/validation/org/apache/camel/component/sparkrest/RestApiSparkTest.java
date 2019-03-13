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
package org.apache.camel.component.sparkrest;


import org.junit.Ignore;
import org.junit.Test;


@Ignore("Does not run well on CI due test uses JMX mbeans")
public class RestApiSparkTest extends BaseSparkTest {
    @Test
    public void testApi() throws Exception {
        String out = template.requestBody((("http://localhost:" + (getPort())) + "/api-doc"), null, String.class);
        assertNotNull(out);
        assertTrue(out.contains("\"version\" : \"1.2.3\""));
        assertTrue(out.contains("\"title\" : \"The hello rest thing\""));
        assertTrue(out.contains("\"/hello/bye/{name}\""));
        assertTrue(out.contains("\"/hello/hi/{name}\""));
        assertTrue(out.contains("\"summary\" : \"To update the greeting message\""));
        // regular REST service should still work
        out = template.requestBody((("http://localhost:" + (getPort())) + "/hello/hi/world"), null, String.class);
        assertEquals("Hello World", out);
    }
}

