/**
 * Copyright 2017 NAVER Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.navercorp.pinpoint.web.filter;


import ServiceTypeProperty.RECORD_STATISTICS;
import com.navercorp.pinpoint.common.service.AnnotationKeyRegistryService;
import com.navercorp.pinpoint.common.service.ServiceTypeRegistryService;
import com.navercorp.pinpoint.common.trace.AnnotationKey;
import com.navercorp.pinpoint.common.trace.AnnotationKeyFactory;
import com.navercorp.pinpoint.common.trace.ServiceType;
import com.navercorp.pinpoint.common.trace.ServiceTypeFactory;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author HyunGil Jeong
 */
public class RpcURLPatternFilterTest {
    private final AnnotationKey TEST_RPC_URL_ANNOTATION_KEY = AnnotationKeyFactory.of((-1), "rpc.url");

    private final short TEST_RPC_SERVICE_TYPE_CODE = 9999;

    private final String TEST_RPC_SERVICE_TYPE_NAME = "TEST_RPC";

    private final ServiceType TEST_RPC_SERVICE_TYPE = ServiceTypeFactory.of(TEST_RPC_SERVICE_TYPE_CODE, TEST_RPC_SERVICE_TYPE_NAME, RECORD_STATISTICS);

    private ServiceTypeRegistryService serviceTypeRegistryService;

    private AnnotationKeyRegistryService annotationKeyRegistryService;

    @Test
    public void emptyPatternShouldReject() {
        // Given
        final String urlPattern = "";
        final String rpcUrl = "http://a.b.c";
        final RpcURLPatternFilter rpcURLPatternFilter = new RpcURLPatternFilter(encode(urlPattern), serviceTypeRegistryService, annotationKeyRegistryService);
        // When
        boolean accept = rpcURLPatternFilter.accept(createTestRpcSpans(rpcUrl));
        // Then
        Assert.assertFalse(accept);
    }

    @Test
    public void testPath() {
        // Given
        final String urlPattern = "/test/**";
        final String rpcUrl = "/test/rpc/path";
        final RpcURLPatternFilter rpcURLPatternFilter = new RpcURLPatternFilter(encode(urlPattern), serviceTypeRegistryService, annotationKeyRegistryService);
        // When
        boolean accept = rpcURLPatternFilter.accept(createTestRpcSpans(rpcUrl));
        // Then
        Assert.assertTrue(accept);
    }

    @Test
    public void testFullUrl() {
        // Given
        final String urlPattern = "/test/**";
        final String rpcUrl = "http://some.test.domain:8080/test/rpc/path";
        final RpcURLPatternFilter rpcURLPatternFilter = new RpcURLPatternFilter(encode(urlPattern), serviceTypeRegistryService, annotationKeyRegistryService);
        // When
        boolean accept = rpcURLPatternFilter.accept(createTestRpcSpans(rpcUrl));
        // Then
        Assert.assertTrue(accept);
    }

    @Test
    public void testDomainAndPath() {
        // Given
        final String urlPattern = "some.test.domain/test/rpc/**";
        final String rpcUrl = "some.test.domain/test/rpc/test?value=11";
        final RpcURLPatternFilter rpcURLPatternFilter = new RpcURLPatternFilter(encode(urlPattern), serviceTypeRegistryService, annotationKeyRegistryService);
        // When
        boolean accept = rpcURLPatternFilter.accept(createTestRpcSpans(rpcUrl));
        // Then
        Assert.assertTrue(accept);
    }

    @Test
    public void testString() {
        // Given
        final String urlPattern = "some*";
        final String rpcUrl = "someName";
        final RpcURLPatternFilter rpcURLPatternFilter = new RpcURLPatternFilter(encode(urlPattern), serviceTypeRegistryService, annotationKeyRegistryService);
        // When
        boolean accept = rpcURLPatternFilter.accept(createTestRpcSpans(rpcUrl));
        // Then
        Assert.assertTrue(accept);
    }

    @Test
    public void testWeirdPath() {
        // Given
        final String urlPattern = ":/**";
        final String rpcUrl = ":/invalid/uri";
        final RpcURLPatternFilter rpcURLPatternFilter = new RpcURLPatternFilter(encode(urlPattern), serviceTypeRegistryService, annotationKeyRegistryService);
        // When
        boolean accept = rpcURLPatternFilter.accept(createTestRpcSpans(rpcUrl));
        // Then
        Assert.assertTrue(accept);
    }
}

