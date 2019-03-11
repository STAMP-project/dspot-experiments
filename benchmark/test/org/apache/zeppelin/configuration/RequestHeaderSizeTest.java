/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.zeppelin.configuration;


import HttpStatus.OK_200;
import HttpStatus.REQUEST_HEADER_FIELDS_TOO_LARGE_431;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.zeppelin.rest.AbstractTestRestApi;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;


public class RequestHeaderSizeTest extends AbstractTestRestApi {
    private static final int REQUEST_HEADER_MAX_SIZE = 20000;

    @Test
    public void increased_request_header_size_do_not_cause_431_when_request_size_is_over_8K() throws Exception {
        HttpClient httpClient = new HttpClient();
        GetMethod getMethod = new GetMethod(((AbstractTestRestApi.getUrlToTest()) + "/version"));
        String headerValue = RandomStringUtils.randomAlphanumeric(((RequestHeaderSizeTest.REQUEST_HEADER_MAX_SIZE) - 2000));
        getMethod.setRequestHeader("not_too_large_header", headerValue);
        int httpCode = httpClient.executeMethod(getMethod);
        MatcherAssert.assertThat(httpCode, CoreMatchers.is(OK_200));
        getMethod = new GetMethod(((AbstractTestRestApi.getUrlToTest()) + "/version"));
        headerValue = RandomStringUtils.randomAlphanumeric(((RequestHeaderSizeTest.REQUEST_HEADER_MAX_SIZE) + 2000));
        getMethod.setRequestHeader("too_large_header", headerValue);
        httpCode = httpClient.executeMethod(getMethod);
        MatcherAssert.assertThat(httpCode, CoreMatchers.is(REQUEST_HEADER_FIELDS_TOO_LARGE_431));
    }
}

