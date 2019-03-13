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
package org.apache.camel.component.atom;


import org.apache.abdera.model.Entry;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.TestSupport;
import org.junit.Test;


/**
 * Example for wiki documentation
 */
// END SNIPPET: e1
public class AtomGoodBlogsTest extends TestSupport {
    // START SNIPPET: e1
    // This is the CamelContext that is the heart of Camel
    private CamelContext context;

    /**
     * This is the actual junit test method that does the assertion that our routes is working as expected
     */
    @Test
    public void testFiltering() throws Exception {
        // create and start Camel
        context = createCamelContext();
        context.start();
        // Get the mock endpoint
        MockEndpoint mock = context.getEndpoint("mock:result", MockEndpoint.class);
        // There should be at least two good blog entries from the feed
        mock.expectedMinimumMessageCount(2);
        // Asserts that the above expectations is true, will throw assertions exception if it failed
        // Camel will default wait max 20 seconds for the assertions to be true, if the conditions
        // is true sooner Camel will continue
        mock.assertIsSatisfied();
        // stop Camel after use
        context.stop();
    }

    /**
     * Services for blogs
     */
    public class BlogService {
        /**
         * Tests the blogs if its a good blog entry or not
         */
        public boolean isGoodBlog(Exchange exchange) {
            Entry entry = exchange.getIn().getBody(Entry.class);
            String title = entry.getTitle();
            // We like blogs about Camel
            boolean good = title.toLowerCase().contains("camel");
            return good;
        }
    }
}

