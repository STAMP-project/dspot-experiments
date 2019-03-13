/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.yarn.server.webproxy.amfilter;


import AmFilterInitializer.RM_HA_URLS;
import AmIpFilter.PROXY_HOSTS;
import AmIpFilter.PROXY_HOSTS_DELIMITER;
import AmIpFilter.PROXY_URI_BASES;
import AmIpFilter.PROXY_URI_BASES_DELIMITER;
import HttpConfig.Policy.HTTPS_ONLY;
import YarnConfiguration.PROXY_ADDRESS;
import YarnConfiguration.RM_HA_ENABLED;
import YarnConfiguration.RM_HA_IDS;
import YarnConfiguration.RM_HOSTNAME;
import YarnConfiguration.RM_WEBAPP_ADDRESS;
import YarnConfiguration.YARN_HTTP_POLICY_KEY;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.http.FilterContainer;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.webapp.util.WebAppUtils;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test class for {@Link AmFilterInitializer}.
 */
public class TestAmFilterInitializer {
    @Test
    public void testInitFilter() {
        // Check PROXY_ADDRESS
        TestAmFilterInitializer.MockFilterContainer con = new TestAmFilterInitializer.MockFilterContainer();
        Configuration conf = new Configuration(false);
        conf.set(PROXY_ADDRESS, "host1:1000");
        AmFilterInitializer afi = new TestAmFilterInitializer.MockAmFilterInitializer();
        Assert.assertNull(con.givenParameters);
        afi.initFilter(con, conf);
        Assert.assertEquals(2, con.givenParameters.size());
        Assert.assertEquals("host1", con.givenParameters.get(PROXY_HOSTS));
        Assert.assertEquals("http://host1:1000/foo", con.givenParameters.get(PROXY_URI_BASES));
        Assert.assertEquals(null, con.givenParameters.get(RM_HA_URLS));
        // Check a single RM_WEBAPP_ADDRESS
        con = new TestAmFilterInitializer.MockFilterContainer();
        conf = new Configuration(false);
        conf.set(RM_WEBAPP_ADDRESS, "host2:2000");
        afi = new TestAmFilterInitializer.MockAmFilterInitializer();
        Assert.assertNull(con.givenParameters);
        afi.initFilter(con, conf);
        Assert.assertEquals(2, con.givenParameters.size());
        Assert.assertEquals("host2", con.givenParameters.get(PROXY_HOSTS));
        Assert.assertEquals("http://host2:2000/foo", con.givenParameters.get(PROXY_URI_BASES));
        Assert.assertEquals(null, con.givenParameters.get(RM_HA_URLS));
        // Check multiple RM_WEBAPP_ADDRESSes (RM HA)
        con = new TestAmFilterInitializer.MockFilterContainer();
        conf = new Configuration(false);
        conf.setBoolean(RM_HA_ENABLED, true);
        conf.set(RM_HA_IDS, "rm1,rm2,rm3");
        conf.set(((YarnConfiguration.RM_WEBAPP_ADDRESS) + ".rm1"), "host2:2000");
        conf.set(((YarnConfiguration.RM_WEBAPP_ADDRESS) + ".rm2"), "host3:3000");
        conf.set(((YarnConfiguration.RM_WEBAPP_ADDRESS) + ".rm3"), "host4:4000");
        afi = new TestAmFilterInitializer.MockAmFilterInitializer();
        Assert.assertNull(con.givenParameters);
        afi.initFilter(con, conf);
        Assert.assertEquals(3, con.givenParameters.size());
        String[] proxyHosts = con.givenParameters.get(PROXY_HOSTS).split(PROXY_HOSTS_DELIMITER);
        Assert.assertEquals(3, proxyHosts.length);
        Arrays.sort(proxyHosts);
        Assert.assertEquals("host2", proxyHosts[0]);
        Assert.assertEquals("host3", proxyHosts[1]);
        Assert.assertEquals("host4", proxyHosts[2]);
        String[] proxyBases = con.givenParameters.get(PROXY_URI_BASES).split(PROXY_URI_BASES_DELIMITER);
        Assert.assertEquals(3, proxyBases.length);
        Arrays.sort(proxyBases);
        Assert.assertEquals("http://host2:2000/foo", proxyBases[0]);
        Assert.assertEquals("http://host3:3000/foo", proxyBases[1]);
        Assert.assertEquals("http://host4:4000/foo", proxyBases[2]);
        Assert.assertEquals("host2:2000,host3:3000,host4:4000", con.givenParameters.get(RM_HA_URLS));
        // Check multiple RM_WEBAPP_ADDRESSes (RM HA) with HTTPS
        con = new TestAmFilterInitializer.MockFilterContainer();
        conf = new Configuration(false);
        conf.set(YARN_HTTP_POLICY_KEY, HTTPS_ONLY.toString());
        conf.setBoolean(RM_HA_ENABLED, true);
        conf.set(RM_HA_IDS, "rm1,rm2");
        conf.set(((YarnConfiguration.RM_WEBAPP_HTTPS_ADDRESS) + ".rm1"), "host5:5000");
        conf.set(((YarnConfiguration.RM_WEBAPP_HTTPS_ADDRESS) + ".rm2"), "host6:6000");
        afi = new TestAmFilterInitializer.MockAmFilterInitializer();
        Assert.assertNull(con.givenParameters);
        afi.initFilter(con, conf);
        Assert.assertEquals(3, con.givenParameters.size());
        proxyHosts = con.givenParameters.get(PROXY_HOSTS).split(PROXY_HOSTS_DELIMITER);
        Assert.assertEquals(2, proxyHosts.length);
        Arrays.sort(proxyHosts);
        Assert.assertEquals("host5", proxyHosts[0]);
        Assert.assertEquals("host6", proxyHosts[1]);
        proxyBases = con.givenParameters.get(PROXY_URI_BASES).split(PROXY_URI_BASES_DELIMITER);
        Assert.assertEquals(2, proxyBases.length);
        Arrays.sort(proxyBases);
        Assert.assertEquals("https://host5:5000/foo", proxyBases[0]);
        Assert.assertEquals("https://host6:6000/foo", proxyBases[1]);
        Assert.assertEquals("host5:5000,host6:6000", con.givenParameters.get(RM_HA_URLS));
    }

