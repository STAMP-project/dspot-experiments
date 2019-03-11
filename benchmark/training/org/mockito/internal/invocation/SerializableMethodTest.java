/**
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockito.internal.invocation;


import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import org.junit.Assert;
import org.junit.Test;
import org.mockitoutil.TestBase;


// TODO: add tests for generated equals() method
public class SerializableMethodTest extends TestBase {
    private MockitoMethod method;

    private Method toStringMethod;

    private Class<?>[] args;

    @Test
    public void shouldBeSerializable() throws Exception {
        ByteArrayOutputStream serialized = new ByteArrayOutputStream();
        new ObjectOutputStream(serialized).writeObject(method);
    }

    @Test
    public void shouldBeAbleToRetrieveMethodExceptionTypes() throws Exception {
        Assert.assertArrayEquals(toStringMethod.getExceptionTypes(), method.getExceptionTypes());
    }

    @Test
    public void shouldBeAbleToRetrieveMethodName() throws Exception {
        Assert.assertEquals(toStringMethod.getName(), method.getName());
    }

    @Test
    public void shouldBeAbleToCheckIsArgVargs() throws Exception {
        Assert.assertEquals(toStringMethod.isVarArgs(), method.isVarArgs());
    }

    @Test
    public void shouldBeAbleToGetParameterTypes() throws Exception {
        Assert.assertArrayEquals(toStringMethod.getParameterTypes(), method.getParameterTypes());
    }

    @Test
    public void shouldBeAbleToGetReturnType() throws Exception {
        Assert.assertEquals(toStringMethod.getReturnType(), method.getReturnType());
    }

    @Test
    public void shouldBeEqualForTwoInstances() throws Exception {
        Assert.assertTrue(new SerializableMethod(toStringMethod).equals(method));
    }

    @Test
    public void shouldNotBeEqualForSameMethodFromTwoDifferentClasses() throws Exception {
        Method testBaseToStringMethod = String.class.getMethod("toString", args);
        Assert.assertFalse(new SerializableMethod(testBaseToStringMethod).equals(method));
    }
}

