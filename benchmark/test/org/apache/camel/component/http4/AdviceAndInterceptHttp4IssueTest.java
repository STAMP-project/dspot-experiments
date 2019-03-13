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
package org.apache.camel.component.http4;


import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;


/**
 *
 */
public class AdviceAndInterceptHttp4IssueTest extends CamelTestSupport {
    private String simpleProvider = "http4:fakeHTTPADDRESS.com:80?throwExceptionOnFailure=false";

    private String providerWithParameter = "http4:fakeHTTPADDRESS.com:80?throwExceptionOnFailure=false&httpClient.cookieSpec=ignoreCookies";

    private volatile boolean messageIntercepted;

    @Test
    public void testHttp4WithoutHttpClientParameter() throws Exception {
        doTestHttp4Parameter(simpleProvider);
    }

    @Test
    public void testHttp4WithHttpClientParameter() throws Exception {
        doTestHttp4Parameter(providerWithParameter);
    }
}

