/**
 * Copyright (C) 2010 The Android Open Source Project
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


import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Arrays;
import java.util.List;
import junit.framework.TestCase;


public final class GenericExceptionsTest extends TestCase {
    public void testGenericExceptionsOfMethodsWithTypeParameters() throws Exception {
        Method method = GenericExceptionsTest.Thrower.class.getMethod("parameterizedMethod");
        TestCase.assertEquals(Arrays.<Type>asList(IOException.class), Arrays.asList(method.getGenericExceptionTypes()));
    }

    public void testGenericExceptionsOfMethodsWithGenericParameters() throws Exception {
        Method method = GenericExceptionsTest.Thrower.class.getMethod("genericParameters", List.class);
        TestCase.assertEquals(Arrays.<Type>asList(IOException.class), Arrays.asList(method.getGenericExceptionTypes()));
    }

    public void testGenericExceptionsOfConstructorsWithTypeParameters() throws Exception {
        Constructor constructor = GenericExceptionsTest.Thrower.class.getConstructor();
        TestCase.assertEquals(Arrays.<Type>asList(IOException.class), Arrays.asList(constructor.getGenericExceptionTypes()));
    }

    public void testGenericExceptionsOfConstructorsWithGenericParameters() throws Exception {
        Constructor constructor = GenericExceptionsTest.Thrower.class.getConstructor(List.class);
        TestCase.assertEquals(Arrays.<Type>asList(IOException.class), Arrays.asList(constructor.getGenericExceptionTypes()));
    }

    public void testConstructorThrowingTypeVariable() throws Exception {
        Constructor constructor = GenericExceptionsTest.ThrowerT.class.getConstructor();
        TypeVariable typeVariable = getOnlyValue(constructor.getGenericExceptionTypes(), TypeVariable.class);
        TestCase.assertEquals("T", typeVariable.getName());
        TestCase.assertEquals(Arrays.<Type>asList(Throwable.class), Arrays.asList(typeVariable.getBounds()));
    }

    public void testMethodThrowingTypeVariable() throws Exception {
        Method method = GenericExceptionsTest.ThrowerT.class.getMethod("throwsTypeVariable");
        TypeVariable typeVariable = getOnlyValue(method.getGenericExceptionTypes(), TypeVariable.class);
        TestCase.assertEquals("T", typeVariable.getName());
        TestCase.assertEquals(Arrays.<Type>asList(Throwable.class), Arrays.asList(typeVariable.getBounds()));
    }

    public void testThrowingMethodTypeParameter() throws Exception {
        Method method = GenericExceptionsTest.ThrowerT.class.getMethod("throwsMethodTypeParameter");
        TypeVariable typeVariable = getOnlyValue(method.getGenericExceptionTypes(), TypeVariable.class);
        TestCase.assertEquals("X", typeVariable.getName());
        TestCase.assertEquals(Arrays.<Type>asList(Exception.class), Arrays.asList(typeVariable.getBounds()));
    }

    public void testThrowingMethodThrowsEverything() throws Exception {
        Method method = GenericExceptionsTest.ThrowerT.class.getMethod("throwsEverything");
        Type[] exceptions = method.getGenericExceptionTypes();
        TypeVariable t = ((TypeVariable) (exceptions[0]));
        TestCase.assertEquals(3, exceptions.length);
        TestCase.assertEquals("T", t.getName());
        TestCase.assertEquals(Arrays.<Type>asList(Throwable.class), Arrays.asList(t.getBounds()));
        TestCase.assertEquals(Exception.class, exceptions[1]);
        TypeVariable x = ((TypeVariable) (exceptions[2]));
        TestCase.assertEquals("X", x.getName());
        TestCase.assertEquals(Arrays.<Type>asList(Exception.class), Arrays.asList(x.getBounds()));
    }

    static class Thrower {
        public <T> Thrower() throws IOException {
        }

        public Thrower(List<?> unused) throws IOException {
        }

        public <T> void parameterizedMethod() throws IOException {
        }

        public void genericParameters(List<?> unused) throws IOException {
        }
    }

    static class ThrowerT<T extends Throwable> {
        public ThrowerT() throws T {
        }

        public void throwsTypeVariable() throws T {
        }

        public <X extends Exception> void throwsMethodTypeParameter() throws X {
        }

        public <X extends Exception> void throwsEverything() throws T, X, Exception {
        }
    }
}

