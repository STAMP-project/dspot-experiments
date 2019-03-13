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
package org.jdbi.v3.freemarker;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.Something;
import org.jdbi.v3.core.rule.H2DatabaseRule;
import org.jdbi.v3.sqlobject.customizer.BindList;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.junit.Rule;
import org.junit.Test;


public class BindListTest {
    private Handle handle;

    private List<Something> expectedSomethings;

    @Rule
    public H2DatabaseRule dbRule = new H2DatabaseRule().withSomething();

    // 
    @Test
    public void testSomethingWithExplicitAttributeName() {
        final BindListTest.SomethingWithExplicitAttributeName s = handle.attach(BindListTest.SomethingWithExplicitAttributeName.class);
        final List<Something> out = s.get(1, 2);
        assertThat(out).hasSameElementsAs(expectedSomethings);
    }

    @UseFreemarkerEngine
    public interface SomethingWithExplicitAttributeName {
        @SqlQuery("select id, name from something where id in (${ids})")
        List<Something> get(@BindList("ids")
        int... blarg);
    }

    // 
    @Test
    public void testSomethingByVarargsHandleDefaultWithVarargs() {
        final BindListTest.SomethingByVarargsHandleDefault s = handle.attach(BindListTest.SomethingByVarargsHandleDefault.class);
        final List<Something> out = s.get(1, 2);
        assertThat(out).hasSameElementsAs(expectedSomethings);
    }

    @UseFreemarkerEngine
    public interface SomethingByVarargsHandleDefault {
        @SqlQuery("select id, name from something where id in (${ids})")
        List<Something> get(@BindList
        int... ids);
    }

    // 
    @Test
    public void testSomethingByArrayHandleVoidWithArray() {
        final BindListTest.SomethingByArrayHandleVoid s = handle.attach(BindListTest.SomethingByArrayHandleVoid.class);
        final List<Something> out = s.get(new int[]{ 1, 2 });
        assertThat(out).hasSameElementsAs(expectedSomethings);
    }

    @Test
    public void testSomethingByArrayHandleVoidWithEmptyArray() {
        final BindListTest.SomethingByArrayHandleVoid s = handle.attach(BindListTest.SomethingByArrayHandleVoid.class);
        final List<Something> out = s.get(new int[]{  });
        assertThat(out).isEmpty();
    }

    @Test
    public void testSomethingByArrayHandleVoidWithNull() {
        final BindListTest.SomethingByArrayHandleVoid s = handle.attach(BindListTest.SomethingByArrayHandleVoid.class);
        final List<Something> out = s.get(null);
        assertThat(out).isEmpty();
    }

    @UseFreemarkerEngine
    public interface SomethingByArrayHandleVoid {
        @SqlQuery("select id, name from something where id in (${ids})")
        List<Something> get(@BindList(onEmpty = VOID)
        int[] ids);
    }

    // 
    @Test
    public void testSomethingByArrayHandleThrowWithNull() {
        final BindListTest.SomethingByArrayHandleThrow s = handle.attach(BindListTest.SomethingByArrayHandleThrow.class);
        assertThatThrownBy(() -> s.get(null)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void testSomethingByArrayHandleThrowWithEmptyArray() {
        final BindListTest.SomethingByArrayHandleThrow s = handle.attach(BindListTest.SomethingByArrayHandleThrow.class);
        assertThatThrownBy(() -> s.get(new int[]{  })).isInstanceOf(IllegalArgumentException.class);
    }

    @UseFreemarkerEngine
    private interface SomethingByArrayHandleThrow {
        @SqlQuery("select id, name from something where id in (<ids>)")
        List<Something> get(@BindList(onEmpty = THROW)
        int[] ids);
    }

    // 
    @Test
    public void testSomethingByIterableHandleDefaultWithIterable() {
        final BindListTest.SomethingByIterableHandleDefault s = handle.attach(BindListTest.SomethingByIterableHandleDefault.class);
        final List<Something> out = s.get(new Iterable<Integer>() {
            @Override
            public Iterator<Integer> iterator() {
                return Arrays.asList(1, 2).iterator();
            }
        });
        assertThat(out).hasSameElementsAs(expectedSomethings);
    }

    @Test
    public void testSomethingByIterableHandleDefaultWithEmptyIterable() {
        final BindListTest.SomethingByIterableHandleDefault s = handle.attach(BindListTest.SomethingByIterableHandleDefault.class);
        final List<Something> out = s.get(new ArrayList<>());
        assertThat(out).isEmpty();
    }

    @UseFreemarkerEngine
    public interface SomethingByIterableHandleDefault {
        @SqlQuery("select id, name from something where id in (${ids})")
        List<Something> get(@BindList(onEmpty = VOID)
        Iterable<Integer> ids);
    }

    // 
    @Test
    public void testSomethingByIterableHandleThrowWithEmptyIterable() {
        final BindListTest.SomethingByIterableHandleThrow s = handle.attach(BindListTest.SomethingByIterableHandleThrow.class);
        assertThatThrownBy(() -> s.get(new ArrayList<>())).isInstanceOf(IllegalArgumentException.class);
    }

    @UseFreemarkerEngine
    private interface SomethingByIterableHandleThrow {
        @SqlQuery("select id, name from something where id in (${ids])")
        List<Something> get(@BindList(onEmpty = THROW)
        Iterable<Integer> ids);
    }

    // 
    @Test
    public void testSomethingByIteratorHandleDefault() {
        final BindListTest.SomethingByIteratorHandleDefault s = handle.attach(BindListTest.SomethingByIteratorHandleDefault.class);
        assertThatThrownBy(() -> s.get(Arrays.asList(1, 2).iterator())).isInstanceOf(IllegalArgumentException.class);
    }

    @UseFreemarkerEngine
    private interface SomethingByIteratorHandleDefault {
        @SqlQuery("select id, name from something where id in (${ids])")
        List<Something> get(@BindList
        Iterator<Integer> ids);
    }
}

