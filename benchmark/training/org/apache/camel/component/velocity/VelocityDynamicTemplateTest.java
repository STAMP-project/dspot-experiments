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
package org.apache.camel.component.velocity;


import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;


public class VelocityDynamicTemplateTest extends CamelTestSupport {
    @Test
    public void testVelocityLetter() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedMessageCount(1);
        mock.message(0).body(String.class).contains("Thanks for the order of Camel in Action");
        template.send("direct:a", createLetter());
        mock.assertIsSatisfied();
        mock.reset();
        mock.expectedMessageCount(1);
        mock.message(0).body(String.class).contains("Regards Apache Camel Riders Bookstore");
        template.send("direct:a", createLetter());
        mock.assertIsSatisfied();
    }

    public static class MyBean {
        ProducerTemplate template;

        boolean useLetter2;

        public void sendToNewTemplate(Exchange exchange) throws Exception {
            if ((template) == null) {
                template = exchange.getContext().createProducerTemplate();
            }
            template.send(("velocity:" + (getNewTemplate())), exchange);
            useLetter2 = !(useLetter2);
        }

        private String getNewTemplate() {
            if (useLetter2) {
                return "org/apache/camel/component/velocity/letter2.vm";
            } else {
                return "org/apache/camel/component/velocity/letter.vm";
            }
        }
    }
}

