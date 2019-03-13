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
package org.apache.ambari.server.controller.internal;


import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.junit.Assert;
import org.junit.Test;

import static AppCookieManager.HADOOP_AUTH;


public class AppCookieManagerTest {
    @Test
    public void getCachedKnoxAppCookie() {
        Assert.assertNull(new AppCookieManager().getCachedAppCookie("http://dummy"));
    }

    @Test
    public void getHadoopAuthCookieValueWithNullHeaders() {
        Assert.assertNull(AppCookieManager.getHadoopAuthCookieValue(null));
    }

    @Test
    public void getHadoopAuthCookieValueWitEmptylHeaders() {
        Assert.assertNull(AppCookieManager.getHadoopAuthCookieValue(new Header[0]));
    }

    @Test
    public void getHadoopAuthCookieValueWithValidlHeaders() {
        Header[] headers = new Header[1];
        headers[0] = new BasicHeader("Set-Cookie", ((HADOOP_AUTH) + "=dummyvalue"));
        Assert.assertNotNull(AppCookieManager.getHadoopAuthCookieValue(headers));
    }
}

