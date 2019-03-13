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
package org.apache.ambari.server.utils;


import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import junit.framework.Assert;
import org.junit.Test;


/**
 * Complementary class used in tests to check if string representation corresponds to Set/Map
 */
public class CollectionPresentationUtils {
    @Test
    public void testIsStringPermutationOfCollection() {
        String input1 = "{\"foo\":\"bar\",\"foobar\":\"baz\"}";
        String input2 = "{\"foobar\":\"baz\",\"foo\":\"bar\"}";
        String input3 = "{\"fooba\":\"baz\",\"foo\":\"bar\"}";
        Set<String> expected = new HashSet<>(Arrays.asList(new String[]{ "\"foo\":\"bar\"", "\"foobar\":\"baz\"" }));
        Assert.assertTrue(CollectionPresentationUtils.isStringPermutationOfCollection(input1, expected, ",", 1, 1));
        Assert.assertTrue(CollectionPresentationUtils.isStringPermutationOfCollection(input2, expected, ",", 1, 1));
        Assert.assertFalse(CollectionPresentationUtils.isStringPermutationOfCollection(input3, expected, ",", 1, 1));
    }
}

