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
package org.apache.camel.component.restlet;


import java.util.HashMap;
import java.util.Map;
import org.apache.camel.CamelExecutionException;
import org.junit.Test;


public class RestletThrowExceptionOnFailureTest extends RestletTestSupport {
    @Test(expected = CamelExecutionException.class)
    public void testRestletProducerGet2() throws Exception {
        Map<String, Object> headers = new HashMap<>();
        headers.put("id", 123);
        headers.put("beverage.beer", "Carlsberg");
        String out = template.requestBodyAndHeaders("direct:start", null, headers, String.class);
        assertEquals("123;Donald Duck;Carlsberg", out);
    }
}

