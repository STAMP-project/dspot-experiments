/**
 * SonarQube
 * Copyright (C) 2009-2019 SonarSource SA
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonar.core.util.stream;


import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


public class MoreCollectorsTest {
    private static final List<String> HUGE_LIST = IntStream.range(0, 2000).mapToObj(String::valueOf).collect(Collectors.toList());

    private static final Set<String> HUGE_SET = new HashSet<>(MoreCollectorsTest.HUGE_LIST);

    private static final MoreCollectorsTest.MyObj MY_OBJ_1_A = new MoreCollectorsTest.MyObj(1, "A");

    private static final MoreCollectorsTest.MyObj MY_OBJ_1_C = new MoreCollectorsTest.MyObj(1, "C");

    private static final MoreCollectorsTest.MyObj MY_OBJ_2_B = new MoreCollectorsTest.MyObj(2, "B");

    private static final MoreCollectorsTest.MyObj MY_OBJ_3_C = new MoreCollectorsTest.MyObj(3, "C");

    private static final List<MoreCollectorsTest.MyObj> SINGLE_ELEMENT_LIST = Arrays.asList(MoreCollectorsTest.MY_OBJ_1_A);

    private static final List<MoreCollectorsTest.MyObj> LIST_WITH_DUPLICATE_ID = Arrays.asList(MoreCollectorsTest.MY_OBJ_1_A, MoreCollectorsTest.MY_OBJ_2_B, MoreCollectorsTest.MY_OBJ_1_C);

    private static final List<MoreCollectorsTest.MyObj> LIST = Arrays.asList(MoreCollectorsTest.MY_OBJ_1_A, MoreCollectorsTest.MY_OBJ_2_B, MoreCollectorsTest.MY_OBJ_3_C);

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void toList_builds_an_ImmutableList() {
        List<Integer> res = Arrays.asList(1, 2, 3, 4, 5).stream().collect(MoreCollectors.toList());
        assertThat(res).isInstanceOf(ImmutableList.class).containsExactly(1, 2, 3, 4, 5);
    }

    @Test
    public void toList_parallel_stream() {
        assertThat(MoreCollectorsTest.HUGE_LIST.parallelStream().collect(MoreCollectors.toList())).isEqualTo(MoreCollectorsTest.HUGE_LIST);
    }

    @Test
    public void toList_with_size_builds_an_ImmutableList() {
        List<Integer> res = Arrays.asList(1, 2, 3, 4, 5).stream().collect(MoreCollectors.toList(30));
        assertThat(res).isInstanceOf(ImmutableList.class).containsExactly(1, 2, 3, 4, 5);
    }

    @Test
    public void toList_with_size_parallel_stream() {
        assertThat(MoreCollectorsTest.HUGE_LIST.parallelStream().collect(MoreCollectors.toList(MoreCollectorsTest.HUGE_LIST.size()))).isEqualTo(MoreCollectorsTest.HUGE_LIST);
    }

    @Test
    public void toSet_builds_an_ImmutableSet() {
        Set<Integer> res = Arrays.asList(1, 2, 3, 4, 5).stream().collect(MoreCollectors.toSet());
        assertThat(res).isInstanceOf(ImmutableSet.class).containsExactly(1, 2, 3, 4, 5);
    }

    @Test
    public void toSet_parallel_stream() {
        assertThat(MoreCollectorsTest.HUGE_SET.parallelStream().collect(MoreCollectors.toSet())).isEqualTo(MoreCollectorsTest.HUGE_SET);
    }

    @Test
    public void toSet_with_size_builds_an_ImmutableSet() {
        Set<Integer> res = Stream.of(1, 2, 3, 4, 5).collect(MoreCollectors.toSet(30));
        assertThat(res).isInstanceOf(ImmutableSet.class).containsExactly(1, 2, 3, 4, 5);
    }

    @Test
    public void toSet_with_size_parallel_stream() {
        assertThat(MoreCollectorsTest.HUGE_SET.parallelStream().collect(MoreCollectors.toSet(MoreCollectorsTest.HUGE_SET.size()))).isEqualTo(MoreCollectorsTest.HUGE_SET);
    }

    @Test
    public void toEnumSet() {
        Set<MoreCollectorsTest.MyEnum> res = Stream.of(MoreCollectorsTest.MyEnum.ONE, MoreCollectorsTest.MyEnum.ONE, MoreCollectorsTest.MyEnum.TWO).collect(MoreCollectors.toEnumSet(MoreCollectorsTest.MyEnum.class));
        assertThat(res).isInstanceOf(EnumSet.class).containsExactly(MoreCollectorsTest.MyEnum.ONE, MoreCollectorsTest.MyEnum.TWO);
    }

    @Test
    public void toEnumSet_with_empty_stream() {
        Set<MoreCollectorsTest.MyEnum> res = Stream.<MoreCollectorsTest.MyEnum>empty().collect(MoreCollectors.toEnumSet(MoreCollectorsTest.MyEnum.class));
        assertThat(res).isInstanceOf(EnumSet.class).isEmpty();
    }

    @Test
    public void toArrayList_builds_an_ArrayList() {
        List<Integer> res = Arrays.asList(1, 2, 3, 4, 5).stream().collect(MoreCollectors.toArrayList());
        assertThat(res).isInstanceOf(ArrayList.class).containsExactly(1, 2, 3, 4, 5);
    }

    @Test
    public void toArrayList_parallel_stream() {
        assertThat(MoreCollectorsTest.HUGE_LIST.parallelStream().collect(MoreCollectors.toArrayList())).isEqualTo(MoreCollectorsTest.HUGE_LIST);
    }

    @Test
    public void toArrayList_with_size_builds_an_ArrayList() {
        List<Integer> res = Arrays.asList(1, 2, 3, 4, 5).stream().collect(MoreCollectors.toArrayList(30));
        assertThat(res).isInstanceOf(ArrayList.class).containsExactly(1, 2, 3, 4, 5);
    }

    @Test
    public void toArrayList_with_size_parallel_stream() {
        assertThat(MoreCollectorsTest.HUGE_LIST.parallelStream().collect(MoreCollectors.toArrayList(MoreCollectorsTest.HUGE_LIST.size()))).isEqualTo(MoreCollectorsTest.HUGE_LIST);
    }

    @Test
    public void toHashSet_builds_an_HashSet() {
        Set<Integer> res = Arrays.asList(1, 2, 3, 4, 5).stream().collect(MoreCollectors.toHashSet());
        assertThat(res).isInstanceOf(HashSet.class).containsExactly(1, 2, 3, 4, 5);
    }

    @Test
    public void toHashSet_parallel_stream() {
        assertThat(MoreCollectorsTest.HUGE_SET.parallelStream().collect(MoreCollectors.toHashSet())).isEqualTo(MoreCollectorsTest.HUGE_SET);
    }

    @Test
    public void toHashSet_with_size_builds_an_ArrayList() {
        Set<Integer> res = Arrays.asList(1, 2, 3, 4, 5).stream().collect(MoreCollectors.toHashSet(30));
        assertThat(res).isInstanceOf(HashSet.class).containsExactly(1, 2, 3, 4, 5);
    }

    @Test
    public void toHashSet_with_size_parallel_stream() {
        assertThat(MoreCollectorsTest.HUGE_SET.parallelStream().collect(MoreCollectors.toHashSet(MoreCollectorsTest.HUGE_SET.size()))).isEqualTo(MoreCollectorsTest.HUGE_SET);
    }

    @Test
    public void uniqueIndex_empty_stream_returns_empty_map() {
        assertThat(Collections.<MoreCollectorsTest.MyObj>emptyList().stream().collect(MoreCollectors.uniqueIndex(MoreCollectorsTest.MyObj::getId))).isEmpty();
        assertThat(Collections.<MoreCollectorsTest.MyObj>emptyList().stream().collect(MoreCollectors.uniqueIndex(MoreCollectorsTest.MyObj::getId, 6))).isEmpty();
        assertThat(Collections.<MoreCollectorsTest.MyObj>emptyList().stream().collect(MoreCollectors.uniqueIndex(MoreCollectorsTest.MyObj::getId, MoreCollectorsTest.MyObj::getText))).isEmpty();
        assertThat(Collections.<MoreCollectorsTest.MyObj>emptyList().stream().collect(MoreCollectors.uniqueIndex(MoreCollectorsTest.MyObj::getId, MoreCollectorsTest.MyObj::getText, 10))).isEmpty();
    }

    @Test
    public void uniqueIndex_fails_when_there_is_duplicate_keys() {
        Stream<MoreCollectorsTest.MyObj> stream = MoreCollectorsTest.LIST_WITH_DUPLICATE_ID.stream();
        expectedDuplicateKey1IAE();
        stream.collect(MoreCollectors.uniqueIndex(MoreCollectorsTest.MyObj::getId));
    }

    @Test
    public void uniqueIndex_with_expected_size_fails_when_there_is_duplicate_keys() {
        Stream<MoreCollectorsTest.MyObj> stream = MoreCollectorsTest.LIST_WITH_DUPLICATE_ID.stream();
        expectedDuplicateKey1IAE();
        stream.collect(MoreCollectors.uniqueIndex(MoreCollectorsTest.MyObj::getId, 1));
    }

    @Test
    public void uniqueIndex_with_valueFunction_fails_when_there_is_duplicate_keys() {
        Stream<MoreCollectorsTest.MyObj> stream = MoreCollectorsTest.LIST_WITH_DUPLICATE_ID.stream();
        expectedDuplicateKey1IAE();
        stream.collect(MoreCollectors.uniqueIndex(MoreCollectorsTest.MyObj::getId, MoreCollectorsTest.MyObj::getText));
    }

    @Test
    public void uniqueIndex_with_valueFunction_and_expected_size_fails_when_there_is_duplicate_keys() {
        Stream<MoreCollectorsTest.MyObj> stream = MoreCollectorsTest.LIST_WITH_DUPLICATE_ID.stream();
        expectedDuplicateKey1IAE();
        stream.collect(MoreCollectors.uniqueIndex(MoreCollectorsTest.MyObj::getId, MoreCollectorsTest.MyObj::getText, 10));
    }

    @Test
    public void uniqueIndex_fails_if_key_function_is_null() {
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("Key function can't be null");
        MoreCollectors.uniqueIndex(null);
    }

    @Test
    public void uniqueIndex_with_expected_size_fails_if_key_function_is_null() {
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("Key function can't be null");
        MoreCollectors.uniqueIndex(null, 2);
    }

    @Test
    public void uniqueIndex_with_valueFunction_fails_if_key_function_is_null() {
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("Key function can't be null");
        MoreCollectors.uniqueIndex(null, MoreCollectorsTest.MyObj::getText);
    }

    @Test
    public void uniqueIndex_with_valueFunction_and_expected_size_fails_if_key_function_is_null() {
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("Key function can't be null");
        MoreCollectors.uniqueIndex(null, MoreCollectorsTest.MyObj::getText, 9);
    }

    @Test
    public void uniqueIndex_with_valueFunction_fails_if_value_function_is_null() {
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("Value function can't be null");
        MoreCollectors.uniqueIndex(MoreCollectorsTest.MyObj::getId, null);
    }

    @Test
    public void uniqueIndex_with_valueFunction_and_expected_size_fails_if_value_function_is_null() {
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("Value function can't be null");
        MoreCollectors.uniqueIndex(MoreCollectorsTest.MyObj::getId, null, 9);
    }

    @Test
    public void uniqueIndex_fails_if_key_function_returns_null() {
        expectKeyFunctionCantReturnNullNPE();
        MoreCollectorsTest.SINGLE_ELEMENT_LIST.stream().collect(MoreCollectors.uniqueIndex(( s) -> null));
    }

    @Test
    public void uniqueIndex_with_expected_size_fails_if_key_function_returns_null() {
        expectKeyFunctionCantReturnNullNPE();
        MoreCollectorsTest.SINGLE_ELEMENT_LIST.stream().collect(MoreCollectors.uniqueIndex(( s) -> null, 90));
    }

    @Test
    public void uniqueIndex_with_valueFunction_fails_if_key_function_returns_null() {
        expectKeyFunctionCantReturnNullNPE();
        MoreCollectorsTest.SINGLE_ELEMENT_LIST.stream().collect(MoreCollectors.uniqueIndex(( s) -> null, MoreCollectorsTest.MyObj::getText));
    }

    @Test
    public void uniqueIndex_with_valueFunction_and_expected_size_fails_if_key_function_returns_null() {
        expectKeyFunctionCantReturnNullNPE();
        MoreCollectorsTest.SINGLE_ELEMENT_LIST.stream().collect(MoreCollectors.uniqueIndex(( s) -> null, MoreCollectorsTest.MyObj::getText, 9));
    }

    @Test
    public void uniqueIndex_with_valueFunction_fails_if_value_function_returns_null() {
        expectValueFunctionCantReturnNullNPE();
        MoreCollectorsTest.SINGLE_ELEMENT_LIST.stream().collect(MoreCollectors.uniqueIndex(MoreCollectorsTest.MyObj::getId, ( s) -> null));
    }

    @Test
    public void uniqueIndex_with_valueFunction_and_expected_size_fails_if_value_function_returns_null() {
        expectValueFunctionCantReturnNullNPE();
        MoreCollectorsTest.SINGLE_ELEMENT_LIST.stream().collect(MoreCollectors.uniqueIndex(MoreCollectorsTest.MyObj::getId, ( s) -> null, 9));
    }

    @Test
    public void uniqueIndex_returns_map() {
        assertThat(MoreCollectorsTest.LIST.stream().collect(MoreCollectors.uniqueIndex(MoreCollectorsTest.MyObj::getId))).containsOnly(entry(1, MoreCollectorsTest.MY_OBJ_1_A), entry(2, MoreCollectorsTest.MY_OBJ_2_B), entry(3, MoreCollectorsTest.MY_OBJ_3_C));
    }

    @Test
    public void uniqueIndex_with_expected_size_returns_map() {
        assertThat(MoreCollectorsTest.LIST.stream().collect(MoreCollectors.uniqueIndex(MoreCollectorsTest.MyObj::getId, 3))).containsOnly(entry(1, MoreCollectorsTest.MY_OBJ_1_A), entry(2, MoreCollectorsTest.MY_OBJ_2_B), entry(3, MoreCollectorsTest.MY_OBJ_3_C));
    }

    @Test
    public void uniqueIndex_with_valueFunction_returns_map() {
        assertThat(MoreCollectorsTest.LIST.stream().collect(MoreCollectors.uniqueIndex(MoreCollectorsTest.MyObj::getId, MoreCollectorsTest.MyObj::getText))).containsOnly(entry(1, "A"), entry(2, "B"), entry(3, "C"));
    }

    @Test
    public void uniqueIndex_with_valueFunction_and_expected_size_returns_map() {
        assertThat(MoreCollectorsTest.LIST.stream().collect(MoreCollectors.uniqueIndex(MoreCollectorsTest.MyObj::getId, MoreCollectorsTest.MyObj::getText, 9))).containsOnly(entry(1, "A"), entry(2, "B"), entry(3, "C"));
    }

    @Test
    public void uniqueIndex_parallel_stream() {
        Map<String, String> map = MoreCollectorsTest.HUGE_LIST.parallelStream().collect(MoreCollectors.uniqueIndex(Function.identity()));
        assertThat(map.keySet()).isEqualTo(MoreCollectorsTest.HUGE_SET);
        assertThat(map.values()).containsExactlyElementsOf(MoreCollectorsTest.HUGE_SET);
    }

    @Test
    public void uniqueIndex_with_expected_size_parallel_stream() {
        Map<String, String> map = MoreCollectorsTest.HUGE_LIST.parallelStream().collect(MoreCollectors.uniqueIndex(Function.identity(), MoreCollectorsTest.HUGE_LIST.size()));
        assertThat(map.keySet()).isEqualTo(MoreCollectorsTest.HUGE_SET);
        assertThat(map.values()).containsExactlyElementsOf(MoreCollectorsTest.HUGE_SET);
    }

    @Test
    public void uniqueIndex_with_valueFunction_parallel_stream() {
        Map<String, String> map = MoreCollectorsTest.HUGE_LIST.parallelStream().collect(MoreCollectors.uniqueIndex(Function.identity(), Function.identity()));
        assertThat(map.keySet()).isEqualTo(MoreCollectorsTest.HUGE_SET);
        assertThat(map.values()).containsExactlyElementsOf(MoreCollectorsTest.HUGE_SET);
    }

    @Test
    public void uniqueIndex_with_valueFunction_and_expected_size_parallel_stream() {
        Map<String, String> map = MoreCollectorsTest.HUGE_LIST.parallelStream().collect(MoreCollectors.uniqueIndex(Function.identity(), Function.identity(), MoreCollectorsTest.HUGE_LIST.size()));
        assertThat(map.keySet()).isEqualTo(MoreCollectorsTest.HUGE_SET);
        assertThat(map.values()).containsExactlyElementsOf(MoreCollectorsTest.HUGE_SET);
    }

    @Test
    public void index_empty_stream_returns_empty_map() {
        assertThat(Collections.<MoreCollectorsTest.MyObj>emptyList().stream().collect(MoreCollectors.index(MoreCollectorsTest.MyObj::getId)).size()).isEqualTo(0);
        assertThat(Collections.<MoreCollectorsTest.MyObj>emptyList().stream().collect(MoreCollectors.index(MoreCollectorsTest.MyObj::getId, MoreCollectorsTest.MyObj::getText)).size()).isEqualTo(0);
    }

    @Test
    public void index_fails_if_key_function_is_null() {
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("Key function can't be null");
        MoreCollectors.index(null);
    }

    @Test
    public void index_with_valueFunction_fails_if_key_function_is_null() {
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("Key function can't be null");
        MoreCollectors.index(null, MoreCollectorsTest.MyObj::getText);
    }

    @Test
    public void index_with_valueFunction_fails_if_value_function_is_null() {
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("Value function can't be null");
        MoreCollectors.index(MoreCollectorsTest.MyObj::getId, null);
    }

    @Test
    public void index_fails_if_key_function_returns_null() {
        expectKeyFunctionCantReturnNullNPE();
        MoreCollectorsTest.SINGLE_ELEMENT_LIST.stream().collect(MoreCollectors.index(( s) -> null));
    }

    @Test
    public void index_with_valueFunction_fails_if_key_function_returns_null() {
        expectKeyFunctionCantReturnNullNPE();
        MoreCollectorsTest.SINGLE_ELEMENT_LIST.stream().collect(MoreCollectors.index(( s) -> null, MoreCollectorsTest.MyObj::getText));
    }

    @Test
    public void index_with_valueFunction_fails_if_value_function_returns_null() {
        expectValueFunctionCantReturnNullNPE();
        MoreCollectorsTest.SINGLE_ELEMENT_LIST.stream().collect(MoreCollectors.index(MoreCollectorsTest.MyObj::getId, ( s) -> null));
    }

    @Test
    public void index_supports_duplicate_keys() {
        Multimap<Integer, MoreCollectorsTest.MyObj> multimap = MoreCollectorsTest.LIST_WITH_DUPLICATE_ID.stream().collect(MoreCollectors.index(MoreCollectorsTest.MyObj::getId));
        assertThat(multimap.keySet()).containsOnly(1, 2);
        assertThat(multimap.get(1)).containsOnly(MoreCollectorsTest.MY_OBJ_1_A, MoreCollectorsTest.MY_OBJ_1_C);
        assertThat(multimap.get(2)).containsOnly(MoreCollectorsTest.MY_OBJ_2_B);
    }

    @Test
    public void uniqueIndex_supports_duplicate_keys() {
        Multimap<Integer, String> multimap = MoreCollectorsTest.LIST_WITH_DUPLICATE_ID.stream().collect(MoreCollectors.index(MoreCollectorsTest.MyObj::getId, MoreCollectorsTest.MyObj::getText));
        assertThat(multimap.keySet()).containsOnly(1, 2);
        assertThat(multimap.get(1)).containsOnly("A", "C");
        assertThat(multimap.get(2)).containsOnly("B");
    }

    @Test
    public void index_returns_multimap() {
        Multimap<Integer, MoreCollectorsTest.MyObj> multimap = MoreCollectorsTest.LIST.stream().collect(MoreCollectors.index(MoreCollectorsTest.MyObj::getId));
        assertThat(multimap.size()).isEqualTo(3);
        Map<Integer, Collection<MoreCollectorsTest.MyObj>> map = multimap.asMap();
        assertThat(map.get(1)).containsOnly(MoreCollectorsTest.MY_OBJ_1_A);
        assertThat(map.get(2)).containsOnly(MoreCollectorsTest.MY_OBJ_2_B);
        assertThat(map.get(3)).containsOnly(MoreCollectorsTest.MY_OBJ_3_C);
    }

    @Test
    public void index_with_valueFunction_returns_multimap() {
        Multimap<Integer, String> multimap = MoreCollectorsTest.LIST.stream().collect(MoreCollectors.index(MoreCollectorsTest.MyObj::getId, MoreCollectorsTest.MyObj::getText));
        assertThat(multimap.size()).isEqualTo(3);
        Map<Integer, Collection<String>> map = multimap.asMap();
        assertThat(map.get(1)).containsOnly("A");
        assertThat(map.get(2)).containsOnly("B");
        assertThat(map.get(3)).containsOnly("C");
    }

    @Test
    public void index_parallel_stream() {
        Multimap<String, String> multimap = MoreCollectorsTest.HUGE_LIST.parallelStream().collect(MoreCollectors.index(Function.identity()));
        assertThat(multimap.keySet()).isEqualTo(MoreCollectorsTest.HUGE_SET);
    }

    @Test
    public void index_with_valueFunction_parallel_stream() {
        Multimap<String, String> multimap = MoreCollectorsTest.HUGE_LIST.parallelStream().collect(MoreCollectors.index(Function.identity(), Function.identity()));
        assertThat(multimap.keySet()).isEqualTo(MoreCollectorsTest.HUGE_SET);
    }

    @Test
    public void join_on_empty_stream_returns_empty_string() {
        assertThat(Collections.emptyList().stream().collect(MoreCollectors.join(Joiner.on(",")))).isEmpty();
    }

    @Test
    public void join_fails_with_NPE_if_joiner_is_null() {
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("Joiner can't be null");
        MoreCollectors.join(null);
    }

    @Test
    public void join_applies_joiner_to_stream() {
        assertThat(Arrays.asList("1", "2", "3", "4").stream().collect(MoreCollectors.join(Joiner.on(",")))).isEqualTo("1,2,3,4");
    }

    @Test
    public void join_does_not_support_parallel_stream_and_fails_with_ISE() {
        Stream<String> hugeStream = MoreCollectorsTest.HUGE_LIST.parallelStream();
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("Parallel processing is not supported");
        hugeStream.collect(MoreCollectors.join(Joiner.on(" ")));
    }

    @Test
    public void join_supports_null_if_joiner_does() {
        Stream<String> stream = Arrays.asList("1", null).stream();
        expectedException.expect(NullPointerException.class);
        stream.collect(MoreCollectors.join(Joiner.on(",")));
    }

    private static final class MyObj {
        private final int id;

        private final String text;

        public MyObj(int id, String text) {
            this.id = id;
            this.text = text;
        }

        public int getId() {
            return id;
        }

        public String getText() {
            return text;
        }
    }

    private enum MyEnum {

        ONE,
        TWO,
        THREE;}
}

