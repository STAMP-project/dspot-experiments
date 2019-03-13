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
package org.apache.camel.component.milo.client;


import com.google.common.net.UrlEscapers;
import org.apache.camel.ResolveEndpointFailedException;
import org.apache.camel.component.milo.AbstractMiloServerTest;
import org.apache.camel.component.milo.server.MiloServerComponent;
import org.junit.Test;


/**
 * Testing different ways to specify node IDs
 */
public class NodeIdTest extends AbstractMiloServerTest {
    @Test
    public void testFull1() {
        final String s = String.format("nsu=%s;s=%s", MiloServerComponent.DEFAULT_NAMESPACE_URI, "item-1");
        testUri((("milo-client:tcp://foo:bar@localhost:@@port@@?samplingInterval=1000&node=RAW(" + s) + ")"), MiloServerComponent.DEFAULT_NAMESPACE_URI, "item-1");
    }

    @Test
    public void testFull2() {
        final String s = String.format("ns=%s;s=%s", 1, "item-1");
        testUri((("milo-client:tcp://foo:bar@localhost:@@port@@?samplingInterval=1000&node=RAW(" + s) + ")"), ushort(1), "item-1");
    }

    @Test
    public void testFull3() {
        final String s = String.format("ns=%s;i=%s", 1, 2);
        testUri((("milo-client:tcp://foo:bar@localhost:@@port@@?samplingInterval=1000&node=RAW(" + s) + ")"), ushort(1), uint(2));
    }

    @Test
    public void testFull1NonRaw() {
        final String s = String.format("ns=%s;i=%s", 1, 2);
        testUri(("milo-client:tcp://foo:bar@localhost:@@port@@?samplingInterval=1000&node=" + (UrlEscapers.urlFormParameterEscaper().escape(s))), ushort(1), uint(2));
    }

    @Test
    public void testDocURL() {
        testUri("milo-client://user:password@localhost:12345?node=RAW(nsu=http://foo.bar;s=foo/bar)", "http://foo.bar", "foo/bar");
    }

    @Test(expected = ResolveEndpointFailedException.class)
    public void testMixed() {
        // This must fail since "node" is incomplete
        testUri(("milo-client:tcp://foo:bar@localhost:@@port@@?node=foo&namespaceUri=" + (MiloServerComponent.DEFAULT_NAMESPACE_URI)), null, null);
    }
}

