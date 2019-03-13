/**
 * Copyright 2018 Red Hat, Inc. and/or its affiliates
 * and other contributors as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.keycloak.testsuite.admin.client.authorization;


import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.undertow.Undertow;
import java.io.ByteArrayInputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.keycloak.adapters.spi.HttpFacade;
import org.keycloak.testsuite.AbstractKeycloakTest;
import org.keycloak.util.JsonSerialization;


/**
 *
 *
 * @author <a href="mailto:psilva@redhat.com">Pedro Igor</a>
 */
public class ClaimInformationPointProviderTest extends AbstractKeycloakTest {
    private static Undertow httpService;

    @Test
    public void testBasicClaimsInformationPoint() {
        HttpFacade httpFacade = createHttpFacade();
        Map<String, List<String>> claims = getClaimInformationProviderForPath("/claims-provider", "claims").resolve(httpFacade);
        Assert.assertEquals("parameter-a", claims.get("claim-from-request-parameter").get(0));
        Assert.assertEquals("header-b", claims.get("claim-from-header").get(0));
        Assert.assertEquals("cookie-c", claims.get("claim-from-cookie").get(0));
        Assert.assertEquals("user-remote-addr", claims.get("claim-from-remoteAddr").get(0));
        Assert.assertEquals("GET", claims.get("claim-from-method").get(0));
        Assert.assertEquals("/app/request-uri", claims.get("claim-from-uri").get(0));
        Assert.assertEquals("/request-relative-path", claims.get("claim-from-relativePath").get(0));
        Assert.assertEquals("true", claims.get("claim-from-secure").get(0));
        Assert.assertEquals("static value", claims.get("claim-from-static-value").get(0));
        Assert.assertEquals("static", claims.get("claim-from-multiple-static-value").get(0));
        Assert.assertEquals("value", claims.get("claim-from-multiple-static-value").get(1));
        Assert.assertEquals("Test param-other-claims-value1 and parameter-a", claims.get("param-replace-multiple-placeholder").get(0));
    }

    @Test
    public void testBodyJsonClaimsInformationPoint() throws Exception {
        Map<String, List<String>> headers = new HashMap<>();
        headers.put("Content-Type", Arrays.asList("application/json"));
        ObjectMapper mapper = JsonSerialization.mapper;
        JsonParser parser = mapper.getFactory().createParser("{\"a\": {\"b\": {\"c\": \"c-value\"}}, \"d\": [\"d-value1\", \"d-value2\"]}");
        TreeNode treeNode = mapper.readTree(parser);
        HttpFacade httpFacade = createHttpFacade(headers, new ByteArrayInputStream(treeNode.toString().getBytes()));
        Map<String, List<String>> claims = getClaimInformationProviderForPath("/claims-provider", "claims").resolve(httpFacade);
        Assert.assertEquals("c-value", claims.get("claim-from-json-body-object").get(0));
        Assert.assertEquals("d-value2", claims.get("claim-from-json-body-array").get(0));
    }

    @Test
    public void testBodyClaimsInformationPoint() {
        HttpFacade httpFacade = createHttpFacade(new HashMap<>(), new ByteArrayInputStream("raw-body-text".getBytes()));
        Map<String, List<String>> claims = getClaimInformationProviderForPath("/claims-provider", "claims").resolve(httpFacade);
        Assert.assertEquals("raw-body-text", claims.get("claim-from-body").get(0));
    }

    @Test
    public void testHttpClaimInformationPointProviderWithoutClaims() {
        HttpFacade httpFacade = createHttpFacade();
        Map<String, List<String>> claims = getClaimInformationProviderForPath("/http-get-claim-provider", "http").resolve(httpFacade);
        Assert.assertEquals("a-value1", claims.get("a").get(0));
        Assert.assertEquals("b-value1", claims.get("b").get(0));
        Assert.assertEquals("d-value1", claims.get("d").get(0));
        Assert.assertEquals("d-value2", claims.get("d").get(1));
        Assert.assertNull(claims.get("claim-a"));
        Assert.assertNull(claims.get("claim-d"));
        Assert.assertNull(claims.get("claim-d0"));
        Assert.assertNull(claims.get("claim-d-all"));
    }

    @Test
    public void testHttpClaimInformationPointProviderWithClaims() {
        HttpFacade httpFacade = createHttpFacade();
        Map<String, List<String>> claims = getClaimInformationProviderForPath("/http-post-claim-provider", "http").resolve(httpFacade);
        Assert.assertEquals("a-value1", claims.get("claim-a").get(0));
        Assert.assertEquals("d-value1", claims.get("claim-d").get(0));
        Assert.assertEquals("d-value2", claims.get("claim-d").get(1));
        Assert.assertEquals("d-value1", claims.get("claim-d0").get(0));
        Assert.assertEquals("d-value1", claims.get("claim-d-all").get(0));
        Assert.assertEquals("d-value2", claims.get("claim-d-all").get(1));
        Assert.assertNull(claims.get("a"));
        Assert.assertNull(claims.get("b"));
        Assert.assertNull(claims.get("d"));
    }
}

