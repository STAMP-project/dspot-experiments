/**
 * Copyright 2002-2019 the original author or authors.
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
package org.springframework.web.util;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collections;
import org.junit.Assert;
import org.junit.Test;


/**
 * Unit tests for {@link UriComponents}.
 *
 * @author Arjen Poutsma
 * @author Phillip Webb
 * @author Rossen Stoyanchev
 */
public class UriComponentsTests {
    @Test
    public void expandAndEncode() {
        UriComponents uri = UriComponentsBuilder.UriComponentsBuilder.fromPath("/hotel list/{city} specials").queryParam("q", "{value}").build().expand("Z\u00fcrich", "a+b").encode();
        Assert.assertEquals("/hotel%20list/Z%C3%BCrich%20specials?q=a+b", uri.toString());
    }

    @Test
    public void encodeAndExpand() {
        UriComponents uri = UriComponentsBuilder.UriComponentsBuilder.fromPath("/hotel list/{city} specials").queryParam("q", "{value}").encode().build().expand("Z\u00fcrich", "a+b");
        Assert.assertEquals("/hotel%20list/Z%C3%BCrich%20specials?q=a%2Bb", uri.toString());
    }

    @Test
    public void encodeAndExpandPartially() {
        UriComponents uri = UriComponentsBuilder.UriComponentsBuilder.fromPath("/hotel list/{city} specials").queryParam("q", "{value}").encode().uriVariables(Collections.singletonMap("city", "Z\u00fcrich")).build();
        Assert.assertEquals("/hotel%20list/Z%C3%BCrich%20specials?q=a%2Bb", uri.expand("a+b").toString());
    }

    // SPR-17168
    @Test
    public void encodeAndExpandWithDollarSign() {
        UriComponents uri = UriComponentsBuilder.UriComponentsBuilder.fromPath("/path").queryParam("q", "{value}").encode().build();
        Assert.assertEquals("/path?q=JavaClass%241.class", uri.expand("JavaClass$1.class").toString());
    }

    @Test
    public void toUriEncoded() throws URISyntaxException {
        UriComponents uriComponents = UriComponentsBuilder.UriComponentsBuilder.fromUriString("http://example.com/hotel list/Z\u00fcrich").build();
        Assert.assertEquals(new URI("http://example.com/hotel%20list/Z%C3%BCrich"), uriComponents.encode().toUri());
    }

    @Test
    public void toUriNotEncoded() throws URISyntaxException {
        UriComponents uriComponents = UriComponentsBuilder.UriComponentsBuilder.fromUriString("http://example.com/hotel list/Z\u00fcrich").build();
        Assert.assertEquals(new URI("http://example.com/hotel%20list/Z\u00fcrich"), uriComponents.toUri());
    }

    @Test
    public void toUriAlreadyEncoded() throws URISyntaxException {
        UriComponents uriComponents = UriComponentsBuilder.UriComponentsBuilder.fromUriString("http://example.com/hotel%20list/Z%C3%BCrich").build(true);
        UriComponents encoded = uriComponents.encode();
        Assert.assertEquals(new URI("http://example.com/hotel%20list/Z%C3%BCrich"), encoded.toUri());
    }

    @Test
    public void toUriWithIpv6HostAlreadyEncoded() throws URISyntaxException {
        UriComponents uriComponents = UriComponentsBuilder.UriComponentsBuilder.fromUriString("http://[1abc:2abc:3abc::5ABC:6abc]:8080/hotel%20list/Z%C3%BCrich").build(true);
        UriComponents encoded = uriComponents.encode();
        Assert.assertEquals(new URI("http://[1abc:2abc:3abc::5ABC:6abc]:8080/hotel%20list/Z%C3%BCrich"), encoded.toUri());
    }

    @Test
    public void expand() {
        UriComponents uriComponents = UriComponentsBuilder.UriComponentsBuilder.fromUriString("http://example.com").path("/{foo} {bar}").build();
        uriComponents = uriComponents.expand("1 2", "3 4");
        Assert.assertEquals("/1 2 3 4", uriComponents.getPath());
        Assert.assertEquals("http://example.com/1 2 3 4", uriComponents.toUriString());
    }

    // SPR-13311
    @Test
    public void expandWithRegexVar() {
        String template = "/myurl/{name:[a-z]{1,5}}/show";
        UriComponents uriComponents = UriComponentsBuilder.UriComponentsBuilder.fromUriString(template).build();
        uriComponents = uriComponents.expand(Collections.singletonMap("name", "test"));
        Assert.assertEquals("/myurl/test/show", uriComponents.getPath());
    }

    // SPR-17630
    @Test
    public void uirTemplateExpandWithMismatchedCurlyBraces() {
        Assert.assertEquals("/myurl/?q=%7B%7B%7B%7B", UriComponentsBuilder.UriComponentsBuilder.fromUriString("/myurl/?q={{{{").encode().build().toUriString());
    }

