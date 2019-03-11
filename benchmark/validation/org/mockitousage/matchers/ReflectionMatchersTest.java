/**
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockitousage.matchers;


import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.exceptions.verification.junit.ArgumentsAreDifferent;
import org.mockitoutil.TestBase;


@SuppressWarnings("all")
public class ReflectionMatchersTest extends TestBase {
    class Parent {
        private int parentField;

        protected String protectedParentField;

        public Parent(int parentField, String protectedParentField) {
            this.parentField = parentField;
            this.protectedParentField = protectedParentField;
        }
    }

    class Child extends ReflectionMatchersTest.Parent {
        private int childFieldOne;

        private Object childFieldTwo;

        public Child(int parentField, String protectedParentField, int childFieldOne, Object childFieldTwo) {
            super(parentField, protectedParentField);
            this.childFieldOne = childFieldOne;
            this.childFieldTwo = childFieldTwo;
        }
    }

    interface MockMe {
        void run(ReflectionMatchersTest.Child child);
    }

    ReflectionMatchersTest.MockMe mock;

    @Test
    public void shouldMatchWhenFieldValuesEqual() throws Exception {
        ReflectionMatchersTest.Child wanted = new ReflectionMatchersTest.Child(1, "foo", 2, "bar");
        Mockito.verify(mock).run(ArgumentMatchers.refEq(wanted));
    }

    @Test(expected = ArgumentsAreDifferent.class)
    public void shouldNotMatchWhenFieldValuesDiffer() throws Exception {
        ReflectionMatchersTest.Child wanted = new ReflectionMatchersTest.Child(1, "foo", 2, "bar XXX");
        Mockito.verify(mock).run(ArgumentMatchers.refEq(wanted));
    }

    @Test(expected = ArgumentsAreDifferent.class)
    public void shouldNotMatchAgain() throws Exception {
        ReflectionMatchersTest.Child wanted = new ReflectionMatchersTest.Child(1, "foo", 999, "bar");
        Mockito.verify(mock).run(ArgumentMatchers.refEq(wanted));
    }

    @Test(expected = ArgumentsAreDifferent.class)
    public void shouldNotMatchYetAgain() throws Exception {
        ReflectionMatchersTest.Child wanted = new ReflectionMatchersTest.Child(1, "XXXXX", 2, "bar");
        Mockito.verify(mock).run(ArgumentMatchers.refEq(wanted));
    }

    @Test(expected = ArgumentsAreDifferent.class)
    public void shouldNotMatch() throws Exception {
        ReflectionMatchersTest.Child wanted = new ReflectionMatchersTest.Child(234234, "foo", 2, "bar");
        Mockito.verify(mock).run(ArgumentMatchers.refEq(wanted));
    }

    @Test
    public void shouldMatchWhenFieldValuesEqualWithOneFieldExcluded() throws Exception {
        ReflectionMatchersTest.Child wanted = new ReflectionMatchersTest.Child(1, "foo", 2, "excluded");
        Mockito.verify(mock).run(ArgumentMatchers.refEq(wanted, "childFieldTwo"));
    }

    @Test
    public void shouldMatchWhenFieldValuesEqualWithTwoFieldsExcluded() throws Exception {
        ReflectionMatchersTest.Child wanted = new ReflectionMatchersTest.Child(234234, "foo", 2, "excluded");
        Mockito.verify(mock).run(ArgumentMatchers.refEq(wanted, "childFieldTwo", "parentField"));
        Mockito.verify(mock).run(ArgumentMatchers.refEq(wanted, "parentField", "childFieldTwo"));
    }

    @Test(expected = ArgumentsAreDifferent.class)
    public void shouldNotMatchWithFieldsExclusion() throws Exception {
        ReflectionMatchersTest.Child wanted = new ReflectionMatchersTest.Child(234234, "foo", 2, "excluded");
        Mockito.verify(mock).run(ArgumentMatchers.refEq(wanted, "childFieldTwo"));
    }
}

