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
package io.undertow.servlet.test.errorpage;


import StatusCodes.INTERNAL_SERVER_ERROR;
import StatusCodes.NOT_FOUND;
import StatusCodes.NOT_IMPLEMENTED;
import io.undertow.testutils.DefaultServer;
import io.undertow.testutils.TestHttpClient;
import java.io.IOException;
import javax.servlet.ServletException;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 *
 *
 * @author Stuart Douglas
 */
@RunWith(DefaultServer.class)
public class ErrorPageTestCase {
    @Test
    public void testErrorPages() throws IOException {
        TestHttpClient client = new TestHttpClient();
        try {
            runTest(1, client, NOT_FOUND, null, "/404");
            runTest(1, client, INTERNAL_SERVER_ERROR, null, "/defaultErrorPage");
            runTest(1, client, NOT_IMPLEMENTED, null, "/defaultErrorPage");
            runTest(1, client, null, ParentException.class, "/parentException");
            runTest(1, client, null, ChildException.class, "/childException");
            runTest(1, client, null, RuntimeException.class, "/runtimeException");
            runTest(1, client, null, IllegalStateException.class, "/runtimeException");
            runTest(1, client, null, Exception.class, "/defaultErrorPage");
            runTest(1, client, null, IOException.class, "/defaultErrorPage");
            runTest(1, client, null, ServletException.class, "/defaultErrorPage");
        } finally {
            client.getConnectionManager().shutdown();
        }
    }

    @Test
    public void testErrorPagesWithNoDefaultErrorPage() throws IOException {
        TestHttpClient client = new TestHttpClient();
        try {
            runTest(2, client, NOT_FOUND, null, "/404");
            runTest(2, client, NOT_IMPLEMENTED, null, "/501");
            runTest(2, client, INTERNAL_SERVER_ERROR, null, "<html><head><title>Error</title></head><body>Internal Server Error</body></html>", false);
            runTest(2, client, null, ParentException.class, "/parentException");
            runTest(2, client, null, ChildException.class, "/childException");
            runTest(2, client, null, RuntimeException.class, "/runtimeException");
            runTest(2, client, null, IllegalStateException.class, "/runtimeException");
            runTest(2, client, null, Exception.class, "<html><head><title>Error</title></head><body>Internal Server Error</body></html>", false);
            runTest(2, client, null, IOException.class, "<html><head><title>Error</title></head><body>Internal Server Error</body></html>", false);
            runTest(2, client, null, ServletException.class, "<html><head><title>Error</title></head><body>Internal Server Error</body></html>", false);
        } finally {
            client.getConnectionManager().shutdown();
        }
    }

    // see UNDERTOW-249
    @Test
    public void testErrorPagesWith500PageMapped() throws IOException {
        TestHttpClient client = new TestHttpClient();
        try {
            runTest(3, client, NOT_FOUND, null, "/404");
            runTest(3, client, INTERNAL_SERVER_ERROR, null, "/500");
            runTest(3, client, NOT_IMPLEMENTED, null, "/defaultErrorPage");
            runTest(3, client, null, ParentException.class, "/parentException");
            runTest(3, client, null, ChildException.class, "/childException");
            runTest(3, client, null, RuntimeException.class, "/runtimeException");
            runTest(3, client, null, IllegalStateException.class, "/runtimeException");
            runTest(3, client, null, Exception.class, "/500");
            runTest(3, client, null, IOException.class, "/500");
            runTest(3, client, null, ServletException.class, "/500");
        } finally {
            client.getConnectionManager().shutdown();
        }
    }
}

