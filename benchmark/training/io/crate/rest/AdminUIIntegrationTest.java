/**
 * Licensed to Crate under one or more contributor license agreements.
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.  Crate licenses this file
 * to you under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.  You may
 * obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.  See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * However, if you have executed another commercial license agreement
 * with Crate these terms will supersede the license and you may use the
 * software solely pursuant to the terms of the relevant commercial
 * agreement.
 */
package io.crate.rest;


import ESIntegTestCase.ClusterScope;
import ESIntegTestCase.Scope;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import org.apache.http.Header;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.message.BasicHeader;
import org.hamcrest.Matchers;
import org.hamcrest.core.Is;
import org.junit.Test;


@ClusterScope(scope = Scope.SUITE, numDataNodes = 2)
public class AdminUIIntegrationTest extends AdminUIHttpIntegrationTest {
    @Test
    public void testNonBrowserRequestToRoot() throws IOException {
        // request to root
        AdminUIIntegrationTest.assertIsJsonInfoResponse(get(""));
    }

    @Test
    public void testBrowserJsonRequestToRoot() throws IOException {
        Header[] headers = new Header[]{ AdminUIHttpIntegrationTest.browserHeader(), new BasicHeader("Accept", "application/json") };
        AdminUIIntegrationTest.assertIsJsonInfoResponse(get("/", headers));
    }

    @Test
    public void testLegacyRedirect() throws IOException, URISyntaxException {
        // request to '/admin' is redirected to '/index.html'
        Header[] headers = new Header[]{ AdminUIHttpIntegrationTest.browserHeader() };
        List<URI> allRedirectLocations = getAllRedirectLocations("/admin", headers);
        // all redirect locations should not be null
        assertThat(allRedirectLocations, Matchers.notNullValue());
        // all redirect locations should contain the crateAdminUI URI
        assertThat(allRedirectLocations.contains(adminURI()), Is.is(true));
    }

    @Test
    public void testPluginURLRedirect() throws IOException, URISyntaxException {
        // request to '/_plugin/crate-admin' is redirected to '/'
        Header[] headers = new Header[]{ AdminUIHttpIntegrationTest.browserHeader() };
        List<URI> allRedirectLocations = getAllRedirectLocations("/_plugin/crate-admin", headers);
        // all redirect locations should contain the crateAdminUI URI
        assertThat(allRedirectLocations.contains(adminURI()), Is.is(true));
    }

    @Test
    public void testPluginURLRedirectReturnsIndex() throws IOException, URISyntaxException {
        // request to '/_plugins/crate-admin' is redirected to '/index.html'
        AdminUIIntegrationTest.assertIsIndexResponse(browserGet("/_plugin/crate-admin"));
    }

    @Test
    public void testPostForbidden() throws IOException {
        CloseableHttpResponse response = post("/static/");
        // status should be 403 FORBIDDEN
        assertThat(response.getStatusLine().getStatusCode(), Is.is(403));
    }

    @Test
    public void testGetHTML() throws IOException {
        AdminUIIntegrationTest.assertIsIndexResponse(browserGet("/"));
        AdminUIIntegrationTest.assertIsIndexResponse(browserGet("/index.html"));
        AdminUIIntegrationTest.assertIsIndexResponse(browserGet("//index.html"));
    }

    @Test
    public void testNotFound() throws Exception {
        CloseableHttpResponse response = browserGet("/static/does/not/exist.html");
        assertThat(response.getStatusLine().getStatusCode(), Is.is(404));
    }
}

