/**
 * Copyright (C) 2013 Square, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package retrofit2;


import org.junit.Test;


// TODO this test is far too white box, migrate to black box.
public final class RequestFactoryBuilderTest {
    @Test
    public void pathParameterParsing() throws Exception {
        RequestFactoryBuilderTest.expectParams("/");
        RequestFactoryBuilderTest.expectParams("/foo");
        RequestFactoryBuilderTest.expectParams("/foo/bar");
        RequestFactoryBuilderTest.expectParams("/foo/bar/{}");
        RequestFactoryBuilderTest.expectParams("/foo/bar/{taco}", "taco");
        RequestFactoryBuilderTest.expectParams("/foo/bar/{t}", "t");
        RequestFactoryBuilderTest.expectParams("/foo/bar/{!!!}/");// Invalid parameter.

        RequestFactoryBuilderTest.expectParams("/foo/bar/{}/{taco}", "taco");
        RequestFactoryBuilderTest.expectParams("/foo/bar/{taco}/or/{burrito}", "taco", "burrito");
        RequestFactoryBuilderTest.expectParams("/foo/bar/{taco}/or/{taco}", "taco");
        RequestFactoryBuilderTest.expectParams("/foo/bar/{taco-shell}", "taco-shell");
        RequestFactoryBuilderTest.expectParams("/foo/bar/{taco_shell}", "taco_shell");
        RequestFactoryBuilderTest.expectParams("/foo/bar/{sha256}", "sha256");
        RequestFactoryBuilderTest.expectParams("/foo/bar/{TACO}", "TACO");
        RequestFactoryBuilderTest.expectParams("/foo/bar/{taco}/{tAco}/{taCo}", "taco", "tAco", "taCo");
        RequestFactoryBuilderTest.expectParams("/foo/bar/{1}");// Invalid parameter, name cannot start with digit.

    }
}

