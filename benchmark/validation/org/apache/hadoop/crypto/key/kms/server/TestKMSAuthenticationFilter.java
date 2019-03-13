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
package org.apache.hadoop.crypto.key.kms.server;


import DelegationTokenAuthenticationHandler.TOKEN_KIND;
import KMSAuthenticationFilter.AUTH_TYPE;
import KMSDelegationToken.TOKEN_KIND_STR;
import java.util.Properties;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.security.token.delegation.web.PseudoDelegationTokenAuthenticationHandler;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test KMS Authentication Filter.
 */
public class TestKMSAuthenticationFilter {
    @Test
    public void testConfiguration() throws Exception {
        Configuration conf = new Configuration();
        conf.set("hadoop.kms.authentication.type", "simple");
        Properties prop = new KMSAuthenticationFilter().getKMSConfiguration(conf);
        Assert.assertEquals(prop.getProperty(AUTH_TYPE), PseudoDelegationTokenAuthenticationHandler.class.getName());
        Assert.assertEquals(prop.getProperty(TOKEN_KIND), TOKEN_KIND_STR);
    }
}

