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
package org.jdbi.v3.core.statement;


import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.rule.H2DatabaseRule;
import org.junit.Rule;
import org.junit.Test;


public class TestBindBeanList {
    @Rule
    public H2DatabaseRule dbRule = new H2DatabaseRule();

    private Handle handle;

    @Test
    public void bindBeanListWithNoValues() {
        assertThatThrownBy(() -> handle.createQuery("select id, foo from thing where (foo, bar) in (<keys>)").bindBeanList("keys", Collections.emptyList(), Arrays.asList("foo", "bar"))).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void bindBeanListWithNoProperties() {
        TestBindBeanList.ThingKey thingKey = new TestBindBeanList.ThingKey("a", "b");
        assertThatThrownBy(() -> handle.createQuery("select id, foo from thing where (foo, bar) in (<keys>)").bindBeanList("keys", Collections.singletonList(thingKey), Collections.emptyList())).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void happyPath() {
        TestBindBeanList.ThingKey thing1Key = new TestBindBeanList.ThingKey("foo1", "bar1");
        TestBindBeanList.ThingKey thing3Key = new TestBindBeanList.ThingKey("foo3", "bar3");
        List<TestBindBeanList.Thing> list = handle.createQuery("select id, foo from thing where (foo, bar) in (<keys>)").bindBeanList("keys", Arrays.asList(thing1Key, thing3Key), Arrays.asList("foo", "bar")).mapTo(TestBindBeanList.Thing.class).list();
        assertThat(list).extracting(TestBindBeanList.Thing::getId, TestBindBeanList.Thing::getFoo, TestBindBeanList.Thing::getBar, TestBindBeanList.Thing::getBaz).containsExactly(tuple(1, "foo1", null, null), tuple(3, "foo3", null, null));
    }

    public static class ThingKey {
        public String foo;

        public String bar;

        public ThingKey(String foo, String bar) {
            this.foo = foo;
            this.bar = bar;
        }

        public String getFoo() {
            return foo;
        }

        public String getBar() {
            return bar;
        }
    }

    public static class Thing {
        public int id;

        public String foo;

        public String bar;

        public String baz;

        public int getId() {
            return id;
        }

        public String getFoo() {
            return foo;
        }

        public String getBar() {
            return bar;
        }

        public String getBaz() {
            return baz;
        }
    }
}

