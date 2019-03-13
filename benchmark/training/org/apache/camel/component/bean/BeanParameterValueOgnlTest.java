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
package org.apache.camel.component.bean;


import org.apache.camel.ContextTestSupport;
import org.junit.Test;


/**
 *
 */
public class BeanParameterValueOgnlTest extends ContextTestSupport {
    @Test
    public void testBeanParameterValue() throws Exception {
        getMockEndpoint("mock:result").expectedBodiesReceived("Hello World");
        template.sendBody("direct:start", "World");
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testBeanParameterValueBodyOgnl() throws Exception {
        getMockEndpoint("mock:result").expectedBodiesReceived("Hello Tony");
        BeanParameterValueOgnlTest.Animal tiger = new BeanParameterValueOgnlTest.Animal("Tony", 13);
        template.sendBody("direct:start2", tiger);
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testBeanParameterValueHeaderOgnl() throws Exception {
        getMockEndpoint("mock:result").expectedBodiesReceived("Hello Kong");
        BeanParameterValueOgnlTest.Animal kong = new BeanParameterValueOgnlTest.Animal("Kong", 34);
        BeanParameterValueOgnlTest.Animal tiger = new BeanParameterValueOgnlTest.Animal("Tony", 13);
        tiger.setFriend(kong);
        template.sendBodyAndHeader("direct:start3", "Hello World", "animal", tiger);
        assertMockEndpointsSatisfied();
    }

    public static class MyBean {
        public String bar(String body, boolean hello) {
            if (hello) {
                return "Hello " + body;
            } else {
                return body;
            }
        }

        public String echo(String body, int times) {
            if (times > 0) {
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < times; i++) {
                    sb.append(body);
                }
                return sb.toString();
            }
            return body;
        }
    }

    public static final class Animal {
        private String name;

        private int age;

        private BeanParameterValueOgnlTest.Animal friend;

        private Animal(String name, int age) {
            this.name = name;
            this.age = age;
        }

        public String getName() {
            return name;
        }

        public int getAge() {
            return age;
        }

        public BeanParameterValueOgnlTest.Animal getFriend() {
            return friend;
        }

        public void setFriend(BeanParameterValueOgnlTest.Animal friend) {
            this.friend = friend;
        }

        public boolean isDangerous() {
            return name.contains("Tiger");
        }

        @Override
        public String toString() {
            return name;
        }
    }
}