    // SPR-12123
    @Test
    public void port() {
        UriComponents uri1 = fromUriString("http://example.com:8080/bar").build();
        UriComponents uri2 = fromUriString("http://example.com/bar").port(8080).build();
        UriComponents uri3 = fromUriString("http://example.com/bar").port("{port}").build().expand(8080);
        UriComponents uri4 = fromUriString("http://example.com/bar").port("808{digit}").build().expand(0);
        Assert.assertEquals(8080, uri1.getPort());
        Assert.assertEquals("http://example.com:8080/bar", uri1.toUriString());
        Assert.assertEquals(8080, uri2.getPort());
        Assert.assertEquals("http://example.com:8080/bar", uri2.toUriString());
        Assert.assertEquals(8080, uri3.getPort());
        Assert.assertEquals("http://example.com:8080/bar", uri3.toUriString());
        Assert.assertEquals(8080, uri4.getPort());
        Assert.assertEquals("http://example.com:8080/bar", uri4.toUriString());
    }

    @Test(expected = IllegalStateException.class)
    public void expandEncoded() {
        UriComponentsBuilder.UriComponentsBuilder.fromPath("/{foo}").build().encode().expand("bar");
    }

    @Test(expected = IllegalArgumentException.class)
    public void invalidCharacters() {
        UriComponentsBuilder.UriComponentsBuilder.fromPath("/{foo}").build(true);
    }

    @Test(expected = IllegalArgumentException.class)
    public void invalidEncodedSequence() {
        UriComponentsBuilder.UriComponentsBuilder.fromPath("/fo%2o").build(true);
    }

    @Test
    public void normalize() {
        UriComponents uriComponents = UriComponentsBuilder.UriComponentsBuilder.fromUriString("http://example.com/foo/../bar").build();
        Assert.assertEquals("http://example.com/bar", uriComponents.normalize().toString());
    }

    @Test
    public void serializable() throws Exception {
        UriComponents uriComponents = UriComponentsBuilder.UriComponentsBuilder.fromUriString("http://example.com").path("/{foo}").query("bar={baz}").build();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(uriComponents);
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bos.toByteArray()));
        UriComponents readObject = ((UriComponents) (ois.readObject()));
        Assert.assertThat(uriComponents.toString(), equalTo(readObject.toString()));
    }

    @Test
    public void copyToUriComponentsBuilder() {
        UriComponents source = UriComponentsBuilder.UriComponentsBuilder.fromPath("/foo/bar").pathSegment("ba/z").build();
        UriComponentsBuilder.UriComponentsBuilder targetBuilder = UriComponentsBuilder.UriComponentsBuilder.newInstance();
        source.copyToUriComponentsBuilder(targetBuilder);
        UriComponents result = targetBuilder.build().encode();
        Assert.assertEquals("/foo/bar/ba%2Fz", result.getPath());
        Assert.assertEquals(Arrays.asList("foo", "bar", "ba%2Fz"), result.getPathSegments());
    }

    @Test
    public void equalsHierarchicalUriComponents() {
        String url = "http://example.com";
        UriComponents uric1 = UriComponentsBuilder.UriComponentsBuilder.fromUriString(url).path("/{foo}").query("bar={baz}").build();
        UriComponents uric2 = UriComponentsBuilder.UriComponentsBuilder.fromUriString(url).path("/{foo}").query("bar={baz}").build();
        UriComponents uric3 = UriComponentsBuilder.UriComponentsBuilder.fromUriString(url).path("/{foo}").query("bin={baz}").build();
        Assert.assertThat(uric1, instanceOf(HierarchicalUriComponents.class));
        Assert.assertThat(uric1, equalTo(uric1));
        Assert.assertThat(uric1, equalTo(uric2));
        Assert.assertThat(uric1, not(equalTo(uric3)));
    }

    @Test
    public void equalsOpaqueUriComponents() {
        String baseUrl = "http:example.com";
        UriComponents uric1 = UriComponentsBuilder.UriComponentsBuilder.fromUriString((baseUrl + "/foo/bar")).build();
        UriComponents uric2 = UriComponentsBuilder.UriComponentsBuilder.fromUriString((baseUrl + "/foo/bar")).build();
        UriComponents uric3 = UriComponentsBuilder.UriComponentsBuilder.fromUriString((baseUrl + "/foo/bin")).build();
        Assert.assertThat(uric1, instanceOf(OpaqueUriComponents.class));
        Assert.assertThat(uric1, equalTo(uric1));
        Assert.assertThat(uric1, equalTo(uric2));
        Assert.assertThat(uric1, not(equalTo(uric3)));
    }
}

