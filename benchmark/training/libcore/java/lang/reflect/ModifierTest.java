/**
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package libcore.java.lang.reflect;


import java.lang.reflect.Modifier;
import junit.framework.TestCase;


public class ModifierTest extends TestCase {
    public void test_classModifiers() {
        TestCase.assertEquals(3103, Modifier.classModifiers());
    }

    public void test_constructorModifiers() {
        TestCase.assertEquals(7, Modifier.constructorModifiers());
    }

    public void test_fieldModifiers() {
        TestCase.assertEquals(223, Modifier.fieldModifiers());
    }

    public void test_interfaceModifiers() {
        TestCase.assertEquals(3087, Modifier.interfaceModifiers());
    }

    public void test_methodModifiers() {
        TestCase.assertEquals(3391, Modifier.methodModifiers());
    }

    public void test_isAbstractI() {
        TestCase.assertTrue(Modifier.isAbstract(Modifier.ABSTRACT));
        TestCase.assertTrue((!(Modifier.isAbstract(((-1) & (~(Modifier.ABSTRACT)))))));
    }

    public void test_isFinalI() {
        TestCase.assertTrue(Modifier.isFinal(Modifier.FINAL));
        TestCase.assertTrue((!(Modifier.isFinal(((-1) & (~(Modifier.FINAL)))))));
    }

    public void test_isInterfaceI() {
        TestCase.assertTrue(Modifier.isInterface(Modifier.INTERFACE));
        TestCase.assertTrue((!(Modifier.isInterface(((-1) & (~(Modifier.INTERFACE)))))));
    }

    public void test_isNativeI() {
        TestCase.assertTrue(Modifier.isNative(Modifier.NATIVE));
        TestCase.assertTrue((!(Modifier.isNative(((-1) & (~(Modifier.NATIVE)))))));
    }

    public void test_isPrivateI() {
        TestCase.assertTrue(Modifier.isPrivate(Modifier.PRIVATE));
        TestCase.assertTrue((!(Modifier.isPrivate(((-1) & (~(Modifier.PRIVATE)))))));
    }

    public void test_isProtectedI() {
        TestCase.assertTrue(Modifier.isProtected(Modifier.PROTECTED));
        TestCase.assertTrue((!(Modifier.isProtected(((-1) & (~(Modifier.PROTECTED)))))));
    }

    public void test_isPublicI() {
        TestCase.assertTrue(Modifier.isPublic(Modifier.PUBLIC));
        TestCase.assertTrue((!(Modifier.isPublic(((-1) & (~(Modifier.PUBLIC)))))));
    }

    public void test_isStaticI() {
        TestCase.assertTrue(Modifier.isStatic(Modifier.STATIC));
        TestCase.assertTrue((!(Modifier.isStatic(((-1) & (~(Modifier.STATIC)))))));
    }

    public void test_isStrictI() {
        TestCase.assertTrue(Modifier.isStrict(Modifier.STRICT));
        TestCase.assertTrue((!(Modifier.isStrict(((-1) & (~(Modifier.STRICT)))))));
    }

    public void test_isSynchronizedI() {
        TestCase.assertTrue(Modifier.isSynchronized(Modifier.SYNCHRONIZED));
        TestCase.assertTrue((!(Modifier.isSynchronized(((-1) & (~(Modifier.SYNCHRONIZED)))))));
    }

    public void test_isTransientI() {
        TestCase.assertTrue(Modifier.isTransient(Modifier.TRANSIENT));
        TestCase.assertTrue((!(Modifier.isTransient(((-1) & (~(Modifier.TRANSIENT)))))));
    }

    public void test_isVolatileI() {
        TestCase.assertTrue(Modifier.isVolatile(Modifier.VOLATILE));
        TestCase.assertTrue((!(Modifier.isVolatile(((-1) & (~(Modifier.VOLATILE)))))));
    }

    public void test_toStringI() {
        TestCase.assertEquals("public abstract", Modifier.toString(((Modifier.PUBLIC) | (Modifier.ABSTRACT))));
    }
}

