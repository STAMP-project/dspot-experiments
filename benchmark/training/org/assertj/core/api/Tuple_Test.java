/**
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *
 * Copyright 2012-2019 the original author or authors.
 */
package org.assertj.core.api;


import java.util.List;
import org.assertj.core.groups.Tuple;
import org.assertj.core.util.Arrays;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;

import static java.util.Arrays.asList;


public class Tuple_Test {
    @Test
    public void should_create_tuple() {
        Tuple tuple = new Tuple("Yoda", 800, "Jedi");
        Assertions.assertThat(tuple).isEqualTo(new Tuple("Yoda", 800, "Jedi"));
    }

    @Test
    public void tuple_equal_should_support_primitive_array() {
        Tuple tuple = new Tuple("1".getBytes(), "Name");
        Assertions.assertThat(tuple).isEqualTo(new Tuple("1".getBytes(), "Name"));
    }

    @Test
    public void should_create_empty_tuple() {
        Tuple tuple = new Tuple();
        Assertions.assertThat(tuple).isEqualTo(new Tuple());
    }

    @Test
    public void convert_tuple_to_an_array() {
        Tuple tuple = new Tuple("Yoda", 800, "Jedi");
        Assertions.assertThat(tuple.toArray()).isEqualTo(Arrays.array("Yoda", 800, "Jedi"));
    }

    @Test
    public void convert_tuple_to_a_list() {
        Tuple tuple = new Tuple("Yoda", 800, "Jedi");
        Assertions.assertThat(tuple.toList()).isEqualTo(Lists.newArrayList("Yoda", 800, "Jedi"));
    }

    @Test
    public void tuple_representation() {
        Tuple tuple = new Tuple("Yoda", 800, "Jedi");
        Assertions.assertThat(tuple).hasToString("(\"Yoda\", 800, \"Jedi\")");
    }

    @Test
    public void test_for_issue_448() {
        SinteticClass item1 = new SinteticClass("1".getBytes(), "Foo");
        SinteticClass item2 = new SinteticClass("2".getBytes(), "Bar");
        SinteticClass item3 = new SinteticClass("3".getBytes(), "Baz");
        List<SinteticClass> list = asList(item1, item2, item3);
        Assertions.assertThat(list).extracting("pk", "name").contains(Tuple.tuple("1".getBytes(), "Foo"), Tuple.tuple("2".getBytes(), "Bar"), Tuple.tuple("3".getBytes(), "Baz"));
        Assertions.assertThat(list).extracting("pk", "name").contains(Tuple.tuple("1".getBytes(), "Foo")).contains(Tuple.tuple("2".getBytes(), "Bar")).contains(Tuple.tuple("3".getBytes(), "Baz"));
    }
}

