/**
 * Copyright 2015 ThoughtWorks, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.thoughtworks.go.util;


import org.junit.Assert;
import org.junit.Test;


public class TriStateTest {
    @Test
    public void testTrueShouldBeTruthy() throws Exception {
        TriState triState = TriState.from("tRuE");
        Assert.assertTrue(triState.isTrue());
        Assert.assertTrue(triState.isTruthy());
        Assert.assertFalse(triState.isFalsy());
        Assert.assertFalse(triState.isFalse());
    }

    @Test
    public void testFalseShouldBeTruthy() throws Exception {
        TriState triState = TriState.from("FaLsE");
        Assert.assertTrue(triState.isFalsy());
        Assert.assertTrue(triState.isFalse());
        Assert.assertFalse(triState.isTrue());
        Assert.assertFalse(triState.isTruthy());
    }

    @Test
    public void testUnsetShouldBeTruthy() throws Exception {
        TriState triState = TriState.from(null);
        Assert.assertTrue(triState.isFalsy());
        Assert.assertFalse(triState.isFalse());
        Assert.assertFalse(triState.isTrue());
        Assert.assertFalse(triState.isTruthy());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBadStringShouldRaiseError() throws Exception {
        TriState.from("foo");
    }
}

