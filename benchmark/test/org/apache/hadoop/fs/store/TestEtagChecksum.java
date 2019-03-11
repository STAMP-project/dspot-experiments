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
package org.apache.hadoop.fs.store;


import org.junit.Assert;
import org.junit.Test;


/**
 * Unit test of etag operations.
 */
public class TestEtagChecksum extends Assert {
    private final EtagChecksum empty1 = tag("");

    private final EtagChecksum empty2 = tag("");

    private final EtagChecksum valid1 = tag("valid");

    private final EtagChecksum valid2 = tag("valid");

    @Test
    public void testEmptyTagsEqual() {
        Assert.assertEquals(empty1, empty2);
    }

    @Test
    public void testEmptyTagRoundTrip() throws Throwable {
        Assert.assertEquals(empty1, roundTrip(empty1));
    }

    @Test
    public void testValidTagsEqual() {
        Assert.assertEquals(valid1, valid2);
    }

    @Test
    public void testValidTagRoundTrip() throws Throwable {
        Assert.assertEquals(valid1, roundTrip(valid1));
    }

    @Test
    public void testValidAndEmptyTagsDontMatch() {
        Assert.assertNotEquals(valid1, empty1);
        Assert.assertNotEquals(valid1, tag("other valid one"));
    }

    @Test
    public void testDifferentTagsDontMatch() {
        Assert.assertNotEquals(valid1, tag("other valid one"));
    }
}

