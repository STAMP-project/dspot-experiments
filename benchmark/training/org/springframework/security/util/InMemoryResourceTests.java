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
package org.springframework.security.util;


import org.junit.Test;


/**
 *
 *
 * @author Luke Taylor
 */
public class InMemoryResourceTests {
    @Test
    public void resourceContainsExpectedData() throws Exception {
        InMemoryResource resource = new InMemoryResource("blah");
        assertThat(resource.getDescription()).isNull();
        assertThat(resource.hashCode()).isEqualTo(1);
        assertThat(resource.getInputStream()).isNotNull();
    }

    @Test
    public void resourceIsEqualToOneWithSameContent() throws Exception {
        assertThat(new InMemoryResource("xxx")).isEqualTo(new InMemoryResource("xxx"));
        assertThat(new InMemoryResource("xxx").equals(new InMemoryResource("xxxx"))).isFalse();
        assertThat(new InMemoryResource("xxx").equals(new Object())).isFalse();
    }
}

