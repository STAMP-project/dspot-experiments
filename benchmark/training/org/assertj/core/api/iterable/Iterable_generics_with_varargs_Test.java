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
package org.assertj.core.api.iterable;


import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 *
 *
 * @author Johannes Schneider (<a href="mailto:js@cedarsoft.com">js@cedarsoft.com</a>)
 */
public class Iterable_generics_with_varargs_Test {
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Test
    public void testWithoutGenerics() {
        List strings = Arrays.asList("a", "b", "c");
        Assertions.assertThat(strings).contains("a", "b");
    }

    @Test
    public void testConcreteType() {
        List<String> strings = Arrays.asList("a", "b", "c");
        Assertions.assertThat(strings).contains("a", "b");
    }

    @Test
    public void testListAssertWithGenericsAndExtracting() {
        List<? extends String> strings = Arrays.asList("a", "b", "c");
        Function<? super String, String> doubleFunction = new Function<String, String>() {
            @Override
            public String apply(String s) {
                return s + s;
            }
        };
        Assertions.assertThat(strings).extracting(doubleFunction, doubleFunction).contains(Assertions.tuple("aa", "aa"), Assertions.tuple("bb", "bb"));
    }
}

