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
package io.helidon.webserver;


import PathPattern.CanonicalPathMatcher;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;


/**
 * Class CanonicalPathMatcherTest.
 */
public class CanonicalPathMatcherTest {
    @Test
    public void testLeadingSlashMatching() {
        PathPattern.CanonicalPathMatcher matcher = new PathPattern.CanonicalPathMatcher("/");
        MatcherAssert.assertThat(matcher.prefixMatch("/greet/me"), CoreMatchers.is(new PathPattern.PositiveResult(null, "/greet/me")));
    }
}

