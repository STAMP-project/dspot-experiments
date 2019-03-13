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
package org.apache.camel.component.rss;


import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import org.apache.camel.Body;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;


// END SNIPPET: ex2
public class RssFilterTest extends CamelTestSupport {
    @Test
    public void testFilterOutNonCamelPosts() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedMessageCount(6);
        mock.assertIsSatisfied();
    }

    // START SNIPPET: ex2
    public static class FilterBean {
        public boolean titleContainsCamel(@Body
        SyndFeed feed) {
            SyndEntry firstEntry = ((SyndEntry) (feed.getEntries().get(0)));
            return firstEntry.getTitle().contains("Camel");
        }
    }
}

