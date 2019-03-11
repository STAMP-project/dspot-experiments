/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.druid.segment.data;


import GenericIndexed.STRING_STRATEGY;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 */
public class GenericIndexedTest {
    @Test(expected = UnsupportedOperationException.class)
    public void testNotSortedNoIndexOf() {
        GenericIndexed.fromArray(new String[]{ "a", "c", "b" }, STRING_STRATEGY).indexOf("a");
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testSerializationNotSortedNoIndexOf() throws Exception {
        serializeAndDeserialize(GenericIndexed.fromArray(new String[]{ "a", "c", "b" }, STRING_STRATEGY)).indexOf("a");
    }

    @Test
    public void testSanity() {
        final String[] strings = new String[]{ "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l" };
        Indexed<String> indexed = GenericIndexed.fromArray(strings, STRING_STRATEGY);
        checkBasicAPIs(strings, indexed, true);
        Assert.assertEquals((-13), indexed.indexOf("q"));
        Assert.assertEquals((-9), indexed.indexOf("howdydo"));
        Assert.assertEquals((-1), indexed.indexOf("1111"));
    }

    @Test
    public void testSortedSerialization() throws Exception {
        final String[] strings = new String[]{ "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l" };
        GenericIndexed<String> deserialized = serializeAndDeserialize(GenericIndexed.fromArray(strings, STRING_STRATEGY));
        checkBasicAPIs(strings, deserialized, true);
        Assert.assertEquals((-13), deserialized.indexOf("q"));
        Assert.assertEquals((-9), deserialized.indexOf("howdydo"));
        Assert.assertEquals((-1), deserialized.indexOf("1111"));
    }

    @Test
    public void testNotSortedSerialization() throws Exception {
        final String[] strings = new String[]{ "a", "b", "c", "d", "e", "f", "g", "h", "i", "k", "j", "l" };
        GenericIndexed<String> deserialized = serializeAndDeserialize(GenericIndexed.fromArray(strings, STRING_STRATEGY));
        checkBasicAPIs(strings, deserialized, false);
    }
}

