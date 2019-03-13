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
package org.apache.camel.component.facebook;


import FacebookConstants.RAW_JSON_HEADER;
import facebook4j.api.SearchMethods;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Test;


public class FacebookComponentConsumerTest extends CamelFacebookTestSupport {
    public static final String APACHE_FOUNDATION_PAGE_ID = "6538157161";

    private final Set<String> searchNames = new HashSet<>();

    private List<String> excludedNames;

    public FacebookComponentConsumerTest() throws Exception {
        // find search methods for consumer tests
        for (Method method : SearchMethods.class.getDeclaredMethods()) {
            String name = getShortName(method.getName());
            if ((!("locations".equals(name))) && (!("checkins".equals(name)))) {
                searchNames.add(name);
            }
        }
        excludedNames = Arrays.asList("places", "users", "search", "pages", "searchPosts");
    }

    @Test
    public void testConsumers() throws InterruptedException {
        for (String name : searchNames) {
            MockEndpoint mock;
            if (!(excludedNames.contains(name))) {
                mock = getMockEndpoint(("mock:consumeResult" + name));
                mock.expectedMinimumMessageCount(1);
            }
            mock = getMockEndpoint(("mock:consumeQueryResult" + name));
            mock.expectedMinimumMessageCount(1);
        }
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testJsonStoreEnabled() throws Exception {
        final MockEndpoint mock = getMockEndpoint("mock:testJsonStoreEnabled");
        mock.expectedMinimumMessageCount(1);
        mock.assertIsSatisfied();
        final String rawJSON = mock.getExchanges().get(0).getIn().getHeader(RAW_JSON_HEADER, String.class);
        assertNotNull("Null rawJSON", rawJSON);
        assertFalse("Empty rawJSON", rawJSON.isEmpty());
    }

    @Test
    public void testPage() throws Exception {
        final MockEndpoint mock = getMockEndpoint("mock:testPage");
        mock.expectedMinimumMessageCount(1);
        mock.assertIsSatisfied();
    }
}

