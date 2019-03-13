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
package org.apache.camel.cdi.test;


import javax.inject.Inject;
import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(Arquillian.class)
@Ignore
public class ContextComponentTest {
    @Inject
    private CamelContext main;

    @Test
    @InSequence(1)
    public void addRouteToMainContext() throws Exception {
        main.addRoutes(new RouteBuilder() {
            @Override
            public void configure() {
                from("direct:inbound").to("first:in");
                from("first:out").to("second:in");
                from("second:out").to("mock:outbound");
            }
        });
    }
}

