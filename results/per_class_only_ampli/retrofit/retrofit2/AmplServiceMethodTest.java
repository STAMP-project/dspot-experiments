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


public final class AmplServiceMethodTest {
    @org.junit.Test
    public void pathParameterParsing() throws java.lang.Exception {
        retrofit2.AmplServiceMethodTest.expectParams("/");
        retrofit2.AmplServiceMethodTest.expectParams("/foo");
        retrofit2.AmplServiceMethodTest.expectParams("/foo/bar");
        retrofit2.AmplServiceMethodTest.expectParams("/foo/bar/{}");
        retrofit2.AmplServiceMethodTest.expectParams("/foo/bar/{taco}", "taco");
        retrofit2.AmplServiceMethodTest.expectParams("/foo/bar/{t}", "t");
        retrofit2.AmplServiceMethodTest.expectParams("/foo/bar/{!!!}/");// Invalid parameter.
        
        retrofit2.AmplServiceMethodTest.expectParams("/foo/bar/{}/{taco}", "taco");
        retrofit2.AmplServiceMethodTest.expectParams("/foo/bar/{taco}/or/{burrito}", "taco", "burrito");
        retrofit2.AmplServiceMethodTest.expectParams("/foo/bar/{taco}/or/{taco}", "taco");
        retrofit2.AmplServiceMethodTest.expectParams("/foo/bar/{taco-shell}", "taco-shell");
        retrofit2.AmplServiceMethodTest.expectParams("/foo/bar/{taco_shell}", "taco_shell");
        retrofit2.AmplServiceMethodTest.expectParams("/foo/bar/{sha256}", "sha256");
        retrofit2.AmplServiceMethodTest.expectParams("/foo/bar/{TACO}", "TACO");
        retrofit2.AmplServiceMethodTest.expectParams("/foo/bar/{taco}/{tAco}/{taCo}", "taco", "tAco", "taCo");
        retrofit2.AmplServiceMethodTest.expectParams("/foo/bar/{1}");// Invalid parameter, name cannot start with digit.
        
    }

    private static void expectParams(java.lang.String path, java.lang.String... expected) {
        java.util.Set<java.lang.String> calculated = retrofit2.ServiceMethod.parsePathParameters(path);
        org.assertj.core.api.Assertions.assertThat(calculated).containsExactly(expected);
    }
}

