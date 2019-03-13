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
package org.apache.activemq.bugs;


import CompositeDataConstants.STRING_PROPERTIES;
import java.util.Map;
import javax.management.openmbean.CompositeData;
import javax.management.openmbean.TabularDataSupport;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.jmx.QueueViewMBean;
import org.apache.activemq.command.ActiveMQQueue;
import org.hamcrest.Matchers;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;


public class AMQ4530Test {
    private static BrokerService brokerService;

    private static String TEST_QUEUE = "testQueue";

    private static ActiveMQQueue queue = new ActiveMQQueue(AMQ4530Test.TEST_QUEUE);

    private static String BROKER_ADDRESS = "tcp://localhost:0";

    private static String KEY = "testproperty";

    private static String VALUE = "propvalue";

    private ActiveMQConnectionFactory connectionFactory;

    private String connectionUri;

    @SuppressWarnings("unchecked")
    @Test
    public void testStringPropertiesFromCompositeData() throws Exception {
        final QueueViewMBean queueView = getProxyToQueueViewMBean();
        final CompositeData message = queueView.browse()[0];
        Assert.assertNotNull(message);
        TabularDataSupport stringProperties = ((TabularDataSupport) (message.get(STRING_PROPERTIES)));
        Assert.assertNotNull(stringProperties);
        Assert.assertThat(stringProperties.size(), Is.is(Matchers.greaterThan(0)));
        Map.Entry<Object, Object> compositeDataEntry = ((Map.Entry<Object, Object>) (stringProperties.entrySet().toArray()[0]));
        CompositeData stringEntry = ((CompositeData) (compositeDataEntry.getValue()));
        Assert.assertThat(String.valueOf(stringEntry.get("key")), Matchers.equalTo(AMQ4530Test.KEY));
        Assert.assertThat(String.valueOf(stringEntry.get("value")), Matchers.equalTo(AMQ4530Test.VALUE));
    }
}