    @Test
    public void testGetProxyHostsAndPortsForAmFilter() {
        // Check no configs given
        Configuration conf = new Configuration(false);
        List<String> proxyHosts = WebAppUtils.getProxyHostsAndPortsForAmFilter(conf);
        Assert.assertEquals(1, proxyHosts.size());
        Assert.assertEquals(WebAppUtils.getResolvedRMWebAppURLWithoutScheme(conf), proxyHosts.get(0));
        // Check conf in which only RM hostname is set
        conf = new Configuration(false);
        conf.set(RM_WEBAPP_ADDRESS, "${yarn.resourcemanager.hostname}:8088");// default in yarn-default.xml

        conf.set(RM_HOSTNAME, "host1");
        proxyHosts = WebAppUtils.getProxyHostsAndPortsForAmFilter(conf);
        Assert.assertEquals(1, proxyHosts.size());
        Assert.assertEquals("host1:8088", proxyHosts.get(0));
        // Check PROXY_ADDRESS has priority
        conf = new Configuration(false);
        conf.set(PROXY_ADDRESS, "host1:1000");
        conf.setBoolean(RM_HA_ENABLED, true);
        conf.set(RM_HA_IDS, "rm1,rm2,rm3");
        conf.set(((YarnConfiguration.RM_WEBAPP_ADDRESS) + ".rm1"), "host2:2000");
        conf.set(((YarnConfiguration.RM_WEBAPP_ADDRESS) + ".rm2"), "host3:3000");
        conf.set(((YarnConfiguration.RM_WEBAPP_ADDRESS) + ".rm3"), "host4:4000");
        proxyHosts = WebAppUtils.getProxyHostsAndPortsForAmFilter(conf);
        Assert.assertEquals(1, proxyHosts.size());
        Assert.assertEquals("host1:1000", proxyHosts.get(0));
        // Check getting a single RM_WEBAPP_ADDRESS
        conf = new Configuration(false);
        conf.set(RM_WEBAPP_ADDRESS, "host2:2000");
        proxyHosts = WebAppUtils.getProxyHostsAndPortsForAmFilter(conf);
        Assert.assertEquals(1, proxyHosts.size());
        Collections.sort(proxyHosts);
        Assert.assertEquals("host2:2000", proxyHosts.get(0));
        // Check getting multiple RM_WEBAPP_ADDRESSes (RM HA)
        conf = new Configuration(false);
        conf.setBoolean(RM_HA_ENABLED, true);
        conf.set(RM_HA_IDS, "rm1,rm2,rm3");
        conf.set(((YarnConfiguration.RM_WEBAPP_ADDRESS) + ".rm1"), "host2:2000");
        conf.set(((YarnConfiguration.RM_WEBAPP_ADDRESS) + ".rm2"), "host3:3000");
        conf.set(((YarnConfiguration.RM_WEBAPP_ADDRESS) + ".rm3"), "host4:4000");
        conf.set(((YarnConfiguration.RM_WEBAPP_ADDRESS) + ".rm4"), "dummy");
        conf.set(((YarnConfiguration.RM_WEBAPP_HTTPS_ADDRESS) + ".rm1"), "host5:5000");
        conf.set(((YarnConfiguration.RM_WEBAPP_HTTPS_ADDRESS) + ".rm2"), "host6:6000");
        proxyHosts = WebAppUtils.getProxyHostsAndPortsForAmFilter(conf);
        Assert.assertEquals(3, proxyHosts.size());
        Collections.sort(proxyHosts);
        Assert.assertEquals("host2:2000", proxyHosts.get(0));
        Assert.assertEquals("host3:3000", proxyHosts.get(1));
        Assert.assertEquals("host4:4000", proxyHosts.get(2));
        // Check getting multiple RM_WEBAPP_ADDRESSes (RM HA) with HTTPS
        conf = new Configuration(false);
        conf.set(YARN_HTTP_POLICY_KEY, HTTPS_ONLY.toString());
        conf.setBoolean(RM_HA_ENABLED, true);
        conf.set(RM_HA_IDS, "rm1,rm2,rm3,dummy");
        conf.set(((YarnConfiguration.RM_WEBAPP_ADDRESS) + ".rm1"), "host2:2000");
        conf.set(((YarnConfiguration.RM_WEBAPP_ADDRESS) + ".rm2"), "host3:3000");
        conf.set(((YarnConfiguration.RM_WEBAPP_ADDRESS) + ".rm3"), "host4:4000");
        conf.set(((YarnConfiguration.RM_WEBAPP_HTTPS_ADDRESS) + ".rm1"), "host5:5000");
        conf.set(((YarnConfiguration.RM_WEBAPP_HTTPS_ADDRESS) + ".rm2"), "host6:6000");
        proxyHosts = WebAppUtils.getProxyHostsAndPortsForAmFilter(conf);
        Assert.assertEquals(2, proxyHosts.size());
        Collections.sort(proxyHosts);
        Assert.assertEquals("host5:5000", proxyHosts.get(0));
        Assert.assertEquals("host6:6000", proxyHosts.get(1));
        // Check config without explicit RM_WEBAPP_ADDRESS settings (RM HA)
        conf = new Configuration(false);
        conf.setBoolean(RM_HA_ENABLED, true);
        conf.set(RM_HA_IDS, "rm1,rm2,rm3");
        conf.set(((YarnConfiguration.RM_HOSTNAME) + ".rm1"), "host2");
        conf.set(((YarnConfiguration.RM_HOSTNAME) + ".rm2"), "host3");
        conf.set(((YarnConfiguration.RM_HOSTNAME) + ".rm3"), "host4");
        conf.set(((YarnConfiguration.RM_HOSTNAME) + ".rm4"), "dummy");
        proxyHosts = WebAppUtils.getProxyHostsAndPortsForAmFilter(conf);
        Assert.assertEquals(3, proxyHosts.size());
        Collections.sort(proxyHosts);
        Assert.assertEquals(("host2:" + (YarnConfiguration.DEFAULT_RM_WEBAPP_PORT)), proxyHosts.get(0));
        Assert.assertEquals(("host3:" + (YarnConfiguration.DEFAULT_RM_WEBAPP_PORT)), proxyHosts.get(1));
        Assert.assertEquals(("host4:" + (YarnConfiguration.DEFAULT_RM_WEBAPP_PORT)), proxyHosts.get(2));
        // Check config without explicit RM_WEBAPP_HTTPS_ADDRESS settings (RM HA)
        conf = new Configuration(false);
        conf.set(YARN_HTTP_POLICY_KEY, HTTPS_ONLY.toString());
        conf.setBoolean(RM_HA_ENABLED, true);
        conf.set(RM_HA_IDS, "rm1,rm2,rm3");
        conf.set(((YarnConfiguration.RM_HOSTNAME) + ".rm1"), "host2");
        conf.set(((YarnConfiguration.RM_HOSTNAME) + ".rm2"), "host3");
        conf.set(((YarnConfiguration.RM_HOSTNAME) + ".rm3"), "host4");
        conf.set(((YarnConfiguration.RM_HOSTNAME) + ".rm4"), "dummy");
        proxyHosts = WebAppUtils.getProxyHostsAndPortsForAmFilter(conf);
        Assert.assertEquals(3, proxyHosts.size());
        Collections.sort(proxyHosts);
        Assert.assertEquals(("host2:" + (YarnConfiguration.DEFAULT_RM_WEBAPP_HTTPS_PORT)), proxyHosts.get(0));
        Assert.assertEquals(("host3:" + (YarnConfiguration.DEFAULT_RM_WEBAPP_HTTPS_PORT)), proxyHosts.get(1));
        Assert.assertEquals(("host4:" + (YarnConfiguration.DEFAULT_RM_WEBAPP_HTTPS_PORT)), proxyHosts.get(2));
    }

    class MockAmFilterInitializer extends AmFilterInitializer {
        @Override
        protected String getApplicationWebProxyBase() {
            return "/foo";
        }
    }

    class MockFilterContainer implements FilterContainer {
        Map<String, String> givenParameters;

        @Override
        public void addFilter(String name, String classname, Map<String, String> parameters) {
            givenParameters = parameters;
        }

        @Override
        public void addGlobalFilter(String name, String classname, Map<String, String> parameters) {
        }
    }
}

