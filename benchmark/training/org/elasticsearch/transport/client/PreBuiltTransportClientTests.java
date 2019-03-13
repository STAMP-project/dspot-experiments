/**
 * Licensed to Elasticsearch under one or more contributor
 * license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright
 * ownership. Elasticsearch licenses this file to you under
 * the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.elasticsearch.transport.client;


import Netty4Plugin.NETTY_TRANSPORT_NAME;
import NetworkModule.HTTP_DEFAULT_TYPE_SETTING;
import NetworkModule.TRANSPORT_DEFAULT_TYPE_SETTING;
import com.carrotsearch.randomizedtesting.RandomizedTest;
import java.util.Arrays;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.index.reindex.ReindexPlugin;
import org.elasticsearch.join.ParentJoinPlugin;
import org.elasticsearch.percolator.PercolatorPlugin;
import org.elasticsearch.plugins.Plugin;
import org.elasticsearch.script.mustache.MustachePlugin;
import org.junit.Assert;
import org.junit.Test;


public class PreBuiltTransportClientTests extends RandomizedTest {
    @Test
    public void testPluginInstalled() {
        try (TransportClient client = new PreBuiltTransportClient(Settings.EMPTY)) {
            Settings settings = client.settings();
            Assert.assertEquals(NETTY_TRANSPORT_NAME, HTTP_DEFAULT_TYPE_SETTING.get(settings));
            Assert.assertEquals(NETTY_TRANSPORT_NAME, TRANSPORT_DEFAULT_TYPE_SETTING.get(settings));
        }
    }

    @Test
    public void testInstallPluginTwice() {
        for (Class<? extends Plugin> plugin : Arrays.asList(ParentJoinPlugin.class, ReindexPlugin.class, PercolatorPlugin.class, MustachePlugin.class)) {
            try {
                new PreBuiltTransportClient(Settings.EMPTY, plugin);
                Assert.fail("exception expected");
            } catch (IllegalArgumentException ex) {
                Assert.assertTrue((("Expected message to start with [plugin already exists: ] but was instead [" + (ex.getMessage())) + "]"), ex.getMessage().startsWith("plugin already exists: "));
            }
        }
    }
}

