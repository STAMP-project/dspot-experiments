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
package org.apache.camel.component.netty4.http;


import org.apache.camel.Exchange;
import org.junit.Assert;
import org.junit.Test;


public class NettyHttpHeaderFilterStrategyTest {
    private NettyHttpHeaderFilterStrategy filter;

    private Exchange exchange;

    @Test
    public void applyFilterToExternalHeaders() {
        Assert.assertFalse(filter.applyFilterToExternalHeaders("content-length", 10, exchange));
        Assert.assertFalse(filter.applyFilterToExternalHeaders("Content-Length", 10, exchange));
        Assert.assertFalse(filter.applyFilterToExternalHeaders("content-type", "text/xml", exchange));
        Assert.assertFalse(filter.applyFilterToExternalHeaders("Content-Type", "text/xml", exchange));
        Assert.assertFalse(filter.applyFilterToExternalHeaders("cache-control", "no-cache", exchange));
        Assert.assertFalse(filter.applyFilterToExternalHeaders("Cache-Control", "no-cache", exchange));
        Assert.assertFalse(filter.applyFilterToExternalHeaders("connection", "close", exchange));
        Assert.assertFalse(filter.applyFilterToExternalHeaders("Connection", "close", exchange));
        Assert.assertFalse(filter.applyFilterToExternalHeaders("date", "close", exchange));
        Assert.assertFalse(filter.applyFilterToExternalHeaders("Data", "close", exchange));
        Assert.assertFalse(filter.applyFilterToExternalHeaders("pragma", "no-cache", exchange));
        Assert.assertFalse(filter.applyFilterToExternalHeaders("Pragma", "no-cache", exchange));
        Assert.assertFalse(filter.applyFilterToExternalHeaders("trailer", "Max-Forwards", exchange));
        Assert.assertFalse(filter.applyFilterToExternalHeaders("Trailer", "Max-Forwards", exchange));
        Assert.assertFalse(filter.applyFilterToExternalHeaders("transfer-encoding", "chunked", exchange));
        Assert.assertFalse(filter.applyFilterToExternalHeaders("Transfer-Encoding", "chunked", exchange));
        Assert.assertFalse(filter.applyFilterToExternalHeaders("upgrade", "HTTP/2.0", exchange));
        Assert.assertFalse(filter.applyFilterToExternalHeaders("Upgrade", "HTTP/2.0", exchange));
        Assert.assertFalse(filter.applyFilterToExternalHeaders("via", "1.1 nowhere.com", exchange));
        Assert.assertFalse(filter.applyFilterToExternalHeaders("Via", "1.1 nowhere.com", exchange));
        Assert.assertFalse(filter.applyFilterToExternalHeaders("warning", "199 Miscellaneous warning", exchange));
        Assert.assertFalse(filter.applyFilterToExternalHeaders("Warning", "199 Miscellaneous warning", exchange));
        // any Camel header should be filtered
        Assert.assertTrue(filter.applyFilterToExternalHeaders("CamelHeader", "test", exchange));
        Assert.assertTrue(filter.applyFilterToExternalHeaders("org.apache.camel.header", "test", exchange));
        Assert.assertFalse(filter.applyFilterToExternalHeaders("notFilteredHeader", "test", exchange));
        Assert.assertFalse(filter.applyFilterToExternalHeaders("host", "dummy.host.com", exchange));
        Assert.assertFalse(filter.applyFilterToExternalHeaders("Host", "dummy.host.com", exchange));
    }

    @Test
    public void applyFilterToCamelHeaders() {
        Assert.assertTrue(filter.applyFilterToCamelHeaders("content-length", 10, exchange));
        Assert.assertTrue(filter.applyFilterToCamelHeaders("Content-Length", 10, exchange));
        Assert.assertTrue(filter.applyFilterToCamelHeaders("content-type", "text/xml", exchange));
        Assert.assertTrue(filter.applyFilterToCamelHeaders("Content-Type", "text/xml", exchange));
        Assert.assertTrue(filter.applyFilterToCamelHeaders("cache-control", "no-cache", exchange));
        Assert.assertTrue(filter.applyFilterToCamelHeaders("Cache-Control", "no-cache", exchange));
        Assert.assertTrue(filter.applyFilterToCamelHeaders("connection", "close", exchange));
        Assert.assertTrue(filter.applyFilterToCamelHeaders("Connection", "close", exchange));
        Assert.assertTrue(filter.applyFilterToCamelHeaders("date", "close", exchange));
        Assert.assertTrue(filter.applyFilterToCamelHeaders("Date", "close", exchange));
        Assert.assertTrue(filter.applyFilterToCamelHeaders("pragma", "no-cache", exchange));
        Assert.assertTrue(filter.applyFilterToCamelHeaders("Pragma", "no-cache", exchange));
        Assert.assertTrue(filter.applyFilterToCamelHeaders("trailer", "Max-Forwards", exchange));
        Assert.assertTrue(filter.applyFilterToCamelHeaders("Trailer", "Max-Forwards", exchange));
        Assert.assertTrue(filter.applyFilterToCamelHeaders("transfer-encoding", "chunked", exchange));
        Assert.assertTrue(filter.applyFilterToCamelHeaders("Transfer-Encoding", "chunked", exchange));
        Assert.assertTrue(filter.applyFilterToCamelHeaders("upgrade", "HTTP/2.0", exchange));
        Assert.assertTrue(filter.applyFilterToCamelHeaders("Upgrade", "HTTP/2.0", exchange));
        Assert.assertTrue(filter.applyFilterToCamelHeaders("via", "1.1 nowhere.com", exchange));
        Assert.assertTrue(filter.applyFilterToCamelHeaders("Via", "1.1 nowhere.com", exchange));
        Assert.assertTrue(filter.applyFilterToCamelHeaders("warning", "199 Miscellaneous warning", exchange));
        Assert.assertTrue(filter.applyFilterToCamelHeaders("Warning", "199 Miscellaneous warning", exchange));
        // any Camel header should be filtered
        Assert.assertTrue(filter.applyFilterToCamelHeaders("CamelHeader", "test", exchange));
        Assert.assertTrue(filter.applyFilterToCamelHeaders("org.apache.camel.header", "test", exchange));
        Assert.assertFalse(filter.applyFilterToCamelHeaders("notFilteredHeader", "test", exchange));
        Assert.assertTrue(filter.applyFilterToCamelHeaders("host", "dummy.host.com", exchange));
        Assert.assertTrue(filter.applyFilterToCamelHeaders("Host", "dummy.host.com", exchange));
    }
}

