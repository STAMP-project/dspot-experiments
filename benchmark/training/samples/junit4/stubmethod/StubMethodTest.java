/**
 * Copyright 2009 the original author or authors.
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
package samples.junit4.stubmethod;


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.support.membermodification.MemberMatcher;
import org.powermock.api.support.membermodification.MemberModifier;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.exceptions.MethodNotFoundException;
import org.powermock.reflect.exceptions.TooManyMethodsFoundException;
import samples.suppressmethod.SuppressMethod;

import static junit.framework.Assert.fail;


@RunWith(PowerMockRunner.class)
@PrepareForTest(SuppressMethod.class)
public class StubMethodTest {
    @Test
    public void whenStubbingInstanceMethodTheMethodReturnsTheStubbedValue() throws Exception {
        String expectedValue = "Hello";
        MemberModifier.stub(MemberMatcher.method(SuppressMethod.class, "getObject")).toReturn(expectedValue);
        SuppressMethod tested = new SuppressMethod();
        Assert.assertEquals(expectedValue, tested.getObject());
        Assert.assertEquals(expectedValue, tested.getObject());
    }

    @Test
    public void whenStubbingStaticMethodTheMethodReturnsTheStubbedValue() throws Exception {
        String expectedValue = "Hello";
        MemberModifier.stub(MemberMatcher.method(SuppressMethod.class, "getObjectStatic")).toReturn(expectedValue);
        Assert.assertEquals(expectedValue, SuppressMethod.getObjectStatic());
        Assert.assertEquals(expectedValue, SuppressMethod.getObjectStatic());
    }

    @Test
    public void whenStubbingInstanceMethodWithPrimiteValueTheMethodReturnsTheStubbedValue() throws Exception {
        float expectedValue = 4;
        MemberModifier.stub(MemberMatcher.method(SuppressMethod.class, "getFloat")).toReturn(expectedValue);
        SuppressMethod tested = new SuppressMethod();
        Assert.assertEquals(expectedValue, tested.getFloat(), 0.0F);
        Assert.assertEquals(expectedValue, tested.getFloat(), 0.0F);
    }

    @Test(expected = TooManyMethodsFoundException.class)
    public void whenSeveralMethodsFoundThenTooManyMethodsFoundExceptionIsThrown() throws Exception {
        MemberModifier.stub(MemberMatcher.method(SuppressMethod.class, "sameName"));
    }

    @Test(expected = MethodNotFoundException.class)
    public void whenNoMethodsFoundThenMethodNotFoundExceptionIsThrown() throws Exception {
        MemberModifier.stub(MemberMatcher.method(SuppressMethod.class, "notFound"));
    }

    @Test
    public void whenStubbingInstanceMethodByPassingTheMethodTheMethodReturnsTheStubbedValue() throws Exception {
        String expected = "Hello";
        MemberModifier.stub(MemberMatcher.method(SuppressMethod.class, "getObject")).toReturn(expected);
        SuppressMethod tested = new SuppressMethod();
        Assert.assertEquals(expected, tested.getObject());
        Assert.assertEquals(expected, tested.getObject());
    }

    @Test
    public void whenStubbingStaticMethodByPassingTheMethodTheMethodReturnsTheStubbedValue() throws Exception {
        String expected = "Hello";
        MemberModifier.stub(MemberMatcher.method(SuppressMethod.class, "getObjectStatic")).toReturn(expected);
        Assert.assertEquals(expected, SuppressMethod.getObjectStatic());
        Assert.assertEquals(expected, SuppressMethod.getObjectStatic());
    }

    @Test(expected = ClassCastException.class)
    public void whenStubbingInstanceMethodWithWrongReturnTypeThenClasscastExceptionIsThrown() throws Exception {
        String illegalReturnType = "Hello";
        MemberModifier.stub(MemberMatcher.method(SuppressMethod.class, "getFloat")).toReturn(illegalReturnType);
        SuppressMethod tested = new SuppressMethod();
        tested.getFloat();
    }

    @Test
    public void whenStubbingInstanceMethodToThrowExceptionTheMethodThrowsTheStubbedException() throws Exception {
        Exception expected = new Exception("message");
        MemberModifier.stub(MemberMatcher.method(SuppressMethod.class, "getObject")).toThrow(expected);
        SuppressMethod tested = new SuppressMethod();
        try {
            tested.getObject();
            fail();
        } catch (Exception e) {
            Assert.assertEquals("message", e.getMessage());
        }
    }

    @Test
    public void whenStubbingStaticMethodToThrowExceptionTheMethodThrowsTheStubbedException() throws Exception {
        Exception expected = new Exception("message");
        MemberModifier.stub(MemberMatcher.method(SuppressMethod.class, "getObjectStatic")).toThrow(expected);
        try {
            SuppressMethod.getObjectStatic();
            fail();
        } catch (Exception e) {
            Assert.assertEquals("message", e.getMessage());
        }
    }
}

