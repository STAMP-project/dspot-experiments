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
package org.apache.camel.processor.aggregator;


import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;
import org.apache.camel.ContextTestSupport;
import org.junit.Assert;
import org.junit.Test;


public class AggregationStrategyBeanAdapterAllowNullTest extends ContextTestSupport {
    private AggregationStrategyBeanAdapterAllowNullTest.MyUserAppender appender = new AggregationStrategyBeanAdapterAllowNullTest.MyUserAppender();

    @Test
    public void testAggregate() throws Exception {
        getMockEndpoint("mock:result").expectedMessageCount(1);
        template.sendBody("direct:start", new AggregationStrategyBeanAdapterAllowNullTest.User("Claus"));
        template.sendBody("direct:start", new AggregationStrategyBeanAdapterAllowNullTest.User("James"));
        template.sendBody("direct:start", new AggregationStrategyBeanAdapterAllowNullTest.User("Jonathan"));
        assertMockEndpointsSatisfied();
        List<?> names = getMockEndpoint("mock:result").getReceivedExchanges().get(0).getIn().getBody(List.class);
        Assert.assertEquals("Claus", names.get(0));
        Assert.assertEquals("James", names.get(1));
        Assert.assertEquals("Jonathan", names.get(2));
    }

    public static final class MyUserAppender {
        public List<String> addUsers(List<String> names, AggregationStrategyBeanAdapterAllowNullTest.User user) {
            if (names == null) {
                names = new ArrayList<>();
            }
            names.add(user.getName());
            return names;
        }
    }

    /**
     * We support annotations on the types.
     */
    @XmlRootElement(name = "user")
    public static final class User {
        private String name;

        public User(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }
}

