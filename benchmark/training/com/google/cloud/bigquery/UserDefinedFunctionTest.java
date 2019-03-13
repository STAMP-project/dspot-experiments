/**
 * Copyright 2015 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.cloud.bigquery;


import UserDefinedFunction.Type.FROM_URI;
import UserDefinedFunction.Type.INLINE;
import org.junit.Assert;
import org.junit.Test;


public class UserDefinedFunctionTest {
    private static final String INLINE = "inline";

    private static final String URI = "uri";

    private static final UserDefinedFunction INLINE_FUNCTION = new UserDefinedFunction.InlineFunction(UserDefinedFunctionTest.INLINE);

    private static final UserDefinedFunction URI_FUNCTION = new UserDefinedFunction.UriFunction(UserDefinedFunctionTest.URI);

    @Test
    public void testConstructor() {
        Assert.assertEquals(UserDefinedFunctionTest.INLINE, UserDefinedFunctionTest.INLINE_FUNCTION.getContent());
        Assert.assertEquals(UserDefinedFunction.Type.INLINE, UserDefinedFunctionTest.INLINE_FUNCTION.getType());
        Assert.assertEquals(UserDefinedFunctionTest.URI, UserDefinedFunctionTest.URI_FUNCTION.getContent());
        Assert.assertEquals(FROM_URI, UserDefinedFunctionTest.URI_FUNCTION.getType());
    }

    @Test
    public void testFactoryMethod() {
        compareUserDefinedFunction(UserDefinedFunctionTest.INLINE_FUNCTION, UserDefinedFunction.inline(UserDefinedFunctionTest.INLINE));
        compareUserDefinedFunction(UserDefinedFunctionTest.URI_FUNCTION, UserDefinedFunction.fromUri(UserDefinedFunctionTest.URI));
    }

    @Test
    public void testToAndFromPb() {
        compareUserDefinedFunction(UserDefinedFunctionTest.INLINE_FUNCTION, UserDefinedFunction.fromPb(UserDefinedFunctionTest.INLINE_FUNCTION.toPb()));
        compareUserDefinedFunction(UserDefinedFunctionTest.URI_FUNCTION, UserDefinedFunction.fromPb(UserDefinedFunctionTest.URI_FUNCTION.toPb()));
    }
}

