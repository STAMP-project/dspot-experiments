/**
 * Copyright ? 2010-2017 Nokia
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
package org.jsonschema2pojo;


import com.fasterxml.jackson.databind.JsonNode;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import java.net.URI;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;


/**
 *
 *
 * @author {@link "https://github.com/s13o" "s13o"}
 * @since 3/17/2017
 */
public class ContentResolverNetworkTest {
    private static final String ADDRESS = "localhost";

    @Rule
    public WireMockRule server = new WireMockRule(options().dynamicPort().bindAddress(ContentResolverNetworkTest.ADDRESS).usingFilesUnderClasspath("wiremock"));

    private ContentResolver resolver = new ContentResolver();

    @Test(expected = IllegalArgumentException.class)
    public void brokenLinkCausesIllegalArgumentException() {
        URI brokenHttpUri = URI.create((((("http://" + (ContentResolverNetworkTest.ADDRESS)) + ":") + (server.port())) + "/address404.json"));
        resolver.resolve(brokenHttpUri);
    }

    @Test(expected = IllegalArgumentException.class)
    public void serverErrorCausesIllegalArgumentException() {
        URI brokenHttpUri = URI.create((((("http://" + (ContentResolverNetworkTest.ADDRESS)) + ":") + (server.port())) + "/address500.json"));
        resolver.resolve(brokenHttpUri);
    }

    @Test
    public void httpLinkIsResolvedToContent() {
        URI httpUri = URI.create((((("http://" + (ContentResolverNetworkTest.ADDRESS)) + ":") + (server.port())) + "/address.json"));
        JsonNode uriContent = resolver.resolve(httpUri);
        Assert.assertThat(uriContent.path("description").asText().length(), Matchers.is(Matchers.greaterThan(0)));
    }
}

