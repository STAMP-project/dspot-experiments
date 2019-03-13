/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.lang3;


import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;


/**
 *
 */
public class ClassPathUtilsTest {
    @Test
    public void testConstructor() {
        Assertions.assertNotNull(new ClassPathUtils());
        final Constructor<?>[] cons = ClassPathUtils.class.getDeclaredConstructors();
        Assertions.assertEquals(1, cons.length);
        Assertions.assertTrue(Modifier.isPublic(cons[0].getModifiers()));
        Assertions.assertTrue(Modifier.isPublic(ClassPathUtils.class.getModifiers()));
        Assertions.assertFalse(Modifier.isFinal(ClassPathUtils.class.getModifiers()));
    }

    @Test
    public void testToFullyQualifiedNameNullClassString() {
        Assertions.assertThrows(NullPointerException.class, () -> ClassPathUtils.toFullyQualifiedName(((Class<?>) (null)), "Test.properties"));
    }

    @Test
    public void testToFullyQualifiedNameClassNull() {
        Assertions.assertThrows(NullPointerException.class, () -> ClassPathUtils.toFullyQualifiedName(ClassPathUtils.class, null));
    }

    @Test
    public void testToFullyQualifiedNameClassString() {
        final String expected = "org.apache.commons.lang3.Test.properties";
        final String actual = ClassPathUtils.toFullyQualifiedName(ClassPathUtils.class, "Test.properties");
        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void testToFullyQualifiedNameNullPackageString() {
        Assertions.assertThrows(NullPointerException.class, () -> ClassPathUtils.toFullyQualifiedName(((Package) (null)), "Test.properties"));
    }

    @Test
    public void testToFullyQualifiedNamePackageNull() {
        Assertions.assertThrows(NullPointerException.class, () -> ClassPathUtils.toFullyQualifiedName(ClassPathUtils.class.getPackage(), null));
    }

    @Test
    public void testToFullyQualifiedNamePackageString() {
        final String expected = "org.apache.commons.lang3.Test.properties";
        final String actual = ClassPathUtils.toFullyQualifiedName(ClassPathUtils.class.getPackage(), "Test.properties");
        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void testToFullyQualifiedPathClassNullString() {
        Assertions.assertThrows(NullPointerException.class, () -> ClassPathUtils.toFullyQualifiedPath(((Class<?>) (null)), "Test.properties"));
    }

    @Test
    public void testToFullyQualifiedPathClassNull() {
        Assertions.assertThrows(NullPointerException.class, () -> ClassPathUtils.toFullyQualifiedPath(ClassPathUtils.class, null));
    }

    @Test
    public void testToFullyQualifiedPathClass() {
        final String expected = "org/apache/commons/lang3/Test.properties";
        final String actual = ClassPathUtils.toFullyQualifiedPath(ClassPathUtils.class, "Test.properties");
        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void testToFullyQualifiedPathPackageNullString() {
        Assertions.assertThrows(NullPointerException.class, () -> ClassPathUtils.toFullyQualifiedPath(((Package) (null)), "Test.properties"));
    }

    @Test
    public void testToFullyQualifiedPathPackageNull() {
        Assertions.assertThrows(NullPointerException.class, () -> ClassPathUtils.toFullyQualifiedPath(ClassPathUtils.class.getPackage(), null));
    }

    @Test
    public void testToFullyQualifiedPathPackage() {
        final String expected = "org/apache/commons/lang3/Test.properties";
        final String actual = ClassPathUtils.toFullyQualifiedPath(ClassPathUtils.class.getPackage(), "Test.properties");
        Assertions.assertEquals(expected, actual);
    }
}

