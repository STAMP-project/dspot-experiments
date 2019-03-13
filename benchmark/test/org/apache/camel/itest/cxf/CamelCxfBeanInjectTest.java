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
package org.apache.camel.itest.cxf;


import org.apache.camel.test.AvailablePortFinder;
import org.apache.camel.test.karaf.AbstractFeatureTest;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.junit.PaxExam;


@RunWith(PaxExam.class)
@Ignore("Flaky on CI server")
public class CamelCxfBeanInjectTest extends AbstractFeatureTest {
    private static final int PORT = AvailablePortFinder.getNextAvailable(30000);

    private static final String ENDPOINT_ADDRESS = String.format("http://localhost:%s/CamelCxfBeanInjectTest/router", CamelCxfBeanInjectTest.PORT);

    @Test
    public void testReverseProxy() {
        SimpleService client = createClient();
        setHttpHeaders(client, "X-Forwarded-Proto", "https");
        String result = client.op("test");
        Assert.assertEquals("Scheme should be set to 'https'", "scheme: https, x-forwarded-proto: https", result);
    }
}

