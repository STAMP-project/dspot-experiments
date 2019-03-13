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
package io.undertow.server.handlers;


import io.undertow.testutils.DefaultServer;
import java.io.IOException;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 *
 *
 * @author Stuart Douglas
 */
@RunWith(DefaultServer.class)
public class RangeRequestTestCase {
    @Test
    public void testGenericRangeHandler() throws IOException, InterruptedException {
        runTest("/path", true);
    }

    @Test
    public void testResourceHandler() throws IOException, InterruptedException {
        runTest("/resource/range.txt", false);
    }

    @Test
    public void testCachedResourceHandler() throws IOException, InterruptedException {
        runTest("/cachedresource/range.txt", false);
    }
}

