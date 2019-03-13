/**
 * Copyright 2013-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.cloud.netflix.ribbon.support;


import HttpMethod.POST;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Collections;
import org.junit.Test;
import org.springframework.util.LinkedMultiValueMap;


/**
 *
 *
 * @author Andre D?rnbrack
 */
public class RibbonCommandContextTest {
    private static final byte[] TEST_CONTENT = new byte[]{ 42, 42, 42, 42, 42 };

    private RibbonCommandContext ribbonCommandContext;

    @Test
    public void testMultipleReadsOnRequestEntity() throws Exception {
        givenRibbonCommandContextIsSetup();
        InputStream requestEntity = ribbonCommandContext.getRequestEntity();
        assertThat((requestEntity instanceof ResettableServletInputStreamWrapper)).isTrue();
        whenInputStreamIsConsumed(requestEntity);
        assertThat(requestEntity.read()).isEqualTo((-1));
        requestEntity.reset();
        assertThat(requestEntity.read()).isNotEqualTo((-1));
        whenInputStreamIsConsumed(requestEntity);
        assertThat(requestEntity.read()).isEqualTo((-1));
        requestEntity.reset();
        assertThat(requestEntity.read()).isNotEqualTo((-1));
        whenInputStreamIsConsumed(requestEntity);
        assertThat(requestEntity.read()).isEqualTo((-1));
    }

    @Test
    public void testNullSafetyWithNullableParameters() throws Exception {
        LinkedMultiValueMap headers = new LinkedMultiValueMap();
        LinkedMultiValueMap params = new LinkedMultiValueMap();
        RibbonCommandContext testContext = new RibbonCommandContext("serviceId", POST.toString(), "/my/route", true, headers, params, new ByteArrayInputStream(RibbonCommandContextTest.TEST_CONTENT), Collections.<RibbonRequestCustomizer>emptyList(), null, null);
        assertThat(testContext.hashCode()).isNotEqualTo(0);
        assertThat(testContext.toString()).isNotNull();
    }
}

