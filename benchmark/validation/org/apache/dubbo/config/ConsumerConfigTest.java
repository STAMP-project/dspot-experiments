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
package org.apache.dubbo.config;


import com.alibaba.dubbo.config.ConsumerConfig;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;


public class ConsumerConfigTest {
    @Test
    public void testTimeout() throws Exception {
        try {
            System.clearProperty("sun.rmi.transport.tcp.responseTimeout");
            ConsumerConfig consumer = new ConsumerConfig();
            consumer.setTimeout(10);
            MatcherAssert.assertThat(consumer.getTimeout(), Matchers.is(10));
            MatcherAssert.assertThat(System.getProperty("sun.rmi.transport.tcp.responseTimeout"), Matchers.equalTo("10"));
        } finally {
            System.clearProperty("sun.rmi.transport.tcp.responseTimeout");
        }
    }

    @Test
    public void testDefault() throws Exception {
        ConsumerConfig consumer = new ConsumerConfig();
        consumer.setDefault(true);
        MatcherAssert.assertThat(consumer.isDefault(), Matchers.is(true));
    }

    @Test
    public void testClient() throws Exception {
        ConsumerConfig consumer = new ConsumerConfig();
        consumer.setClient("client");
        MatcherAssert.assertThat(consumer.getClient(), Matchers.equalTo("client"));
    }
}

