/**
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.helidon.security.abac.policy.el;


import AbacSupport.BasicAttributes;
import io.helidon.common.CollectionsHelper;
import io.helidon.security.util.AbacSupport;
import java.util.Collection;
import java.util.Map;
import javax.el.ELContext;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


/**
 * Unit test for {@link AttributeResolver}.
 */
class AttributeResolverTest {
    @Test
    public void testResolveAttribute() {
        AttributeResolver ar = new AttributeResolver();
        ELContext context = Mockito.mock(ELContext.class);
        Object name = ar.getValue(context, new AttributeResolverTest.MyResource(CollectionsHelper.mapOf("name", "jarda")), "name");
        MatcherAssert.assertThat(name, CoreMatchers.notNullValue());
        MatcherAssert.assertThat(name, CoreMatchers.is("jarda"));
    }

    @Test
    public void testNoInterface() {
        AttributeResolver ar = new AttributeResolver();
        ELContext context = Mockito.mock(ELContext.class);
        Object name = ar.getValue(context, "just a string", "name");
        MatcherAssert.assertThat(name, CoreMatchers.nullValue());
    }

    @Test
    public void testMissingAttribute() {
        AttributeResolver ar = new AttributeResolver();
        ELContext context = Mockito.mock(ELContext.class);
        Object name = ar.getValue(context, new AttributeResolverTest.MyResource(CollectionsHelper.mapOf("nickname", "jarda")), "name");
        MatcherAssert.assertThat(name, CoreMatchers.nullValue());
    }

    private static class MyResource implements AbacSupport {
        private BasicAttributes attribs = BasicAttributes.create();

        private MyResource(Map<String, Object> attribs) {
            attribs.forEach(this.attribs::put);
        }

        @Override
        public Object abacAttributeRaw(String key) {
            return attribs.abacAttributeRaw(key);
        }

        @Override
        public Collection<String> abacAttributeNames() {
            return attribs.abacAttributeNames();
        }
    }
}

