/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.flume.agent.embedded;


import java.util.Map;
import junit.framework.Assert;
import org.apache.flume.FlumeException;
import org.junit.Test;


public class TestEmbeddedAgentConfiguration {
    private Map<String, String> properties;

    @Test
    public void testFullSourceType() throws Exception {
        doTestExcepted(EmbeddedAgentConfiguration.configure("test1", properties));
    }

    @Test
    public void testMissingSourceType() throws Exception {
        Assert.assertNotNull(properties.remove("source.type"));
        doTestExcepted(EmbeddedAgentConfiguration.configure("test1", properties));
    }

    @Test
    public void testShortSourceType() throws Exception {
        properties.put("source.type", "EMBEDDED");
        doTestExcepted(EmbeddedAgentConfiguration.configure("test1", properties));
    }

    @Test(expected = FlumeException.class)
    public void testBadSource() throws Exception {
        properties.put("source.type", "exec");
        EmbeddedAgentConfiguration.configure("test1", properties);
    }

    @Test(expected = FlumeException.class)
    public void testBadChannel() throws Exception {
        properties.put("channel.type", "jdbc");
        EmbeddedAgentConfiguration.configure("test1", properties);
    }

    @Test(expected = FlumeException.class)
    public void testBadSink() throws Exception {
        properties.put("sink1.type", "hbase");
        EmbeddedAgentConfiguration.configure("test1", properties);
    }

    @Test(expected = FlumeException.class)
    public void testBadSinkProcessor() throws Exception {
        properties.put("processor.type", "bad");
        EmbeddedAgentConfiguration.configure("test1", properties);
    }

    @Test(expected = FlumeException.class)
    public void testNoChannel() throws Exception {
        properties.remove("channel.type");
        EmbeddedAgentConfiguration.configure("test1", properties);
    }

    @Test(expected = FlumeException.class)
    public void testNoSink() throws Exception {
        properties.remove("sink2.type");
        EmbeddedAgentConfiguration.configure("test1", properties);
    }

    @Test(expected = FlumeException.class)
    public void testNoSinkProcessor() throws Exception {
        properties.remove("processor.type");
        EmbeddedAgentConfiguration.configure("test1", properties);
    }

    @Test(expected = FlumeException.class)
    public void testBadKey() throws Exception {
        properties.put("bad.key.name", "bad");
        EmbeddedAgentConfiguration.configure("test1", properties);
    }

    @Test(expected = FlumeException.class)
    public void testSinkNamedLikeSource() throws Exception {
        properties.put("sinks", "source");
        EmbeddedAgentConfiguration.configure("test1", properties);
    }

    @Test(expected = FlumeException.class)
    public void testSinkNamedLikeChannel() throws Exception {
        properties.put("sinks", "channel");
        EmbeddedAgentConfiguration.configure("test1", properties);
    }

    @Test(expected = FlumeException.class)
    public void testSinkNamedLikeProcessor() throws Exception {
        properties.put("sinks", "processor");
        EmbeddedAgentConfiguration.configure("test1", properties);
    }
}

