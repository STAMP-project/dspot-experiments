/**
 * Copyright (c) 2018 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockitousage.stubbing;


import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.mockito.Answers;
import org.mockito.Mockito;


// Reproduces issue #1551
public class SmartNullsGenericBugTest {
    @Test
    public void smart_nulls_generic_bug_generic_T() {
        SmartNullsGenericBugTest.ConcreteDao concreteDao = Mockito.mock(SmartNullsGenericBugTest.ConcreteDao.class, Mockito.withSettings().defaultAnswer(Answers.RETURNS_SMART_NULLS));
        final SmartNullsGenericBugTest.Entity result = concreteDao.findById();
        Assertions.assertThat(result).as("#1551").isNotNull();
    }

    @Test
    public void smart_nulls_generic_bug_generic_M() {
        SmartNullsGenericBugTest.ConcreteDao concreteDao = Mockito.mock(SmartNullsGenericBugTest.ConcreteDao.class, Mockito.withSettings().defaultAnswer(Answers.RETURNS_SMART_NULLS));
        final String other = concreteDao.find();
        Assertions.assertThat(other).as("#1551 - CCannot resolve type").isNull();
    }

    @Test
    public void smart_nulls_generic_bug_generic_M_provided_in_args() {
        SmartNullsGenericBugTest.ConcreteDao concreteDao = Mockito.mock(SmartNullsGenericBugTest.ConcreteDao.class, Mockito.withSettings().defaultAnswer(Answers.RETURNS_SMART_NULLS));
        final String other = concreteDao.findArgs(1, "plop");
        Assertions.assertThat(other).as("#1551").isEqualTo("");
    }

    @Test
    public void smart_nulls_generic_bug_generic_M_provided_as_varargs() {
        SmartNullsGenericBugTest.ConcreteDao concreteDao = Mockito.mock(SmartNullsGenericBugTest.ConcreteDao.class, Mockito.withSettings().defaultAnswer(Answers.RETURNS_SMART_NULLS));
        final String other = concreteDao.findVarargs(42, "plip", "plop");
        Assertions.assertThat(other).as("#1551").isEqualTo("");
    }

    static class AbstractDao<T> {
        T findById() {
            return null;
        }

        <M> M find() {
            return null;
        }

        <M> M findArgs(int idx, M arg) {
            return null;
        }

        <M> M findVarargs(int idx, M... args) {
            return null;
        }
    }

    static class Entity {}

    static class ConcreteDao extends SmartNullsGenericBugTest.AbstractDao<SmartNullsGenericBugTest.Entity> {}
}

