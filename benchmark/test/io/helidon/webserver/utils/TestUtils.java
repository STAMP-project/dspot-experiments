/**
 * Copyright (c) 2017, 2018 Oracle and/or its affiliates. All rights reserved.
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
package io.helidon.webserver.utils;


import java.util.Collections;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.Is;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * The TestUtils.
 */
public class TestUtils {
    @Test
    public void testMyMatcherFailure() throws Exception {
        Map<String, String> map = Collections.singletonMap("my-key", "my-value");
        try {
            MatcherAssert.assertThat(map, TestUtils.matcher(( foo) -> foo.get("not-a-key"), Is.is("my-value")));
            Assertions.fail("Should have thrown an exception");
        } catch (AssertionError e) {
            Logger.getLogger(getClass().getName()).log(Level.FINE, "THIS IS NOT A FAILURE. The assertion message would be:", e);
            MatcherAssert.assertThat(e, hasProperty("message", stringContainsInOrder(Stream.of("Expected:", "lambda call result", "is", "\"my-value\"", "but:", "was null").collect(Collectors.toList()))));
        }
    }

    @Test
    public void testMyMatcher() throws Exception {
        Map<String, String> map = Collections.singletonMap("my-key", "my-value");
        MatcherAssert.assertThat(map, TestUtils.matcher(( testedMap) -> testedMap.get("my-key"), Is.is("my-value")));
    }

    @FunctionalInterface
    public interface RunnableWithException {
        void run() throws Exception;
    }

    @Test
    public void testMySupplierMatcher() throws Exception {
        Map<String, String> map = Collections.singletonMap("my-key", "my-value");
        MatcherAssert.assertThat(map, TestUtils.matcher(() -> map.get("my-key"), Is.is("my-value")));
    }
}

