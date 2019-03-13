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
package org.apache.camel.component.ignite;


import IgniteConstants.IGNITE_IDGEN_OPERATION;
import IgniteIdGenOperation.INCREMENT_AND_GET;
import org.apache.camel.component.ignite.idgen.IgniteIdGenEndpoint;
import org.junit.Test;


public class IgniteIdGenTest extends AbstractIgniteTest {
    @Test
    public void testOperations() {
        assert_().that(template.requestBody("ignite-idgen:abc?initialValue=0&operation=GET", null, Long.class)).isEqualTo(0);
        assert_().that(template.requestBody("ignite-idgen:abc?initialValue=0&operation=GET_AND_INCREMENT", null, Long.class)).isEqualTo(0);
        assert_().that(template.requestBody("ignite-idgen:abc?initialValue=0&operation=INCREMENT_AND_GET", null, Long.class)).isEqualTo(2);
        assert_().that(template.requestBody("ignite-idgen:abc?initialValue=0&operation=ADD_AND_GET", 5, Long.class)).isEqualTo(7);
        assert_().that(template.requestBody("ignite-idgen:abc?initialValue=0&operation=GET_AND_ADD", 5, Long.class)).isEqualTo(7);
        assert_().that(template.requestBody("ignite-idgen:abc?initialValue=0&operation=GET", 5, Long.class)).isEqualTo(12);
    }

    @Test
    public void testInitialValue() {
        assert_().that(template.requestBody("ignite-idgen:abc?operation=GET&initialValue=100", null, Long.class)).isEqualTo(100);
        assert_().that(template.requestBody("ignite-idgen:abc?operation=GET_AND_INCREMENT&initialValue=100", null, Long.class)).isEqualTo(100);
        assert_().that(template.requestBody("ignite-idgen:abc?operation=INCREMENT_AND_GET&initialValue=100", null, Long.class)).isEqualTo(102);
        assert_().that(template.requestBody("ignite-idgen:abc?operation=ADD_AND_GET&initialValue=100", 5, Long.class)).isEqualTo(107);
        assert_().that(template.requestBody("ignite-idgen:abc?operation=GET_AND_ADD&initialValue=100", 5, Long.class)).isEqualTo(107);
        assert_().that(template.requestBody("ignite-idgen:abc?operation=GET&initialValue=100", 5, Long.class)).isEqualTo(112);
    }

    @Test
    public void testDifferentOperation() {
        assert_().that(template.requestBody("ignite-idgen:abc?operation=GET&initialValue=100", null, Long.class)).isEqualTo(100);
        assert_().that(template.requestBodyAndHeader("ignite-idgen:abc?operation=GET_AND_INCREMENT&initialValue=100", null, IGNITE_IDGEN_OPERATION, INCREMENT_AND_GET, Long.class)).isEqualTo(101);
    }

    @Test
    public void testBatchSize() {
        IgniteIdGenEndpoint endpoint = context.getEndpoint("ignite-idgen:abc?operation=GET&initialValue=100&batchSize=100", IgniteIdGenEndpoint.class);
        assert_().that(template.requestBody(endpoint, null, Long.class)).isEqualTo(100);
        // Cannot test much here with a single Ignite instance, let's just test that the parameter could be set.
        assert_().that(endpoint.getBatchSize());
    }
}

