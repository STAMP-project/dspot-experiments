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
package org.apache.camel.component.consul;


import ConsulConstants.CONSUL_ACTION;
import ConsulConstants.CONSUL_KEY;
import ConsulConstants.CONSUL_RESULT;
import ConsulKeyValueActions.PUT;
import java.util.Optional;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Test;


public class ConsulClientKeyValueTest extends ConsulTestSupport {
    @Test
    public void testKeyPut() throws Exception {
        String key = generateKey();
        String val = generateRandomString();
        MockEndpoint mock = getMockEndpoint("mock:kv");
        mock.expectedMinimumMessageCount(1);
        mock.expectedBodiesReceived(val);
        mock.expectedHeaderReceived(CONSUL_RESULT, true);
        fluentTemplate().withHeader(CONSUL_ACTION, PUT).withHeader(CONSUL_KEY, key).withBody(val).to("direct:kv").send();
        mock.assertIsSatisfied();
        Optional<String> keyVal = getConsul().keyValueClient().getValueAsString(key);
        assertTrue(keyVal.isPresent());
        assertEquals(val, keyVal.get());
    }
}

