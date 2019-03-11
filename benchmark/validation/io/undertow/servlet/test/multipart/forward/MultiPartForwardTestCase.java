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
package io.undertow.servlet.test.multipart.forward;


import io.undertow.testutils.DefaultServer;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(DefaultServer.class)
public class MultiPartForwardTestCase {
    @Test
    public void urlEncodedFormRequestDirectlyToMultipartServlet() throws IOException {
        String response = sendRequest("/multipart", createUrlEncodedFormPostEntity());
        Assert.assertEquals(("Params:\n" + "foo: bar"), response);
    }

    @Test
    public void urlEncodedFormRequestForwardedToMultipartServlet() throws IOException {
        String response = sendRequest("/forward", createUrlEncodedFormPostEntity());
        Assert.assertEquals(("Params:\n" + "foo: bar"), response);
    }

    @Test
    public void multiPartFormRequestDirectlyToMultipartServlet() throws IOException {
        String response = sendRequest("/multipart", createMultiPartFormPostEntity());
        Assert.assertEquals(("Params:\n" + "foo: bar"), response);
    }

    @Test
    public void multiPartFormRequestForwardedToMultipartServlet() throws IOException {
        String response = sendRequest("/forward", createMultiPartFormPostEntity());
        Assert.assertEquals(("Params:\n" + "foo: bar"), response);
    }
}

