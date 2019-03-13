/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.beam.sdk.values;


import java.util.List;
import java.util.Set;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for {@link TypeDescriptors}.
 */
@RunWith(JUnit4.class)
public class TypeDescriptorsTest {
    @Test
    public void testTypeDescriptorsIterables() throws Exception {
        TypeDescriptor<Iterable<String>> descriptor = TypeDescriptors.iterables(TypeDescriptors.strings());
        Assert.assertEquals(descriptor, new TypeDescriptor<Iterable<String>>() {});
    }

    @Test
    public void testTypeDescriptorsSets() throws Exception {
        TypeDescriptor<Set<String>> descriptor = TypeDescriptors.sets(TypeDescriptors.strings());
        Assert.assertEquals(descriptor, new TypeDescriptor<Set<String>>() {});
    }

    @Test
    public void testTypeDescriptorsKV() throws Exception {
        TypeDescriptor<KV<String, Integer>> descriptor = TypeDescriptors.kvs(TypeDescriptors.strings(), TypeDescriptors.integers());
        Assert.assertEquals(descriptor, new TypeDescriptor<KV<String, Integer>>() {});
    }

    @Test
    public void testTypeDescriptorsLists() throws Exception {
        TypeDescriptor<List<String>> descriptor = TypeDescriptors.lists(TypeDescriptors.strings());
        Assert.assertEquals(descriptor, new TypeDescriptor<List<String>>() {});
        Assert.assertNotEquals(descriptor, new TypeDescriptor<List<Boolean>>() {});
    }

    @Test
    public void testTypeDescriptorsListsOfLists() throws Exception {
        TypeDescriptor<List<List<String>>> descriptor = TypeDescriptors.lists(TypeDescriptors.lists(TypeDescriptors.strings()));
        Assert.assertEquals(descriptor, new TypeDescriptor<List<List<String>>>() {});
        Assert.assertNotEquals(descriptor, new TypeDescriptor<List<String>>() {});
        Assert.assertNotEquals(descriptor, new TypeDescriptor<List<Boolean>>() {});
    }

    private interface Generic<FooT, BarT> {}

    @Test
    public void testTypeDescriptorsTypeParameterOf() throws Exception {
        Assert.assertEquals(TypeDescriptors.strings(), TypeDescriptorsTest.extractFooT(new TypeDescriptorsTest.Generic<String, Integer>() {}));
        Assert.assertEquals(TypeDescriptors.integers(), TypeDescriptorsTest.extractBarT(new TypeDescriptorsTest.Generic<String, Integer>() {}));
        Assert.assertEquals(TypeDescriptors.kvs(TypeDescriptors.strings(), TypeDescriptors.integers()), TypeDescriptorsTest.extractKV(new TypeDescriptorsTest.Generic<String, Integer>() {}));
    }

    @Test
    public void testTypeDescriptorsTypeParameterOfErased() throws Exception {
        TypeDescriptorsTest.Generic<Integer, String> instance = TypeDescriptorsTest.typeErasedGeneric();
        TypeDescriptor<Integer> fooT = TypeDescriptorsTest.extractFooT(instance);
        Assert.assertNotNull(fooT);
        // Using toString() assertions because verifying the contents of a Type is very cumbersome,
        // and the expected types can not be easily constructed directly.
        Assert.assertEquals("ActualFooT", fooT.toString());
        Assert.assertEquals(TypeDescriptors.strings(), TypeDescriptorsTest.extractBarT(instance));
        TypeDescriptor<KV<Integer, String>> kvT = TypeDescriptorsTest.extractKV(instance);
        Assert.assertNotNull(kvT);
        Assert.assertThat(kvT.toString(), CoreMatchers.containsString("KV<ActualFooT, java.lang.String>"));
    }
}

