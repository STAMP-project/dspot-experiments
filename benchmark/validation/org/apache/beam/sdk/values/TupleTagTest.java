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


import org.apache.beam.vendor.guava.v20_0.com.google.common.base.Splitter;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.Iterables;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for {@link TupleTag}.
 */
@RunWith(JUnit4.class)
public class TupleTagTest {
    private static TupleTag<Object> staticTag = new TupleTag();

    private static TupleTag<Object> staticBlockTag;

    private static TupleTag<Object> staticMethodTag = TupleTagTest.createTag();

    private static TupleTag<Object> instanceMethodTag = new TupleTagTest.AnotherClass().createAnotherTag();

    static {
        TupleTagTest.staticBlockTag = new TupleTag();
    }

    private static class AnotherClass {
        private static TupleTag<Object> anotherTag = new TupleTag();

        private TupleTag<Object> createAnotherTag() {
            return new TupleTag();
        }
    }

    @Test
    public void testStaticTupleTag() {
        Assert.assertEquals("org.apache.beam.sdk.values.TupleTagTest#0", TupleTagTest.staticTag.getId());
        Assert.assertEquals("org.apache.beam.sdk.values.TupleTagTest#3", TupleTagTest.staticBlockTag.getId());
        Assert.assertEquals("org.apache.beam.sdk.values.TupleTagTest#1", TupleTagTest.staticMethodTag.getId());
        Assert.assertEquals("org.apache.beam.sdk.values.TupleTagTest#2", TupleTagTest.instanceMethodTag.getId());
        Assert.assertEquals("org.apache.beam.sdk.values.TupleTagTest$AnotherClass#0", TupleTagTest.AnotherClass.anotherTag.getId());
    }

    @Test
    public void testNonstaticTupleTag() {
        Assert.assertNotEquals(new TupleTag().getId(), new TupleTag().getId());
        Assert.assertNotEquals(createNonstaticTupleTag(), createNonstaticTupleTag());
        TupleTag<Object> tag = createNonstaticTupleTag();
        // Check that the name is derived from the method it is created in.
        Assert.assertThat(Iterables.get(Splitter.on('#').split(tag.getId()), 0), Matchers.startsWith("org.apache.beam.sdk.values.TupleTagTest.createNonstaticTupleTag"));
        // Check that after the name there is a ':' followed by a line number, and just make
        // sure the line number is big enough to be reasonable, so superficial changes don't break
        // the test.
        Assert.assertThat(Integer.parseInt(Iterables.get(Splitter.on(':').split(Iterables.get(Splitter.on('#').split(tag.getId()), 0)), 1)), Matchers.greaterThan(15));
    }
}

