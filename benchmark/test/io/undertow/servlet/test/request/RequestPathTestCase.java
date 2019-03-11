/**
 * JBoss, Home of Professional Open Source.
 * Copyright 2014 Red Hat, Inc., and individual contributors
 * as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package io.undertow.servlet.test.request;


import io.undertow.testutils.DefaultServer;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 *
 *
 * @author Stuart Douglas
 */
@RunWith(DefaultServer.class)
public class RequestPathTestCase {
    @Test
    public void testRequestPaths() throws Exception {
        int port = DefaultServer.getHostPort("default");
        // test default servlet mappings
        runtest("/servletContext/somePath", false, "null", "/somePath", (("http://localhost:" + port) + "/servletContext/somePath"), "/servletContext/somePath", "");
        runtest("/servletContext/somePath?foo=bar", false, "null", "/somePath", (("http://localhost:" + port) + "/servletContext/somePath"), "/servletContext/somePath", "foo=bar");
        runtest("/servletContext/somePath?foo=b+a+r", false, "null", "/somePath", (("http://localhost:" + port) + "/servletContext/somePath"), "/servletContext/somePath", "foo=b+a+r");
        runtest("/servletContext/some%20path?foo=b+a+r", false, "null", "/some path", (("http://localhost:" + port) + "/servletContext/some%20path"), "/servletContext/some%20path", "foo=b+a+r");
        runtest("/servletContext/somePath.txt", true, "null", "/somePath.txt", (("http://localhost:" + port) + "/servletContext/somePath.txt"), "/servletContext/somePath.txt", "");
        runtest("/servletContext/somePath.txt?foo=bar", true, "null", "/somePath.txt", (("http://localhost:" + port) + "/servletContext/somePath.txt"), "/servletContext/somePath.txt", "foo=bar");
        // test non-default mappings
        runtest("/servletContext/req/somePath", false, "/somePath", "/req", (("http://localhost:" + port) + "/servletContext/req/somePath"), "/servletContext/req/somePath", "");
        runtest("/servletContext/req/somePath?foo=bar", false, "/somePath", "/req", (("http://localhost:" + port) + "/servletContext/req/somePath"), "/servletContext/req/somePath", "foo=bar");
        runtest("/servletContext/req/somePath?foo=b+a+r", false, "/somePath", "/req", (("http://localhost:" + port) + "/servletContext/req/somePath"), "/servletContext/req/somePath", "foo=b+a+r");
        runtest("/servletContext/req/some%20path?foo=b+a+r", false, "/some path", "/req", (("http://localhost:" + port) + "/servletContext/req/some%20path"), "/servletContext/req/some%20path", "foo=b+a+r");
        runtest("/servletContext/req/somePath.txt", true, "/somePath.txt", "/req", (("http://localhost:" + port) + "/servletContext/req/somePath.txt"), "/servletContext/req/somePath.txt", "");
        runtest("/servletContext/req/somePath.txt?foo=bar", true, "/somePath.txt", "/req", (("http://localhost:" + port) + "/servletContext/req/somePath.txt"), "/servletContext/req/somePath.txt", "foo=bar");
        // test exact path mappings
        runtest("/servletContext/exact", false, "null", "/exact", (("http://localhost:" + port) + "/servletContext/exact"), "/servletContext/exact", "");
        runtest("/servletContext/exact?foo=bar", false, "null", "/exact", (("http://localhost:" + port) + "/servletContext/exact"), "/servletContext/exact", "foo=bar");
        // test exact path mappings with a filer
        runtest("/servletContext/exact.txt", true, "null", "/exact.txt", (("http://localhost:" + port) + "/servletContext/exact.txt"), "/servletContext/exact.txt", "");
        runtest("/servletContext/exact.txt?foo=bar", true, "null", "/exact.txt", (("http://localhost:" + port) + "/servletContext/exact.txt"), "/servletContext/exact.txt", "foo=bar");
        // test servlet extension matches
        runtest("/servletContext/file.html", false, "null", "/file.html", (("http://localhost:" + port) + "/servletContext/file.html"), "/servletContext/file.html", "");
        runtest("/servletContext/file.html?foo=bar", false, "null", "/file.html", (("http://localhost:" + port) + "/servletContext/file.html"), "/servletContext/file.html", "foo=bar");
    }
}

