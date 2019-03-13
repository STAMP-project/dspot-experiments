/**
 * Copyright (c) 2017 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockito.internal.creation.instance;


import org.junit.Assert;
import org.junit.Test;
import org.mockito.creation.instance.InstantiationException;
import org.mockitoutil.TestBase;


public class ConstructorInstantiatorTest extends TestBase {
    static class SomeClass {}

    class SomeInnerClass {}

    class ChildOfThis extends ConstructorInstantiatorTest {}

    static class SomeClass2 {
        SomeClass2(String x) {
        }
    }

    static class SomeClass3 {
        SomeClass3(int i) {
        }
    }

    @Test
    public void creates_instances() {
        Assert.assertEquals(new ConstructorInstantiator(false, new Object[0]).newInstance(ConstructorInstantiatorTest.SomeClass.class).getClass(), ConstructorInstantiatorTest.SomeClass.class);
    }

    @Test
    public void creates_instances_of_inner_classes() {
        Assert.assertEquals(new ConstructorInstantiator(true, this).newInstance(ConstructorInstantiatorTest.SomeInnerClass.class).getClass(), ConstructorInstantiatorTest.SomeInnerClass.class);
        Assert.assertEquals(new ConstructorInstantiator(true, new ConstructorInstantiatorTest.ChildOfThis()).newInstance(ConstructorInstantiatorTest.SomeInnerClass.class).getClass(), ConstructorInstantiatorTest.SomeInnerClass.class);
    }

    @Test
    public void creates_instances_with_arguments() {
        Assert.assertEquals(new ConstructorInstantiator(false, "someString").newInstance(ConstructorInstantiatorTest.SomeClass2.class).getClass(), ConstructorInstantiatorTest.SomeClass2.class);
    }

    @Test
    public void creates_instances_with_null_arguments() {
        Assert.assertEquals(new ConstructorInstantiator(false, new Object[]{ null }).newInstance(ConstructorInstantiatorTest.SomeClass2.class).getClass(), ConstructorInstantiatorTest.SomeClass2.class);
    }

    @Test
    public void creates_instances_with_primitive_arguments() {
        Assert.assertEquals(new ConstructorInstantiator(false, 123).newInstance(ConstructorInstantiatorTest.SomeClass3.class).getClass(), ConstructorInstantiatorTest.SomeClass3.class);
    }

    @Test(expected = InstantiationException.class)
    public void fails_when_null_is_passed_for_a_primitive() {
        Assert.assertEquals(new ConstructorInstantiator(false, new Object[]{ null }).newInstance(ConstructorInstantiatorTest.SomeClass3.class).getClass(), ConstructorInstantiatorTest.SomeClass3.class);
    }

    @Test
    public void explains_when_constructor_cannot_be_found() {
        try {
            new ConstructorInstantiator(false, new Object[0]).newInstance(ConstructorInstantiatorTest.SomeClass2.class);
            Assert.fail();
        } catch (org.mockito.creation e) {
            assertThat(e).hasMessageContaining(("Unable to create instance of \'SomeClass2\'.\n" + "Please ensure that the target class has a 0-arg constructor."));
        }
    }
}

