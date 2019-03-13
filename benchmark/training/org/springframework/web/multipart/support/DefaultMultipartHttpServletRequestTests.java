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
package org.springframework.web.multipart.support;


import java.util.LinkedHashMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.mock.web.test.MockHttpServletRequest;
import org.springframework.util.MultiValueMap;


/**
 * Unit tests for {@link DefaultMultipartHttpServletRequest}.
 *
 * @author Rossen Stoyanchev
 */
public class DefaultMultipartHttpServletRequestTests {
    private final MockHttpServletRequest servletRequest = new MockHttpServletRequest();

    private final Map<String, String[]> multipartParams = new LinkedHashMap<>();

    private final MultiValueMap<String, String> queryParams = new org.springframework.util.LinkedMultiValueMap();

    // SPR-16590
    @Test
    public void parameterValues() {
        this.multipartParams.put("key", new String[]{ "p" });
        this.queryParams.add("key", "q");
        String[] values = createMultipartRequest().getParameterValues("key");
        Assert.assertArrayEquals(new String[]{ "p", "q" }, values);
    }

    // SPR-16590
    @Test
    public void parameterMap() {
        this.multipartParams.put("key1", new String[]{ "p1" });
        this.multipartParams.put("key2", new String[]{ "p2" });
        this.queryParams.add("key1", "q1");
        this.queryParams.add("key3", "q3");
        Map<String, String[]> map = createMultipartRequest().getParameterMap();
        Assert.assertEquals(3, map.size());
        Assert.assertArrayEquals(new String[]{ "p1", "q1" }, map.get("key1"));
        Assert.assertArrayEquals(new String[]{ "p2" }, map.get("key2"));
        Assert.assertArrayEquals(new String[]{ "q3" }, map.get("key3"));
    }
}

