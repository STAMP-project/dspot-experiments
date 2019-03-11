/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jdbi.v3.json;


import org.jdbi.v3.core.config.ConfigRegistry;
import org.jdbi.v3.core.qualifier.QualifiedType;
import org.jdbi.v3.core.rule.H2DatabaseRule;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;


public class JsonPluginTest {
    @Rule
    public H2DatabaseRule db = new H2DatabaseRule().withPlugin(new JsonPlugin());

    @Rule
    public MockitoRule mockito = MockitoJUnit.rule();

    @Mock
    public JsonMapper jsonMapper;

    @Test
    public void factoryChainWorks() {
        Object instance = new JsonPluginTest.Foo();
        String json = "foo";
        Mockito.when(jsonMapper.toJson(ArgumentMatchers.eq(JsonPluginTest.Foo.class), ArgumentMatchers.eq(instance), ArgumentMatchers.any(ConfigRegistry.class))).thenReturn(json);
        Mockito.when(jsonMapper.fromJson(ArgumentMatchers.eq(JsonPluginTest.Foo.class), ArgumentMatchers.eq(json), ArgumentMatchers.any(ConfigRegistry.class))).thenReturn(instance);
        Object result = db.getJdbi().withHandle(( h) -> {
            h.createUpdate("insert into foo(bar) values(:foo)").bindByType("foo", instance, QualifiedType.of(.class).with(.class)).execute();
            assertThat(h.createQuery("select bar from foo").mapTo(.class).findOnly()).isEqualTo(json);
            return h.createQuery("select bar from foo").mapTo(QualifiedType.of(.class).with(.class)).findOnly();
        });
        assertThat(result).isSameAs(instance);
        Mockito.verify(jsonMapper).fromJson(ArgumentMatchers.eq(JsonPluginTest.Foo.class), ArgumentMatchers.eq(json), ArgumentMatchers.any(ConfigRegistry.class));
        Mockito.verify(jsonMapper).toJson(ArgumentMatchers.eq(JsonPluginTest.Foo.class), ArgumentMatchers.eq(instance), ArgumentMatchers.any(ConfigRegistry.class));
    }

    public static class Foo {
        @Override
        public String toString() {
            return "I am Foot.";
        }
    }
}

