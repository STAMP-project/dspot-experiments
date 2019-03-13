/**
 *
 */
/**
 * ========================================================================
 */
/**
 * Copyright (c) 1995-2019 Mort Bay Consulting Pty. Ltd.
 */
/**
 * ------------------------------------------------------------------------
 */
/**
 * All rights reserved. This program and the accompanying materials
 */
/**
 * are made available under the terms of the Eclipse Public License v1.0
 */
/**
 * and Apache License v2.0 which accompanies this distribution.
 */
/**
 *
 */
/**
 * The Eclipse Public License is available at
 */
/**
 * http://www.eclipse.org/legal/epl-v10.html
 */
/**
 *
 */
/**
 * The Apache License v2.0 is available at
 */
/**
 * http://www.opensource.org/licenses/apache2.0.php
 */
/**
 *
 */
/**
 * You may elect to redistribute this code under either of these licenses.
 */
/**
 * ========================================================================
 */
/**
 *
 */
package org.eclipse.jetty.servlets;


import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.Enumeration;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.servlets.DoSFilter.RateTracker;
import org.eclipse.jetty.toolchain.test.jupiter.WorkDir;
import org.eclipse.jetty.toolchain.test.jupiter.WorkDirExtension;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;


@ExtendWith(WorkDirExtension.class)
public class DoSFilterTest extends AbstractDoSFilterTest {
    public WorkDir workDir;

    private static class RemoteAddressRequest extends Request {
        public RemoteAddressRequest(String remoteHost, int remotePort) {
            super(null, null);
            setRemoteAddr(new InetSocketAddress(remoteHost, remotePort));
        }
    }

    private static class NoOpFilterConfig implements FilterConfig {
        @Override
        public String getFilterName() {
            return "noop";
        }

        @Override
        public ServletContext getServletContext() {
            return null;
        }

        @Override
        public String getInitParameter(String name) {
            return null;
        }

        @Override
        public Enumeration<String> getInitParameterNames() {
            return Collections.emptyEnumeration();
        }
    }

    @Test
    public void testRemotePortLoadIdCreation_ipv6() throws ServletException {
        final ServletRequest request = new DoSFilterTest.RemoteAddressRequest("::192.9.5.5", 12345);
        DoSFilter doSFilter = new DoSFilter();
        doSFilter.init(new DoSFilterTest.NoOpFilterConfig());
        doSFilter.setRemotePort(true);
        try {
            RateTracker tracker = doSFilter.getRateTracker(request);
            MatcherAssert.assertThat("tracker.id", tracker.getId(), // short form
            // long form
            Matchers.anyOf(Matchers.is("[::192.9.5.5]:12345"), Matchers.is("[0:0:0:0:0:0:c009:505]:12345")));
        } finally {
            doSFilter.stopScheduler();
        }
    }

    @Test
    public void testRemotePortLoadIdCreation_ipv4() throws ServletException {
        final ServletRequest request = new DoSFilterTest.RemoteAddressRequest("127.0.0.1", 12345);
        DoSFilter doSFilter = new DoSFilter();
        doSFilter.init(new DoSFilterTest.NoOpFilterConfig());
        doSFilter.setRemotePort(true);
        try {
            RateTracker tracker = doSFilter.getRateTracker(request);
            MatcherAssert.assertThat("tracker.id", tracker.getId(), Matchers.is("127.0.0.1:12345"));
        } finally {
            doSFilter.stopScheduler();
        }
    }

    @Test
    public void testRateIsRateExceeded() throws InterruptedException {
        DoSFilter doSFilter = new DoSFilter();
        doSFilter.setName("foo");
        boolean exceeded = hitRateTracker(doSFilter, 0);
        Assertions.assertTrue(exceeded, "Last hit should have exceeded");
        int sleep = 250;
        exceeded = hitRateTracker(doSFilter, sleep);
        Assertions.assertFalse(exceeded, "Should not exceed as we sleep 300s for each hit and thus do less than 4 hits/s");
    }

    @Test
    public void testWhitelist() throws Exception {
        DoSFilter filter = new DoSFilter();
        filter.setName("foo");
        filter.setWhitelist("192.168.0.1/32,10.0.0.0/8,4d8:0:a:1234:ABc:1F:b18:17,4d8:0:a:1234:ABc:1F:0:0/96");
        Assertions.assertTrue(filter.checkWhitelist("192.168.0.1"));
        Assertions.assertFalse(filter.checkWhitelist("192.168.0.2"));
        Assertions.assertFalse(filter.checkWhitelist("11.12.13.14"));
        Assertions.assertTrue(filter.checkWhitelist("10.11.12.13"));
        Assertions.assertTrue(filter.checkWhitelist("10.0.0.0"));
        Assertions.assertFalse(filter.checkWhitelist("0.0.0.0"));
        Assertions.assertTrue(filter.checkWhitelist("4d8:0:a:1234:ABc:1F:b18:17"));
        Assertions.assertTrue(filter.checkWhitelist("4d8:0:a:1234:ABc:1F:b18:0"));
        Assertions.assertFalse(filter.checkWhitelist("4d8:0:a:1234:ABc:1D:0:0"));
    }

    @Test
    public void testUnresponsiveServer() throws Exception {
        String last = ("GET /ctx/timeout/?sleep=" + (2 * (_requestMaxTime))) + " HTTP/1.1\r\nHost: localhost\r\nConnection: close\r\n\r\n";
        String responses = doRequests("", 0, 0, 0, last);
        MatcherAssert.assertThat(responses, Matchers.containsString(" 503 "));
    }
}

