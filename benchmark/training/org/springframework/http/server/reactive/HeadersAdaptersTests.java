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
package org.springframework.http.server.reactive;


import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.springframework.util.MultiValueMap;


/**
 * Unit tests for {@code HeadersAdapters} {@code MultiValueMap} implementations.
 *
 * @author Brian Clozel
 */
@RunWith(Parameterized.class)
public class HeadersAdaptersTests {
    @Parameterized.Parameter(0)
    public MultiValueMap<String, String> headers;

    @Test
    public void getWithUnknownHeaderShouldReturnNull() {
        Assert.assertNull(this.headers.get("Unknown"));
    }

    @Test
    public void getFirstWithUnknownHeaderShouldReturnNull() {
        Assert.assertNull(this.headers.getFirst("Unknown"));
    }

    @Test
    public void sizeWithMultipleValuesForHeaderShouldCountHeaders() {
        this.headers.add("TestHeader", "first");
        this.headers.add("TestHeader", "second");
        Assert.assertEquals(1, this.headers.size());
    }

    @Test
    public void keySetShouldNotDuplicateHeaderNames() {
        this.headers.add("TestHeader", "first");
        this.headers.add("OtherHeader", "test");
        this.headers.add("TestHeader", "second");
        Assert.assertEquals(2, this.headers.keySet().size());
    }

    @Test
    public void containsKeyShouldBeCaseInsensitive() {
        this.headers.add("TestHeader", "first");
        Assert.assertTrue(this.headers.containsKey("testheader"));
    }

    @Test
    public void addShouldKeepOrdering() {
        this.headers.add("TestHeader", "first");
        this.headers.add("TestHeader", "second");
        Assert.assertEquals("first", this.headers.getFirst("TestHeader"));
        Assert.assertEquals("first", this.headers.get("TestHeader").get(0));
    }

    @Test
    public void putShouldOverrideExisting() {
        this.headers.add("TestHeader", "first");
        this.headers.put("TestHeader", Arrays.asList("override"));
        Assert.assertEquals("override", this.headers.getFirst("TestHeader"));
        Assert.assertEquals(1, this.headers.get("TestHeader").size());
    }
}

