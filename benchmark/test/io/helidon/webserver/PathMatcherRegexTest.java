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
package io.helidon.webserver;


import io.helidon.webserver.utils.TestUtils;
import org.junit.jupiter.api.Test;


/**
 * The PathTemplateRegexTest.
 */
public class PathMatcherRegexTest {
    @Test
    public void testSimpleVar() throws Exception {
        String pathVarPattern = "(?<var1>[^/]+)";
        patternTest(true, (("a/" + pathVarPattern) + "/c"), "a/b/c", TestUtils.toMap("var1", "b"));
        patternTest(false, (("a/" + pathVarPattern) + "/d"), "a/b/c/d");
        patternTest(false, (pathVarPattern + "/c"), "a/b/c");
        patternTest(false, pathVarPattern, "a/b/c");
        patternTest(false, pathVarPattern, "a/b");
        patternTest(true, pathVarPattern, "a", TestUtils.toMap("var1", "a"));
    }
}

