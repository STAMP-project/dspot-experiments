/**
 * Copyright 2012-2019 The Feign Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package feign.mock;


import HttpMethod.GET;
import HttpMethod.POST;
import feign.Request;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class RequestKeyTest {
    private RequestKey requestKey;

    @Test
    public void builder() throws Exception {
        Assert.assertThat(requestKey.getMethod(), Matchers.equalTo(GET));
        Assert.assertThat(requestKey.getUrl(), Matchers.equalTo("a"));
        Assert.assertThat(requestKey.getHeaders().size(), Matchers.is(1));
        Assert.assertThat(requestKey.getHeaders().fetch("my-header"), Matchers.equalTo(((Collection<String>) (Arrays.asList("val")))));
        Assert.assertThat(requestKey.getCharset(), Matchers.equalTo(StandardCharsets.UTF_16));
    }

    @Test
    public void create() throws Exception {
        Map<String, Collection<String>> map = new HashMap<String, Collection<String>>();
        map.put("my-header", Arrays.asList("val"));
        Request request = Request.create(Request.HttpMethod.GET, "a", map, "content".getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_16);
        requestKey = RequestKey.create(request);
        Assert.assertThat(requestKey.getMethod(), Matchers.equalTo(GET));
        Assert.assertThat(requestKey.getUrl(), Matchers.equalTo("a"));
        Assert.assertThat(requestKey.getHeaders().size(), Matchers.is(1));
        Assert.assertThat(requestKey.getHeaders().fetch("my-header"), Matchers.equalTo(((Collection<String>) (Arrays.asList("val")))));
        Assert.assertThat(requestKey.getCharset(), Matchers.equalTo(StandardCharsets.UTF_16));
        Assert.assertThat(requestKey.getBody(), Matchers.equalTo("content".getBytes(StandardCharsets.UTF_8)));
    }

    @Test
    public void checkHashes() {
        RequestKey requestKey1 = RequestKey.builder(GET, "a").build();
        RequestKey requestKey2 = RequestKey.builder(GET, "b").build();
        Assert.assertThat(requestKey1.hashCode(), Matchers.not(Matchers.equalTo(requestKey2.hashCode())));
        Assert.assertThat(requestKey1, Matchers.not(Matchers.equalTo(requestKey2)));
    }

    @Test
    public void equalObject() {
        Assert.assertThat(requestKey, Matchers.not(Matchers.equalTo(new Object())));
    }

    @Test
    public void equalNull() {
        Assert.assertThat(requestKey, Matchers.not(Matchers.equalTo(null)));
    }

    @Test
    public void equalPost() {
        RequestKey requestKey1 = RequestKey.builder(GET, "a").build();
        RequestKey requestKey2 = RequestKey.builder(POST, "a").build();
        Assert.assertThat(requestKey1.hashCode(), Matchers.not(Matchers.equalTo(requestKey2.hashCode())));
        Assert.assertThat(requestKey1, Matchers.not(Matchers.equalTo(requestKey2)));
    }

    @Test
    public void equalSelf() {
        Assert.assertThat(requestKey.hashCode(), Matchers.equalTo(requestKey.hashCode()));
        Assert.assertThat(requestKey, Matchers.equalTo(requestKey));
    }

    @Test
    public void equalMinimum() {
        RequestKey requestKey2 = RequestKey.builder(GET, "a").build();
        Assert.assertThat(requestKey.hashCode(), Matchers.equalTo(requestKey2.hashCode()));
        Assert.assertThat(requestKey, Matchers.equalTo(requestKey2));
    }

    @Test
    public void equalExtra() {
        RequestHeaders headers = RequestHeaders.builder().add("my-other-header", "other value").build();
        RequestKey requestKey2 = RequestKey.builder(GET, "a").headers(headers).charset(StandardCharsets.ISO_8859_1).build();
        Assert.assertThat(requestKey.hashCode(), Matchers.equalTo(requestKey2.hashCode()));
        Assert.assertThat(requestKey, Matchers.equalTo(requestKey2));
    }

    @Test
    public void equalsExtended() {
        RequestKey requestKey2 = RequestKey.builder(GET, "a").build();
        Assert.assertThat(requestKey.hashCode(), Matchers.equalTo(requestKey2.hashCode()));
        Assert.assertThat(requestKey.equalsExtended(requestKey2), Matchers.equalTo(true));
    }

    @Test
    public void equalsExtendedExtra() {
        RequestHeaders headers = RequestHeaders.builder().add("my-other-header", "other value").build();
        RequestKey requestKey2 = RequestKey.builder(GET, "a").headers(headers).charset(StandardCharsets.ISO_8859_1).build();
        Assert.assertThat(requestKey.hashCode(), Matchers.equalTo(requestKey2.hashCode()));
        Assert.assertThat(requestKey.equalsExtended(requestKey2), Matchers.equalTo(false));
    }

    @Test
    public void testToString() throws Exception {
        Assert.assertThat(requestKey.toString(), Matchers.startsWith("Request [GET a: "));
        Assert.assertThat(requestKey.toString(), Matchers.both(Matchers.containsString(" with my-header=[val] ")).and(Matchers.containsString(" UTF-16]")));
    }

    @Test
    public void testToStringSimple() throws Exception {
        requestKey = RequestKey.builder(GET, "a").build();
        Assert.assertThat(requestKey.toString(), Matchers.startsWith("Request [GET a: "));
        Assert.assertThat(requestKey.toString(), Matchers.both(Matchers.containsString(" without ")).and(Matchers.containsString(" no charset")));
    }
}

/**
 *
 */
