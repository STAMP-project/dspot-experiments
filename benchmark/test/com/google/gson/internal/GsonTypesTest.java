/**
 * Copyright (C) 2014 Google Inc.
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
package com.google.gson.internal;


import java.lang.reflect.Type;
import java.util.List;
import junit.framework.TestCase;


public final class GsonTypesTest extends TestCase {
    public void testNewParameterizedTypeWithoutOwner() throws Exception {
        // List<A>. List is a top-level class
        Type type = $Gson$Types.newParameterizedTypeWithOwner(null, List.class, GsonTypesTest.A.class);
        TestCase.assertEquals(GsonTypesTest.A.class, GsonTypesTest.getFirstTypeArgument(type));
        // A<B>. A is a static inner class.
        type = $Gson$Types.newParameterizedTypeWithOwner(null, GsonTypesTest.A.class, GsonTypesTest.B.class);
        TestCase.assertEquals(GsonTypesTest.B.class, GsonTypesTest.getFirstTypeArgument(type));
        final class D {}
        try {
            // D<A> is not allowed since D is not a static inner class
            $Gson$Types.newParameterizedTypeWithOwner(null, D.class, GsonTypesTest.A.class);
            TestCase.fail();
        } catch (IllegalArgumentException expected) {
        }
        // A<D> is allowed.
        type = $Gson$Types.newParameterizedTypeWithOwner(null, GsonTypesTest.A.class, D.class);
        TestCase.assertEquals(D.class, GsonTypesTest.getFirstTypeArgument(type));
    }

    public void testGetFirstTypeArgument() throws Exception {
        TestCase.assertNull(GsonTypesTest.getFirstTypeArgument(GsonTypesTest.A.class));
        Type type = $Gson$Types.newParameterizedTypeWithOwner(null, GsonTypesTest.A.class, GsonTypesTest.B.class, GsonTypesTest.C.class);
        TestCase.assertEquals(GsonTypesTest.B.class, GsonTypesTest.getFirstTypeArgument(type));
    }

    private static final class A {}

    private static final class B {}

    private static final class C {}
}

