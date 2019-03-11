/**
 * This file is part of the Heritrix web crawler (crawler.archive.org).
 *
 *  Licensed to the Internet Archive (IA) by one or more individual
 *  contributors.
 *
 *  The IA licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.archive.modules.net;


import junit.framework.TestCase;
import org.archive.modules.fetcher.DefaultServerCache;


/**
 * Test the BigMapServerCache
 *
 * @author gojomo
 */
public class ServerCacheTest extends TestCase {
    public void testHolds() throws Exception {
        DefaultServerCache servers = new DefaultServerCache();
        String serverKey = "www.example.com:9090";
        String hostKey = "www.example.com";
        servers.getServerFor(serverKey);
        servers.getHostFor(hostKey);
        TestCase.assertTrue("cache lost server", servers.containsServer(serverKey));
        TestCase.assertTrue("cache lost host", servers.containsHost(hostKey));
    }

    public void testCrawlURIKeys() throws Exception {
        DefaultServerCache servers = new DefaultServerCache();
        testHostServer(servers, "http://www.example.com");
        testHostServer(servers, "http://www.example.com:9090");
        testHostServer(servers, "dns://www.example.com:9090");
    }
}

