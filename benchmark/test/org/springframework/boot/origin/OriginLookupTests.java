/**
 * Copyright 2012-2017 the original author or authors.
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
package org.springframework.boot.origin;


import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;


/**
 * Tests for {@link OriginLookup}.
 *
 * @author Phillip Webb
 */
public class OriginLookupTests {
    @Test
    public void getOriginWhenSourceIsNullShouldReturnNull() {
        assertThat(OriginLookup.getOrigin(null, "foo")).isNull();
    }

    @Test
    public void getOriginWhenSourceIsNotLookupShouldReturnLookupOrigin() {
        Object source = new Object();
        assertThat(OriginLookup.getOrigin(source, "foo")).isNull();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void getOriginWhenSourceIsLookupShouldReturnLookupOrigin() {
        OriginLookup<String> source = Mockito.mock(OriginLookup.class);
        Origin origin = MockOrigin.of("bar");
        BDDMockito.given(source.getOrigin("foo")).willReturn(origin);
        assertThat(OriginLookup.getOrigin(source, "foo")).isEqualTo(origin);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void getOriginWhenSourceLookupThrowsAndErrorShouldReturnNull() {
        OriginLookup<String> source = Mockito.mock(OriginLookup.class);
        BDDMockito.willThrow(RuntimeException.class).given(source).getOrigin("foo");
        assertThat(OriginLookup.getOrigin(source, "foo")).isNull();
    }
}

