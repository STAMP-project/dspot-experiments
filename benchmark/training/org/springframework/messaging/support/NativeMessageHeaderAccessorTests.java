/**
 * Copyright 2002-2018 the original author or authors.
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
package org.springframework.messaging.support;


import NativeMessageHeaderAccessor.NATIVE_HEADERS;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.messaging.Message;
import org.springframework.util.MultiValueMap;


/**
 * Test fixture for {@link NativeMessageHeaderAccessor}.
 *
 * @author Rossen Stoyanchev
 */
public class NativeMessageHeaderAccessorTests {
    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    @Test
    public void createFromNativeHeaderMap() {
        MultiValueMap<String, String> inputNativeHeaders = new org.springframework.util.LinkedMultiValueMap();
        inputNativeHeaders.add("foo", "bar");
        inputNativeHeaders.add("bar", "baz");
        NativeMessageHeaderAccessor headerAccessor = new NativeMessageHeaderAccessor(inputNativeHeaders);
        Map<String, Object> actual = headerAccessor.toMap();
        Assert.assertEquals(actual.toString(), 1, actual.size());
        Assert.assertNotNull(actual.get(NATIVE_HEADERS));
        Assert.assertEquals(inputNativeHeaders, actual.get(NATIVE_HEADERS));
        Assert.assertNotSame(inputNativeHeaders, actual.get(NATIVE_HEADERS));
    }

    @Test
    public void createFromMessage() {
        MultiValueMap<String, String> inputNativeHeaders = new org.springframework.util.LinkedMultiValueMap();
        inputNativeHeaders.add("foo", "bar");
        inputNativeHeaders.add("bar", "baz");
        Map<String, Object> inputHeaders = new HashMap<>();
        inputHeaders.put("a", "b");
        inputHeaders.put(NATIVE_HEADERS, inputNativeHeaders);
        GenericMessage<String> message = new GenericMessage("p", inputHeaders);
        NativeMessageHeaderAccessor headerAccessor = new NativeMessageHeaderAccessor(message);
        Map<String, Object> actual = headerAccessor.toMap();
        Assert.assertEquals(2, actual.size());
        Assert.assertEquals("b", actual.get("a"));
        Assert.assertNotNull(actual.get(NATIVE_HEADERS));
        Assert.assertEquals(inputNativeHeaders, actual.get(NATIVE_HEADERS));
        Assert.assertNotSame(inputNativeHeaders, actual.get(NATIVE_HEADERS));
    }

    @Test
    public void createFromMessageNull() {
        NativeMessageHeaderAccessor headerAccessor = new NativeMessageHeaderAccessor(((Message<?>) (null)));
        Map<String, Object> actual = headerAccessor.toMap();
        Assert.assertEquals(0, actual.size());
        Map<String, List<String>> actualNativeHeaders = headerAccessor.toNativeHeaderMap();
        Assert.assertEquals(Collections.emptyMap(), actualNativeHeaders);
    }

    @Test
    public void createFromMessageAndModify() {
        MultiValueMap<String, String> inputNativeHeaders = new org.springframework.util.LinkedMultiValueMap();
        inputNativeHeaders.add("foo", "bar");
        inputNativeHeaders.add("bar", "baz");
        Map<String, Object> nativeHeaders = new HashMap<>();
        nativeHeaders.put("a", "b");
        nativeHeaders.put(NATIVE_HEADERS, inputNativeHeaders);
        GenericMessage<String> message = new GenericMessage("p", nativeHeaders);
        NativeMessageHeaderAccessor headerAccessor = new NativeMessageHeaderAccessor(message);
        headerAccessor.setHeader("a", "B");
        headerAccessor.setNativeHeader("foo", "BAR");
        Map<String, Object> actual = headerAccessor.toMap();
        Assert.assertEquals(2, actual.size());
        Assert.assertEquals("B", actual.get("a"));
        @SuppressWarnings("unchecked")
        Map<String, List<String>> actualNativeHeaders = ((Map<String, List<String>>) (actual.get(NATIVE_HEADERS)));
        Assert.assertNotNull(actualNativeHeaders);
        Assert.assertEquals(Arrays.asList("BAR"), actualNativeHeaders.get("foo"));
        Assert.assertEquals(Arrays.asList("baz"), actualNativeHeaders.get("bar"));
    }

