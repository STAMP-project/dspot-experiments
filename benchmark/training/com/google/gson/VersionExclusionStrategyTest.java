/**
 * Copyright (C) 2008 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.gson;


import com.google.gson.annotations.Since;
import com.google.gson.internal.Excluder;
import junit.framework.TestCase;


/**
 * Unit tests for the {@link Excluder} class.
 *
 * @author Joel Leitch
 */
public class VersionExclusionStrategyTest extends TestCase {
    private static final double VERSION = 5.0;

    public void testClassAndFieldAreAtSameVersion() throws Exception {
        Excluder excluder = Excluder.DEFAULT.withVersion(VersionExclusionStrategyTest.VERSION);
        TestCase.assertFalse(excluder.excludeClass(VersionExclusionStrategyTest.MockObject.class, true));
        TestCase.assertFalse(excluder.excludeField(VersionExclusionStrategyTest.MockObject.class.getField("someField"), true));
    }

    public void testClassAndFieldAreBehindInVersion() throws Exception {
        Excluder excluder = Excluder.DEFAULT.withVersion(((VersionExclusionStrategyTest.VERSION) + 1));
        TestCase.assertFalse(excluder.excludeClass(VersionExclusionStrategyTest.MockObject.class, true));
        TestCase.assertFalse(excluder.excludeField(VersionExclusionStrategyTest.MockObject.class.getField("someField"), true));
    }

    public void testClassAndFieldAreAheadInVersion() throws Exception {
        Excluder excluder = Excluder.DEFAULT.withVersion(((VersionExclusionStrategyTest.VERSION) - 1));
        TestCase.assertTrue(excluder.excludeClass(VersionExclusionStrategyTest.MockObject.class, true));
        TestCase.assertTrue(excluder.excludeField(VersionExclusionStrategyTest.MockObject.class.getField("someField"), true));
    }

    @Since(VersionExclusionStrategyTest.VERSION)
    private static class MockObject {
        @Since(VersionExclusionStrategyTest.VERSION)
        public final int someField = 0;
    }
}

