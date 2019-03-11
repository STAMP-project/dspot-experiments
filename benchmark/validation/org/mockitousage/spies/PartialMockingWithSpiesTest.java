/**
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockitousage.spies;


import org.assertj.core.api.Assertions;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockitoutil.Conditions;
import org.mockitoutil.TestBase;


@SuppressWarnings("unchecked")
public class PartialMockingWithSpiesTest extends TestBase {
    class InheritMe {
        private String inherited = "100$";

        protected String getInherited() {
            return inherited;
        }
    }

    class Person extends PartialMockingWithSpiesTest.InheritMe {
        private final PartialMockingWithSpiesTest.Name defaultName = new PartialMockingWithSpiesTest.Name("Default name");

        public String getName() {
            return guessName().name;
        }

        PartialMockingWithSpiesTest.Name guessName() {
            return defaultName;
        }

        public String howMuchDidYouInherit() {
            return getInherited();
        }

        public String getNameButDelegateToMethodThatThrows() {
            throwSomeException();
            return guessName().name;
        }

        private void throwSomeException() {
            throw new RuntimeException("boo");
        }
    }

    class Name {
        private final String name;

        public Name(String name) {
            this.name = name;
        }
    }

    PartialMockingWithSpiesTest.Person spy = Mockito.spy(new PartialMockingWithSpiesTest.Person());

    @Test
    public void shouldCallRealMethdsEvenDelegatedToOtherSelfMethod() {
        // when
        String name = spy.getName();
        // then
        Assert.assertEquals("Default name", name);
    }

    @Test
    public void shouldAllowStubbingOfMethodsThatDelegateToOtherMethods() {
        // when
        Mockito.when(spy.getName()).thenReturn("foo");
        // then
        Assert.assertEquals("foo", spy.getName());
    }

    @Test
    public void shouldAllowStubbingWithThrowablesMethodsThatDelegateToOtherMethods() {
        // when
        Mockito.doThrow(new RuntimeException("appetite for destruction")).when(spy).getNameButDelegateToMethodThatThrows();
        // then
        try {
            spy.getNameButDelegateToMethodThatThrows();
            Assert.fail();
        } catch (Exception e) {
            Assert.assertEquals("appetite for destruction", e.getMessage());
        }
    }

    @Test
    public void shouldStackTraceGetFilteredOnUserExceptions() {
        try {
            // when
            spy.getNameButDelegateToMethodThatThrows();
            Assert.fail();
        } catch (Throwable t) {
            // then
            Assertions.assertThat(t).has(Conditions.methodsInStackTrace("throwSomeException", "getNameButDelegateToMethodThatThrows", "shouldStackTraceGetFilteredOnUserExceptions"));
        }
    }

    @Test
    public void shouldVerify() {
        // when
        spy.getName();
        // then
        Mockito.verify(spy).guessName();
    }

    @Test
    public void shouldStub() {
        // given
        Mockito.when(spy.guessName()).thenReturn(new PartialMockingWithSpiesTest.Name("John"));
        // when
        String name = spy.getName();
        // then
        Assert.assertEquals("John", name);
    }

    @Test
    public void shouldDealWithPrivateFieldsOfSubclasses() {
        Assert.assertEquals("100$", spy.howMuchDidYouInherit());
    }
}