    @Test
    public void setNativeHeader() {
        MultiValueMap<String, String> nativeHeaders = new org.springframework.util.LinkedMultiValueMap();
        nativeHeaders.add("foo", "bar");
        NativeMessageHeaderAccessor headers = new NativeMessageHeaderAccessor(nativeHeaders);
        headers.setNativeHeader("foo", "baz");
        Assert.assertEquals(Arrays.asList("baz"), headers.getNativeHeader("foo"));
    }

    @Test
    public void setNativeHeaderNullValue() {
        MultiValueMap<String, String> nativeHeaders = new org.springframework.util.LinkedMultiValueMap();
        nativeHeaders.add("foo", "bar");
        NativeMessageHeaderAccessor headers = new NativeMessageHeaderAccessor(nativeHeaders);
        headers.setNativeHeader("foo", null);
        Assert.assertNull(headers.getNativeHeader("foo"));
    }

    @Test
    public void setNativeHeaderLazyInit() {
        NativeMessageHeaderAccessor headerAccessor = new NativeMessageHeaderAccessor();
        headerAccessor.setNativeHeader("foo", "baz");
        Assert.assertEquals(Arrays.asList("baz"), headerAccessor.getNativeHeader("foo"));
    }

    @Test
    public void setNativeHeaderLazyInitNullValue() {
        NativeMessageHeaderAccessor headerAccessor = new NativeMessageHeaderAccessor();
        headerAccessor.setNativeHeader("foo", null);
        Assert.assertNull(headerAccessor.getNativeHeader("foo"));
        Assert.assertNull(headerAccessor.getMessageHeaders().get(NATIVE_HEADERS));
    }

    @Test
    public void setNativeHeaderImmutable() {
        NativeMessageHeaderAccessor headerAccessor = new NativeMessageHeaderAccessor();
        headerAccessor.setNativeHeader("foo", "bar");
        headerAccessor.setImmutable();
        this.thrown.expect(IllegalStateException.class);
        this.thrown.expectMessage("Already immutable");
        headerAccessor.setNativeHeader("foo", "baz");
    }

    @Test
    public void addNativeHeader() {
        MultiValueMap<String, String> nativeHeaders = new org.springframework.util.LinkedMultiValueMap();
        nativeHeaders.add("foo", "bar");
        NativeMessageHeaderAccessor headers = new NativeMessageHeaderAccessor(nativeHeaders);
        headers.addNativeHeader("foo", "baz");
        Assert.assertEquals(Arrays.asList("bar", "baz"), headers.getNativeHeader("foo"));
    }

    @Test
    public void addNativeHeaderNullValue() {
        MultiValueMap<String, String> nativeHeaders = new org.springframework.util.LinkedMultiValueMap();
        nativeHeaders.add("foo", "bar");
        NativeMessageHeaderAccessor headers = new NativeMessageHeaderAccessor(nativeHeaders);
        headers.addNativeHeader("foo", null);
        Assert.assertEquals(Arrays.asList("bar"), headers.getNativeHeader("foo"));
    }

    @Test
    public void addNativeHeaderLazyInit() {
        NativeMessageHeaderAccessor headerAccessor = new NativeMessageHeaderAccessor();
        headerAccessor.addNativeHeader("foo", "bar");
        Assert.assertEquals(Arrays.asList("bar"), headerAccessor.getNativeHeader("foo"));
    }

    @Test
    public void addNativeHeaderLazyInitNullValue() {
        NativeMessageHeaderAccessor headerAccessor = new NativeMessageHeaderAccessor();
        headerAccessor.addNativeHeader("foo", null);
        Assert.assertNull(headerAccessor.getNativeHeader("foo"));
        Assert.assertNull(headerAccessor.getMessageHeaders().get(NATIVE_HEADERS));
    }

    @Test
    public void addNativeHeaderImmutable() {
        NativeMessageHeaderAccessor headerAccessor = new NativeMessageHeaderAccessor();
        headerAccessor.addNativeHeader("foo", "bar");
        headerAccessor.setImmutable();
        this.thrown.expect(IllegalStateException.class);
        this.thrown.expectMessage("Already immutable");
        headerAccessor.addNativeHeader("foo", "baz");
    }

    @Test
    public void setImmutableIdempotent() {
        NativeMessageHeaderAccessor headerAccessor = new NativeMessageHeaderAccessor();
        headerAccessor.addNativeHeader("foo", "bar");
        headerAccessor.setImmutable();
        headerAccessor.setImmutable();
    }
}

