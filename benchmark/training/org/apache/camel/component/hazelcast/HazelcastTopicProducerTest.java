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
package org.apache.camel.component.hazelcast;


import com.hazelcast.core.ITopic;
import org.apache.camel.CamelExecutionException;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;


public class HazelcastTopicProducerTest extends HazelcastCamelTestSupport {
    @Mock
    private ITopic<String> topic;

    @Test(expected = CamelExecutionException.class)
    public void testWithInvalidOperation() {
        template.sendBody("direct:publishInvalid", "foo");
    }

    @Test
    public void noOperation() {
        template.sendBody("direct:no-operation", "bar");
        Mockito.verify(topic).publish("bar");
    }

    @Test
    public void publish() {
        template.sendBody("direct:publish", "bar");
        Mockito.verify(topic).publish("bar");
    }
}

