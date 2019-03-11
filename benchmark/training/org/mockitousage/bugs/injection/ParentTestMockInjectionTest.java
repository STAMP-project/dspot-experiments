/**
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockitousage.bugs.injection;


import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


// issue 229 : @Mock fields in super test class are not injected on @InjectMocks fields
public class ParentTestMockInjectionTest {
    @Test
    public void injectMocksShouldInjectMocksFromTestSuperClasses() {
        ParentTestMockInjectionTest.ImplicitTest it = new ParentTestMockInjectionTest.ImplicitTest();
        MockitoAnnotations.initMocks(it);
        Assert.assertNotNull(it.daoFromParent);
        Assert.assertNotNull(it.daoFromSub);
        Assert.assertNotNull(it.sut.daoFromParent);
        Assert.assertNotNull(it.sut.daoFromSub);
    }

    @Ignore
    public abstract static class BaseTest {
        @Mock
        protected ParentTestMockInjectionTest.DaoA daoFromParent;
    }

    @Ignore("JUnit test under test : don't test this!")
    public static class ImplicitTest extends ParentTestMockInjectionTest.BaseTest {
        @InjectMocks
        private ParentTestMockInjectionTest.TestedSystem sut = new ParentTestMockInjectionTest.TestedSystem();

        @Mock
        private ParentTestMockInjectionTest.DaoB daoFromSub;

        @Before
        public void setup() {
            MockitoAnnotations.initMocks(this);
        }

        @Test
        public void noNullPointerException() {
            sut.businessMethod();
        }
    }

    public static class TestedSystem {
        private ParentTestMockInjectionTest.DaoA daoFromParent;

        private ParentTestMockInjectionTest.DaoB daoFromSub;

        public void businessMethod() {
            daoFromParent.doQuery();
            daoFromSub.doQuery();
        }
    }

    public static class DaoA {
        public void doQuery() {
        }
    }

    public static class DaoB {
        public void doQuery() {
        }
    }
}

