/**
 * Copyright 2014 Google Inc. All Rights Reserved.
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
package com.google.security.zynamics.binnavi.disassembly;


import com.google.security.zynamics.binnavi.Database.MockClasses.MockDatabase;
import com.google.security.zynamics.binnavi.Database.MockClasses.MockSqlProvider;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@RunWith(JUnit4.class)
public class CRawModuleTest {
    @Test
    public void testCRawModule_1() {
        final MockSqlProvider sqlProvider = new MockSqlProvider();
        final MockDatabase mockDatabase = new MockDatabase(sqlProvider);
        try {
            @SuppressWarnings("unused")
            final CRawModule rawModule = new CRawModule(0, null, 0, false, null);
            Assert.fail();
        } catch (final IllegalArgumentException e) {
        }
        try {
            @SuppressWarnings("unused")
            final CRawModule rawModule = new CRawModule(23, null, 0, false, null);
            Assert.fail();
        } catch (final NullPointerException e) {
        }
        try {
            @SuppressWarnings("unused")
            final CRawModule rawModule = new CRawModule(23, "rawModule", 0, false, null);
            Assert.fail();
        } catch (final NullPointerException e) {
        }
        try {
            @SuppressWarnings("unused")
            final CRawModule rawModule = new CRawModule(23, "rawModule", 1, false, null);
            Assert.fail();
        } catch (final NullPointerException e) {
        }
        final CRawModule rawModule = new CRawModule(23, "rawModule", 1, false, sqlProvider);
        @SuppressWarnings("unused")
        final IRawModuleListener listener;
        Assert.assertEquals(1, rawModule.getFunctionCount());
        Assert.assertEquals(23, rawModule.getId());
        Assert.assertEquals("rawModule", rawModule.getName());
        Assert.assertTrue(rawModule.inSameDatabase(sqlProvider));
        Assert.assertTrue(rawModule.inSameDatabase(mockDatabase));
        Assert.assertFalse(rawModule.isComplete());
    }
}

