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


import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.minikdc.MiniKdc;
import org.apache.hadoop.security.authentication.KerberosTestUtils;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


/**
 * Test AmIpFilter. Requests to a no declared hosts should has way through
 * proxy. Another requests can be filtered with (without) user name.
 */
public class TestSecureAmFilter {
    private String proxyHost = "localhost";

    private static final File TEST_ROOT_DIR = new File("target", ((TestSecureAmFilter.class.getName()) + "-root"));

    private static File httpSpnegoKeytabFile = new File(KerberosTestUtils.getKeytabFile());

    private static Configuration rmconf = new Configuration();

    private static String httpSpnegoPrincipal = KerberosTestUtils.getServerPrincipal();

    private static boolean miniKDCStarted = false;

    private static MiniKdc testMiniKDC;

    private class TestAmIpFilter extends AmIpFilter {
        private Set<String> proxyAddresses = null;

        protected Set<String> getProxyAddresses() {
            if ((proxyAddresses) == null) {
                proxyAddresses = new HashSet<String>();
            }
            proxyAddresses.add(proxyHost);
            return proxyAddresses;
        }
    }

    @Test
    public void testFindRedirectUrl() throws Exception {
        final String rm1 = "rm1";
        final String rm2 = "rm2";
        // generate a valid URL
        final String rm1Url = startSecureHttpServer();
        // invalid url
        final String rm2Url = "host2:8088";
        TestSecureAmFilter.TestAmIpFilter filter = new TestSecureAmFilter.TestAmIpFilter();
        TestSecureAmFilter.TestAmIpFilter spy = Mockito.spy(filter);
        // make sure findRedirectUrl() go to HA branch
        spy.proxyUriBases = new HashMap();
        spy.proxyUriBases.put(rm1, rm1Url);
        spy.proxyUriBases.put(rm2, rm2Url);
        spy.rmUrls = new String[]{ rm1, rm2 };
        Assert.assertTrue(isValidUrl(rm1Url));
        Assert.assertFalse(isValidUrl(rm2Url));
        Assert.assertEquals(findRedirectUrl(), rm1Url);
    }
}

