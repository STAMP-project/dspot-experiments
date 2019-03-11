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


import java.util.List;
import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.rule.H2DatabaseRule;
import org.junit.Rule;
import org.junit.Test;

import static java.util.Arrays.asList;


public class TestDefineList {
    @Rule
    public H2DatabaseRule dbRule = new H2DatabaseRule();

    private Handle handle;

    private List<TestDefineList.Thing> list;

    @Test
    public void testDefineListSelect() {
        list = handle.createQuery("select <columns> from thing order by id").defineList("columns", asList("id", "foo", "bar")).mapTo(TestDefineList.Thing.class).list();
        assertThat(list).extracting(TestDefineList.Thing::getId, TestDefineList.Thing::getFoo, TestDefineList.Thing::getBar, TestDefineList.Thing::getBaz).containsExactly(tuple(1, "foo1", "bar1", null), tuple(2, "foo2", "bar2", null));
        list = handle.createQuery("select <columns> from thing order by id").defineList("columns", asList("id", "baz")).mapTo(TestDefineList.Thing.class).list();
        assertThat(list).extracting(TestDefineList.Thing::getId, TestDefineList.Thing::getFoo, TestDefineList.Thing::getBar, TestDefineList.Thing::getBaz).containsExactly(tuple(1, null, null, "baz1"), tuple(2, null, null, "baz2"));
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

