/**
 * Copyright 2012-2019 the original author or authors.
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
package org.springframework.boot.devtools.autoconfigure;


import org.junit.Test;
import org.springframework.objenesis.ObjenesisStd;


/**
 * Tests for {@link HateoasObjenesisCacheDisabler}.
 *
 * @author Andy Wilkinson
 */
public class HateoasObjenesisCacheDisablerTests {
    private ObjenesisStd objenesis;

    @Test
    public void cacheIsEnabledByDefault() {
        assertThat(this.objenesis.getInstantiatorOf(HateoasObjenesisCacheDisablerTests.TestObject.class)).isSameAs(this.objenesis.getInstantiatorOf(HateoasObjenesisCacheDisablerTests.TestObject.class));
    }

    @Test
    public void cacheIsDisabled() {
        new HateoasObjenesisCacheDisabler().afterPropertiesSet();
        assertThat(this.objenesis.getInstantiatorOf(HateoasObjenesisCacheDisablerTests.TestObject.class)).isNotSameAs(this.objenesis.getInstantiatorOf(HateoasObjenesisCacheDisablerTests.TestObject.class));
    }

    private static class TestObject {}
}

