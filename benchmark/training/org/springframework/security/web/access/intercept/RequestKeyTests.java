/**
 * Copyright 2002-2016 the original author or authors.
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
package org.springframework.security.web.access.intercept;


import org.junit.Test;


/**
 *
 *
 * @author Luke Taylor
 */
public class RequestKeyTests {
    @Test
    public void equalsWorksWithNullHttpMethod() {
        RequestKey key1 = new RequestKey("/someurl");
        RequestKey key2 = new RequestKey("/someurl");
        assertThat(key2).isEqualTo(key1);
        key1 = new RequestKey("/someurl", "GET");
        assertThat(key1.equals(key2)).isFalse();
        assertThat(key2.equals(key1)).isFalse();
    }

    @Test
    public void keysWithSameUrlAndHttpMethodAreEqual() {
        RequestKey key1 = new RequestKey("/someurl", "GET");
        RequestKey key2 = new RequestKey("/someurl", "GET");
        assertThat(key2).isEqualTo(key1);
    }

    @Test
    public void keysWithSameUrlAndDifferentHttpMethodAreNotEqual() {
        RequestKey key1 = new RequestKey("/someurl", "GET");
        RequestKey key2 = new RequestKey("/someurl", "POST");
        assertThat(key1.equals(key2)).isFalse();
        assertThat(key2.equals(key1)).isFalse();
    }

    @Test
    public void keysWithDifferentUrlsAreNotEquals() {
        RequestKey key1 = new RequestKey("/someurl", "GET");
        RequestKey key2 = new RequestKey("/anotherurl", "GET");
        assertThat(key1.equals(key2)).isFalse();
        assertThat(key2.equals(key1)).isFalse();
    }
}

