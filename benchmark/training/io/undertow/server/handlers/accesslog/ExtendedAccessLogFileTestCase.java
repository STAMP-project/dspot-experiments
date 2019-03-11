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
package io.undertow.server.handlers.accesslog;


import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.testutils.DefaultServer;
import io.undertow.util.HttpString;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Tests writing the access log to a file
 *
 * @author Stuart Douglas
 */
@RunWith(DefaultServer.class)
public class ExtendedAccessLogFileTestCase {
    private static final Path logDirectory = Paths.get(System.getProperty("java.io.tmpdir"), "logs");

    public static final String PATTERN = "cs-uri cs(test-header) x-O(aa) x-H(secure)";

    private DefaultAccessLogReceiver logReceiver;

    private static final HttpHandler HELLO_HANDLER = new HttpHandler() {
        @Override
        public void handleRequest(final HttpServerExchange exchange) throws Exception {
            exchange.getResponseHeaders().put(new HttpString("aa"), "bb");
            exchange.getResponseSender().send("Hello");
        }
    };

    @Test
    public void testSingleLogMessageToFile() throws IOException, InterruptedException {
        Path logFileName = ExtendedAccessLogFileTestCase.logDirectory.resolve("extended.log");
        verifySingleLogMessageToFile(logFileName, logReceiver);
    }
}

