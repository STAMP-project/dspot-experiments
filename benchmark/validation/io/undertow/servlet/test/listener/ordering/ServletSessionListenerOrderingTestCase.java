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
package io.undertow.servlet.test.listener.ordering;


import StatusCodes.OK;
import io.undertow.servlet.test.util.Tracker;
import io.undertow.testutils.DefaultServer;
import io.undertow.testutils.TestHttpClient;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 *
 *
 * @see https://issues.jboss.org/browse/UNDERTOW-23
 * @author Jozef Hartinger
 */
@RunWith(DefaultServer.class)
public class ServletSessionListenerOrderingTestCase {
    @Test
    public void testSimpleSessionUsage() throws IOException {
        TestHttpClient client = new TestHttpClient();
        try {
            Tracker.reset();
            HttpGet get = new HttpGet(((DefaultServer.getDefaultServerURL()) + "/listener/test"));
            HttpResponse result = client.execute(get);
            Assert.assertEquals(OK, result.getStatusLine().getStatusCode());
            List<String> expected = new ArrayList<>();
            expected.add(FirstListener.class.getSimpleName());
            expected.add(SecondListener.class.getSimpleName());
            expected.add(SecondListener.class.getSimpleName());
            expected.add(FirstListener.class.getSimpleName());
            Assert.assertEquals(expected, Tracker.getActions());
        } finally {
            client.getConnectionManager().shutdown();
        }
    }
}

