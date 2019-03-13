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
package org.apache.camel.component.beanstalk;


import com.surftools.BeanstalkClient.Client;
import org.junit.Assert;
import org.junit.Test;


public class ConnectionSettingsTest {
    @Test
    public void parseUriTest() {
        final ConnectionSettingsFactory factory = BeanstalkComponent.getConnectionSettingsFactory();
        Assert.assertEquals("Full URI", new ConnectionSettings("host.domain.tld", 11300, "someTube"), factory.parseUri("host.domain.tld:11300/someTube"));
        Assert.assertEquals("No port", new ConnectionSettings("host.domain.tld", Client.DEFAULT_PORT, "someTube"), factory.parseUri("host.domain.tld/someTube"));
        Assert.assertEquals("Only tube", new ConnectionSettings(Client.DEFAULT_HOST, Client.DEFAULT_PORT, "someTube"), factory.parseUri("someTube"));
    }

    @Test
    public void parseTubesTest() {
        final ConnectionSettingsFactory factory = BeanstalkComponent.getConnectionSettingsFactory();
        Assert.assertArrayEquals("Full URI", new String[]{ "tube1", "tube2" }, factory.parseUri("host:90/tube1+tube2").tubes);
        Assert.assertArrayEquals("No port", new String[]{ "tube1", "tube2" }, factory.parseUri("host/tube1+tube2").tubes);
        Assert.assertArrayEquals("Only tubes", new String[]{ "tube1", "tube2" }, factory.parseUri("tube1+tube2").tubes);
        Assert.assertArrayEquals("Empty URI", new String[0], factory.parseUri("").tubes);
    }

    @Test(expected = IllegalArgumentException.class)
    public void notValidHost() {
        final ConnectionSettingsFactory factory = BeanstalkComponent.getConnectionSettingsFactory();
        Assert.fail(String.format("Calling on not valid URI must raise exception, but got result %s", factory.parseUri("not_valid?host/tube?")));
    }
}

