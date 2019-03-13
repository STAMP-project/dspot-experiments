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
package org.assertj.core.util;


import java.util.stream.Stream;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;


public class Streams_stream_Test {
    @Test
    public void should_build_stream_from_iterable() {
        // GIVEN
        Iterable<String> iterable = Lists.list("a", "b", "c");
        // WHEN
        Stream<String> stream = Streams.stream(iterable);
        // THEN
        Assertions.assertThat(stream).containsExactly("a", "b", "c");
    }
}

