/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.kafka.message;


import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;


public class MessageGeneratorTest {
    @Rule
    public final Timeout globalTimeout = Timeout.millis(120000);

    @Test
    public void testCapitalizeFirst() throws Exception {
        Assert.assertEquals("", MessageGenerator.capitalizeFirst(""));
        Assert.assertEquals("AbC", MessageGenerator.capitalizeFirst("abC"));
    }

    @Test
    public void testLowerCaseFirst() throws Exception {
        Assert.assertEquals("", MessageGenerator.lowerCaseFirst(""));
        Assert.assertEquals("fORTRAN", MessageGenerator.lowerCaseFirst("FORTRAN"));
        Assert.assertEquals("java", MessageGenerator.lowerCaseFirst("java"));
    }

    @Test
    public void testFirstIsCapitalized() throws Exception {
        Assert.assertFalse(MessageGenerator.firstIsCapitalized(""));
        Assert.assertTrue(MessageGenerator.firstIsCapitalized("FORTRAN"));
        Assert.assertFalse(MessageGenerator.firstIsCapitalized("java"));
    }

    @Test
    public void testToSnakeCase() throws Exception {
        Assert.assertEquals("", MessageGenerator.toSnakeCase(""));
        Assert.assertEquals("foo_bar_baz", MessageGenerator.toSnakeCase("FooBarBaz"));
        Assert.assertEquals("foo_bar_baz", MessageGenerator.toSnakeCase("fooBarBaz"));
        Assert.assertEquals("fortran", MessageGenerator.toSnakeCase("FORTRAN"));
    }
}

