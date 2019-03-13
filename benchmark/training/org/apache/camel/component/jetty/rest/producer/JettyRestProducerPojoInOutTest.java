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
package org.apache.camel.component.jetty.rest.producer;


import Exchange.CONTENT_TYPE;
import org.apache.camel.component.jetty.BaseJettyTest;
import org.apache.camel.component.jetty.rest.CountryPojo;
import org.apache.camel.component.jetty.rest.UserPojo;
import org.junit.Test;


public class JettyRestProducerPojoInOutTest extends BaseJettyTest {
    @Test
    public void testJettyEmptyBody() throws Exception {
        String out = fluentTemplate.to("rest:get:users/lives").withHeader(CONTENT_TYPE, "application/json").request(String.class);
        assertNotNull(out);
        assertEquals("{\"iso\":\"EN\",\"country\":\"England\"}", out);
    }

    @Test
    public void testJettyJSonBody() throws Exception {
        String body = "{\"id\": 123, \"name\": \"Donald Duck\"}";
        String out = fluentTemplate.to("rest:post:users/lives").withHeader(CONTENT_TYPE, "application/json").withBody(body).request(String.class);
        assertNotNull(out);
        assertEquals("{\"iso\":\"EN\",\"country\":\"England\"}", out);
    }

    @Test
    public void testJettyPojoIn() throws Exception {
        UserPojo user = new UserPojo();
        user.setId(123);
        user.setName("Donald Duck");
        String out = fluentTemplate.to("rest:post:users/lives").withHeader(CONTENT_TYPE, "application/json").withBody(user).request(String.class);
        assertNotNull(out);
        assertEquals("{\"iso\":\"EN\",\"country\":\"England\"}", out);
    }

    @Test
    public void testJettyPojoInOut() throws Exception {
        UserPojo user = new UserPojo();
        user.setId(123);
        user.setName("Donald Duck");
        // must provide outType parameter to tell Camel to bind the output from the REST service from json to POJO
        CountryPojo pojo = fluentTemplate.to("rest:post:users/lives?outType=org.apache.camel.component.jetty.rest.CountryPojo").withHeader(CONTENT_TYPE, "application/json").withBody(user).request(CountryPojo.class);
        assertNotNull(pojo);
        assertEquals("EN", pojo.getIso());
        assertEquals("England", pojo.getCountry());
    }
}

