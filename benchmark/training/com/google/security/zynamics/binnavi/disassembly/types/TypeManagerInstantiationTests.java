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
package com.google.security.zynamics.binnavi.disassembly.types;


import com.google.security.zynamics.binnavi.Database.Exceptions.CouldntLoadDataException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Test instantiation of the TypeManager class.
 */
@RunWith(JUnit4.class)
public class TypeManagerInstantiationTests {
    @Test
    public void testEmptyInitialization() throws CouldntLoadDataException {
        new TypeManager(new TypeManagerMockBackend());
    }

    @Test(expected = NullPointerException.class)
    public void testNullBackend() throws CouldntLoadDataException {
        new TypeManager(null);
    }

    @Test
    public void testStandardInitialization() throws CouldntLoadDataException {
        final RawTestTypeSystem typeSystem = new RawTestTypeSystem();
        final TypeManager manager = new TypeManager(new TypeManagerMockBackend());
        for (final BaseType baseType : manager.getTypes()) {
            TypeManagerInstantiationTests.findAndCompareType(baseType, typeSystem.getRawTypes());
        }
    }
}

