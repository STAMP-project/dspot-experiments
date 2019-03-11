/**
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockito.internal.stubbing.defaultanswers;


import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.ThrowableAssert;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.exceptions.verification.SmartNullPointerException;
import org.mockito.stubbing.Answer;
import org.mockitoutil.TestBase;


public class ReturnsSmartNullsTest extends TestBase {
    @Test
    public void should_return_the_usual_default_values_for_primitives() throws Throwable {
        Answer<Object> answer = new ReturnsSmartNulls();
        Assert.assertEquals(false, answer.answer(TestBase.invocationOf(HasPrimitiveMethods.class, "booleanMethod")));
        Assert.assertEquals(((char) (0)), answer.answer(TestBase.invocationOf(HasPrimitiveMethods.class, "charMethod")));
        Assert.assertEquals(((byte) (0)), answer.answer(TestBase.invocationOf(HasPrimitiveMethods.class, "byteMethod")));
        Assert.assertEquals(((short) (0)), answer.answer(TestBase.invocationOf(HasPrimitiveMethods.class, "shortMethod")));
        Assert.assertEquals(0, answer.answer(TestBase.invocationOf(HasPrimitiveMethods.class, "intMethod")));
        Assert.assertEquals(0L, answer.answer(TestBase.invocationOf(HasPrimitiveMethods.class, "longMethod")));
        Assert.assertEquals(0.0F, answer.answer(TestBase.invocationOf(HasPrimitiveMethods.class, "floatMethod")));
        Assert.assertEquals(0.0, answer.answer(TestBase.invocationOf(HasPrimitiveMethods.class, "doubleMethod")));
    }

    @SuppressWarnings("unused")
    interface Foo {
        ReturnsSmartNullsTest.Foo get();

        ReturnsSmartNullsTest.Foo withArgs(String oneArg, String otherArg);
    }

    @Test
    public void should_return_an_object_that_fails_on_any_method_invocation_for_non_primitives() throws Throwable {
        Answer<Object> answer = new ReturnsSmartNulls();
        ReturnsSmartNullsTest.Foo smartNull = ((ReturnsSmartNullsTest.Foo) (answer.answer(TestBase.invocationOf(ReturnsSmartNullsTest.Foo.class, "get"))));
        try {
            smartNull.get();
            Assert.fail();
        } catch (SmartNullPointerException expected) {
        }
    }

    @Test
    public void should_return_an_object_that_allows_object_methods() throws Throwable {
        Answer<Object> answer = new ReturnsSmartNulls();
        ReturnsSmartNullsTest.Foo smartNull = ((ReturnsSmartNullsTest.Foo) (answer.answer(TestBase.invocationOf(ReturnsSmartNullsTest.Foo.class, "get"))));
        assertThat(smartNull.toString()).contains("SmartNull returned by").contains("foo.get()");
    }

    @Test
    public void should_print_the_parameters_when_calling_a_method_with_args() throws Throwable {
        Answer<Object> answer = new ReturnsSmartNulls();
        ReturnsSmartNullsTest.Foo smartNull = ((ReturnsSmartNullsTest.Foo) (answer.answer(TestBase.invocationOf(ReturnsSmartNullsTest.Foo.class, "withArgs", "oompa", "lumpa"))));
        assertThat(smartNull.toString()).contains("foo.withArgs").contains("oompa").contains("lumpa");
    }

    @Test
    public void should_print_the_parameters_on_SmartNullPointerException_message() throws Throwable {
        Answer<Object> answer = new ReturnsSmartNulls();
        ReturnsSmartNullsTest.Foo smartNull = ((ReturnsSmartNullsTest.Foo) (answer.answer(TestBase.invocationOf(ReturnsSmartNullsTest.Foo.class, "withArgs", "oompa", "lumpa"))));
        try {
            smartNull.get();
            Assert.fail();
        } catch (SmartNullPointerException e) {
            assertThat(e).hasMessageContaining("oompa").hasMessageContaining("lumpa");
        }
    }

    interface GenericFoo<T> {
        T get();
    }

    interface GenericFooBar extends ReturnsSmartNullsTest.GenericFoo<ReturnsSmartNullsTest.Foo> {
        <I> I method();

        <I> I methodWithArgs(int firstArg, I secondArg);

        <I> I methodWithVarArgs(int firstArg, I... secondArg);
    }

    @Test
    public void should_return_an_object_that_has_been_defined_with_class_generic() throws Throwable {
        Answer<Object> answer = new ReturnsSmartNulls();
        ReturnsSmartNullsTest.Foo smartNull = ((ReturnsSmartNullsTest.Foo) (answer.answer(TestBase.invocationOf(ReturnsSmartNullsTest.GenericFooBar.class, "get"))));
        assertThat(smartNull.toString()).contains("SmartNull returned by").contains("genericFooBar.get()");
    }

    @Test
    public void should_return_an_object_that_has_been_defined_with_method_generic() throws Throwable {
        Answer<Object> answer = new ReturnsSmartNulls();
        String smartNull = ((String) (answer.answer(TestBase.invocationOf(ReturnsSmartNullsTest.GenericFooBar.class, "method"))));
        assertThat(smartNull).isNull();
    }

    @Test
    public void should_return_a_String_that_has_been_defined_with_method_generic_and_provided_in_argument() throws Throwable {
        Answer<Object> answer = new ReturnsSmartNulls();
        Object smartNull = answer.answer(ReturnsSmartNullsTest.invocationMethodWithArgs("secondArg"));
        assertThat(smartNull).isNotNull().isInstanceOf(String.class).asString().isEmpty();
    }

    @Test
    public void should_return_a_empty_list_that_has_been_defined_with_method_generic_and_provided_in_argument() throws Throwable {
        final List<String> list = Collections.singletonList("String");
        Answer<Object> answer = new ReturnsSmartNulls();
        Object smartNull = answer.answer(ReturnsSmartNullsTest.invocationMethodWithArgs(list));
        assertThat(smartNull).isNotNull().isInstanceOf(List.class);
        assertThat(((List) (smartNull))).isEmpty();
    }

    @Test
    public void should_return_a_empty_map_that_has_been_defined_with_method_generic_and_provided_in_argument() throws Throwable {
        final Map<String, String> map = new HashMap<String, String>();
        map.put("key-1", "value-1");
        map.put("key-2", "value-2");
        Answer<Object> answer = new ReturnsSmartNulls();
        Object smartNull = answer.answer(ReturnsSmartNullsTest.invocationMethodWithArgs(map));
        assertThat(smartNull).isNotNull().isInstanceOf(Map.class);
        assertThat(((Map) (smartNull))).isEmpty();
    }

    @Test
    public void should_return_a_empty_set_that_has_been_defined_with_method_generic_and_provided_in_argument() throws Throwable {
        Answer<Object> answer = new ReturnsSmartNulls();
        Object smartNull = answer.answer(ReturnsSmartNullsTest.invocationMethodWithArgs(new HashSet<String>(Arrays.asList("set-1", "set-2"))));
        assertThat(smartNull).isNotNull().isInstanceOf(Set.class);
        assertThat(((Set) (smartNull))).isEmpty();
    }

    @Test
    public void should_return_a_new_mock_that_has_been_defined_with_method_generic_and_provided_in_argument() throws Throwable {
        Answer<Object> answer = new ReturnsSmartNulls();
        final ReturnsSmartNullsTest.Foo mock = Mockito.mock(ReturnsSmartNullsTest.Foo.class);
        Object smartNull = answer.answer(ReturnsSmartNullsTest.invocationMethodWithArgs(mock));
        assertThat(smartNull).isNotNull().isNotSameAs(mock);
        assertThat(smartNull.toString()).contains("SmartNull returned by").contains("genericFooBar.methodWithArgs(");
    }

    @Test
    public void should_return_an_Object_that_has_been_defined_with_method_generic_and_provided_in_argument() throws Throwable {
        Answer<Object> answer = new ReturnsSmartNulls();
        Object smartNull = answer.answer(ReturnsSmartNullsTest.invocationMethodWithArgs(new Object() {}));
        assertThat(smartNull.toString()).contains("SmartNull returned by").contains("genericFooBar.methodWithArgs(");
    }

    @Test
    public void should_throw_a_error_on_invocation_of_returned_mock() throws Throwable {
        final Answer<Object> answer = new ReturnsSmartNulls();
        final ReturnsSmartNullsTest.Foo mock = Mockito.mock(ReturnsSmartNullsTest.Foo.class);
        final Throwable throwable = Assertions.catchThrowable(new ThrowableAssert.ThrowingCallable() {
            @Override
            public void call() throws Throwable {
                ((ReturnsSmartNullsTest.Foo) (answer.answer(ReturnsSmartNullsTest.invocationMethodWithArgs(mock)))).get();
            }
        });
        Assertions.assertThat(throwable).isInstanceOf(SmartNullPointerException.class).hasMessageContaining("genericFooBar.methodWithArgs(").hasMessageContaining("1").hasMessageContaining(mock.toString());
    }

    @Test
    public void should_return_a_String_that_has_been_defined_with_method_generic_and_provided_in_var_args() throws Throwable {
        Answer<Object> answer = new ReturnsSmartNulls();
        Object smartNull = answer.answer(ReturnsSmartNullsTest.invocationMethodWithVarArgs(new String[]{ "varArg-1", "varArg-2" }));
        assertThat(smartNull).isNotNull().isInstanceOf(String.class).asString().isEmpty();
    }

    @Test
    public void should_return_a_empty_list_that_has_been_defined_with_method_generic_and_provided_in_var_args() throws Throwable {
        final List<String> arg1 = Collections.singletonList("String");
        final List<String> arg2 = Arrays.asList("str-1", "str-2");
        Answer<Object> answer = new ReturnsSmartNulls();
        Object smartNull = answer.answer(ReturnsSmartNullsTest.invocationMethodWithVarArgs(new List[]{ arg1, arg2 }));
        assertThat(smartNull).isNotNull().isInstanceOf(List.class);
        assertThat(((List) (smartNull))).isEmpty();
    }

    @Test
    public void should_return_a_empty_map_that_has_been_defined_with_method_generic_and_provided_in_var_args() throws Throwable {
        final Map<String, String> map1 = new HashMap<String, String>() {
            {
                put("key-1", "value-1");
                put("key-2", "value-2");
            }
        };
        final Map<String, String> map2 = new HashMap<String, String>() {
            {
                put("key-3", "value-1");
                put("key-4", "value-2");
            }
        };
        Answer<Object> answer = new ReturnsSmartNulls();
        Object smartNull = answer.answer(ReturnsSmartNullsTest.invocationMethodWithVarArgs(new Map[]{ map1, map2 }));
        assertThat(smartNull).isNotNull().isInstanceOf(Map.class);
        assertThat(((Map) (smartNull))).isEmpty();
    }

    @Test
    public void should_return_a_empty_set_that_has_been_defined_with_method_generic_and_provided_in_var_args() throws Throwable {
        final HashSet<String> set1 = new HashSet<String>(Arrays.asList("set-1", "set-2"));
        final HashSet<String> set2 = new HashSet<String>(Arrays.asList("set-1", "set-2"));
        Answer<Object> answer = new ReturnsSmartNulls();
        Object smartNull = answer.answer(ReturnsSmartNullsTest.invocationMethodWithVarArgs(new HashSet[]{ set1, set2 }));
        assertThat(smartNull).isNotNull().isInstanceOf(Set.class);
        assertThat(((Set) (smartNull))).isEmpty();
    }

    @Test
    public void should_return_a_new_mock_that_has_been_defined_with_method_generic_and_provided_in_var_args() throws Throwable {
        Answer<Object> answer = new ReturnsSmartNulls();
        final ReturnsSmartNullsTest.Foo mock1 = Mockito.mock(ReturnsSmartNullsTest.Foo.class);
        final ReturnsSmartNullsTest.Foo mock2 = Mockito.mock(ReturnsSmartNullsTest.Foo.class);
        Object smartNull = answer.answer(ReturnsSmartNullsTest.invocationMethodWithVarArgs(new ReturnsSmartNullsTest.Foo[]{ mock1, mock2 }));
        assertThat(smartNull).isNotNull().isNotSameAs(mock1).isNotSameAs(mock2);
        assertThat(smartNull.toString()).contains("SmartNull returned by").contains("genericFooBar.methodWithVarArgs(");
    }

    @Test
    public void should_return_an_Object_that_has_been_defined_with_method_generic_and_provided_in_var_args() throws Throwable {
        Answer<Object> answer = new ReturnsSmartNulls();
        Object smartNull = answer.answer(ReturnsSmartNullsTest.invocationMethodWithVarArgs(new Object[]{ new Object() {}, new Object() {} }));
        assertThat(smartNull.toString()).contains("SmartNull returned by").contains("genericFooBar.methodWithVarArgs(");
    }
}

