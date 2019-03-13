/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.nifi.remote.client.socket;


import SiteToSiteClient.StandardSiteToSiteClientConfig;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import java.io.ByteArrayInputStream;
import java.util.LinkedHashSet;
import java.util.Set;
import org.apache.nifi.remote.client.SiteToSiteClient;
import org.apache.nifi.remote.client.SiteToSiteClientConfig;
import org.apache.nifi.stream.io.ByteArrayOutputStream;
import org.junit.Assert;
import org.junit.Test;


public class TestSiteToSiteClient {
    @Test
    public void testSerialization() {
        final SiteToSiteClientConfig clientConfig = new SiteToSiteClient.Builder().url("http://localhost:8080/nifi").portName("input").buildConfig();
        final Kryo kryo = new Kryo();
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final Output output = new Output(out);
        try {
            kryo.writeObject(output, clientConfig);
        } finally {
            output.close();
        }
        final ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
        final Input input = new Input(in);
        try {
            SiteToSiteClientConfig clientConfig2 = kryo.readObject(input, StandardSiteToSiteClientConfig.class);
            Assert.assertEquals(clientConfig.getUrls(), clientConfig2.getUrls());
        } finally {
            input.close();
        }
    }

    @Test
    public void testGetUrlBackwardCompatibility() {
        final Set<String> urls = new LinkedHashSet<>();
        urls.add("http://node1:8080/nifi");
        urls.add("http://node2:8080/nifi");
        final SiteToSiteClientConfig config = new SiteToSiteClient.Builder().urls(urls).buildConfig();
        Assert.assertEquals("http://node1:8080/nifi", config.getUrl());
        Assert.assertEquals(urls, config.getUrls());
    }
}

